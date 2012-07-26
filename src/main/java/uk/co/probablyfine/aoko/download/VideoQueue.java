package uk.co.probablyfine.aoko.download;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import uk.co.probablyfine.aoko.dao.AccountDao;
import uk.co.probablyfine.aoko.dao.MusicFileDao;
import uk.co.probablyfine.aoko.dao.QueueItemDao;
import uk.co.probablyfine.aoko.dao.YoutubeDao;
import uk.co.probablyfine.aoko.domain.Account;
import uk.co.probablyfine.aoko.domain.FileType;
import uk.co.probablyfine.aoko.domain.MusicFile;
import uk.co.probablyfine.aoko.domain.YoutubeDownload;

import com.google.common.collect.Maps;
import com.google.common.io.Files;

@Service
public class VideoQueue {

	private final Logger log = LoggerFactory.getLogger(VideoQueue.class);
	
	@Value("${script.youtubedl}")
	String scriptPath;
	
	@Value("${script.dltimeout}")
	int timeout;
	
	@Value("${media.repository}")
	String mediaPath;
	
	@Value("${media.art}")
	String artPath;
	
	@Autowired
	YoutubeDao videos;
	
	@Autowired
	MusicFileDao musicFiles;
	
	@Autowired
	QueueItemDao queue;
	
	@Autowired
	AccountDao users;
	
	@Autowired
	ApiExtractor api;
	
	private Process downloaderProcess;

	@Autowired
	protected ArtDownloader artDownloader;
	
	private ExecutorService executor;
	
	@PostConstruct
	public void downloadVideos() {
		
		this.executor = Executors.newSingleThreadExecutor();
		
		final Timer downloadTimer = new Timer();
		
		final TimerTask downloadTask = new TimerTask() {
			@Override
			public void run() {
				final YoutubeDownload qi = videos.next();
				if (qi != null)
					download(qi);
			}
		};
		
		downloadTimer.schedule(downloadTask, 0, 2000);
		
	}
	
	public void download(final YoutubeDownload download) {
	
		log.debug("Attempting to download {}",download.getUrl());
		
		try {
			//Save file to <media-download-path>\<title>.<format>
			final File tempDir = Files.createTempDir();
			final String outputFormat = new File(tempDir,"%(stitle)s.%(ext)s").getAbsolutePath();
						
			videos.markStartDownloading(download);
			
			int code = executor.submit(new Callable<Integer>() {

				@Override
				public Integer call() throws Exception {
					
					downloaderProcess = Runtime.getRuntime().exec(new String[] {scriptPath, "-o", outputFormat, download.getUrl()});
					
					BufferedReader outputReader = new BufferedReader(new InputStreamReader(downloaderProcess.getInputStream()));
								
					String outputLine;
					
					while ((outputLine = outputReader.readLine()) != null) {
							log.trace(outputLine);
					}
					
					return downloaderProcess.waitFor();
					
				}
				
			}).get(timeout, TimeUnit.SECONDS);
			
			if (code == 0) {
				
				final MusicFile file;
				
				final FileType type;
				
				final String videoCode;
				
				//Identify what type of video we're dealing with
				if (download.getUrl().contains("vimeo")) {
					log.debug("Processing vimeo url");
					type = FileType.VIMEO;
					
					final Matcher m = Pattern.compile(".*/([0-9]+)$").matcher(download.getUrl());
					m.find();
					videoCode = m.group(1);
				} else {
					log.debug("Processing youtube url");
					type = FileType.YOUTUBE;
					
					final Matcher m = Pattern.compile("(?<=v=).*?(?=&|$)").matcher(download.getUrl());
					m.find();
					videoCode = m.group(0);
				}
				
				log.debug("{} has identifier {}",download.getUrl(),videoCode);
								
				final Account user = users.getFromUsername(download.getQueuedBy());
								
				if (musicFiles.containsFile(videoCode)) {
					file = musicFiles.getFromUniqueId(videoCode);
				} else {
					
					final File downloadedFile = tempDir.listFiles()[0];
					
					final String extension = downloadedFile.getName().substring(downloadedFile.getName().lastIndexOf("."),downloadedFile.getName().length());
					File newFile = new File(mediaPath,videoCode+extension);
					Files.move(downloadedFile, newFile);
									
					tempDir.delete();
					
					file = new MusicFile();
					
					Map<String,String> data;
					
					if (type == FileType.VIMEO) {

						data = api.getVimeoData(videoCode);
						if (data.containsKey("artlocation"))
								file.setArtLocation(videoCode+".jpg");
						
					} else {
					
						data = Maps.newHashMap();
						
						try {
							artDownloader.getYoutubeArt(videoCode);
							file.setArtLocation(videoCode+".jpg");
							String actualName = downloadedFile.getName().substring(0,downloadedFile.getName().lastIndexOf("."));
							data.put("name", actualName);
						} catch (Exception e) {
							log.error("Cannot download thumbnail for {} ",download.getUrl(),e);
						}

					}
					
					data.put("originalname", downloadedFile.getName());
					
					file.setType(type);
					file.setLocation(newFile.getName());
					file.setMetaData(data);
					file.setUniqueId(videoCode);
				}
								
				queue.queueTrack(user, file);
				videos.markSuccessful(download); 
			} else {
				videos.markFailure(download);
			}
		} catch (IOException e) {
			log.error("IOException: ",e);
			videos.markFailure(download);
		} catch (InterruptedException e) {
			log.error("Thread was interrupted: ",e);
			videos.markFailure(download);
		} catch (Exception e) {
			log.error("Unanticipated error, failure.",e);
			videos.markFailure(download);
		}
					
	}
	
	public void stopDownloader() {
		log.debug("Stopping the downloader.");
		downloaderProcess.destroy();
	}
	
}
