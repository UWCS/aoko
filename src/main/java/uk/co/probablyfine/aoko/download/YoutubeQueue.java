package uk.co.probablyfine.aoko.download;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
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

import com.google.common.io.Files;

@Service
public class YoutubeQueue {

	private final Logger log = LoggerFactory.getLogger(YoutubeQueue.class);
	
	@Value("${script.youtubedl}")
	String ytdPath;
	
	@Value("${media.repository}")
	String mediaPath;
	
	@Value("${media.art}")
	String artPath;
	
	@Autowired
	YoutubeDao ytDao;
	
	@Autowired
	MusicFileDao mfDao;
	
	@Autowired
	QueueItemDao qiDao;
	
	@Autowired
	AccountDao userDao;
	
	private Thread dlThread;

	@Autowired
	protected ArtDownloader artDownloader;
	
	@PostConstruct
	public void downloadVideos() {
		dlThread = new Thread(new Runnable() {
			@Override
			public void run() {
				while (true) {
					try {
						Thread.sleep(2000);
					} catch (InterruptedException e1) {
						e1.printStackTrace();
					}
					
					YoutubeDownload yd = ytDao.next();
					if (null != yd) {
					
					log.debug("Attempting to download {}",yd.getUrl());
					try {
						//Save file to <media-download-path>\<title>.<format>
						File tempDir = Files.createTempDir();
						String outputFormat = tempDir.getAbsolutePath()+File.separator+"%(stitle)s.%(ext)s";
						Process p = Runtime.getRuntime().exec(new String[] {ytdPath, "-o", outputFormat, yd.getUrl()});
						
						BufferedReader outputReader = new BufferedReader(new InputStreamReader(p.getInputStream()));
						
						String outputLine;
						while ((outputLine = outputReader.readLine()) != null) {
							log.trace(outputLine);
						}
						
						int code = p.waitFor();
						
						if (code == 0) {
							byte[] hash;
							try {
								//Get the filehash
								File downloadedFile = tempDir.listFiles()[0];
								hash = Files.getDigest(downloadedFile, MessageDigest.getInstance("SHA1"));
								String hexVal = new BigInteger(hash).toString(16);
							
								log.debug("{} has hash {}",downloadedFile.getName(),hexVal);
								
								Account user = userDao.getFromUsername(yd.getQueuedBy());
								
								MusicFile file;
								
								if (mfDao.containsFile(hexVal)) {
									file = mfDao.getFromUniqueId(hexVal);
								} else {
									
									final String extension = downloadedFile.getName().substring(downloadedFile.getName().lastIndexOf("."),downloadedFile.getName().length());
									
									File newFile = new File(mediaPath+hexVal+extension);
									Files.move(downloadedFile, newFile);
									
									tempDir.delete();
									
									file = new MusicFile();
									
									Matcher m = Pattern.compile("(?<=v=).*?(?=&|$)").matcher(yd.getUrl());
									m.find();
									String id = m.group(0);
									
									try {
										artDownloader.getYoutubeArt(id);
										file.setArtLocation(id+".jpg");
									} catch (Exception e) {
										log.error("Exception: ",e);
									}
									
									Map<String,String> data = new HashMap<String, String>();
									data.put("originalname", downloadedFile.getName());
									
									String actualName = downloadedFile.getName().substring(0,downloadedFile.getName().lastIndexOf(".")).replace("_", " ");
									
									data.put("name", actualName);
									file.setLocation(newFile.getName());
									file.setMetaData(data);
									file.setType(FileType.YOUTUBE);
									file.setUniqueId(hexVal);
								}
								
								qiDao.queueTrack(user, file);
								ytDao.dlSuccess(yd);
							} catch (NoSuchAlgorithmException e) {
								log.error("No such algorithm. ",e);
															
							} 
			
							
						} else {
							ytDao.dlFail(yd);
						}
					} catch (IOException e) {
						log.error("IOException: ",e);
						ytDao.dlFail(yd);
					} catch (InterruptedException e) {
						log.error("Thread was interrupted: ",e);
						ytDao.dlFail(yd);
					}
					
				}
			}
			
		}
		});
		dlThread.start();
	}
	
	public void stopDownloader() {
		log.debug("Stopping the downloader.");
		dlThread.interrupt();
	}
	
	public void startDownloader() {
		log.debug("Starting the downloader.");
		dlThread.start();
	}
	
}
