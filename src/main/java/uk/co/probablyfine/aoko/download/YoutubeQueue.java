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

import javax.annotation.PostConstruct;

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

	@Value("#{settings['script.youtubedl']}")
	String ytdPath;
	
	@Value("#{settings['media.downloadtarget']}")
	String downloadPath;
	
	@Value("#{settings['media.repository']}")
	String mediaPath;
	
	@Autowired
	YoutubeDao ytDao;
	
	@Autowired
	MusicFileDao mfDao;
	
	@Autowired
	QueueItemDao qiDao;
	
	@Autowired
	AccountDao userDao;
	
	private Thread dlThread;
	
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
					
					System.out.println("Getting "+yd.getUrl());
					try {
						//Save file to <media-download-path>\<title>.<format>
						String outputFormat = downloadPath+"%(stitle)s.%(ext)s";
						Process p = Runtime.getRuntime().exec(new String[] {"python", ytdPath, "-o", outputFormat, yd.getUrl()});
						
						BufferedReader foo = new BufferedReader(new InputStreamReader(p.getInputStream()));
						
						String bar;
						while ((bar = foo.readLine()) != null) {
							System.out.println(bar);
						}
						
						int code = p.waitFor();
						
						if (code == 0) {
							byte[] hash;
							try {
								//Get the filehash
								File downloadedFile = new File(downloadPath).listFiles()[0];
								System.out.println(downloadedFile.getAbsolutePath());
								hash = Files.getDigest(downloadedFile, MessageDigest.getInstance("SHA1"));
								String hexVal = new BigInteger(hash).toString(16);
								System.out.println(hexVal);
								
								Account user = userDao.getFromUsername(yd.getQueuedBy());
								
								MusicFile file;
								
								if (mfDao.containsFile(hexVal)) {
									file = mfDao.getFromUniqueId(hexVal);
								} else {
									File newFile = new File(mediaPath+downloadedFile.getName());
									Files.move(downloadedFile, newFile);
									System.out.println(mediaPath+downloadedFile.getName());
									file = new MusicFile();
									
									Map<String,String> data = new HashMap<String, String>();
									data.put("originalname", downloadedFile.getName());
									file.setLocation(mediaPath+downloadedFile.getName());
									file.setMetaData(data);
									file.setType(FileType.YOUTUBE);
									file.setUniqueId(hexVal);
								}
								
								qiDao.queueTrack(user, file);
								ytDao.dlSuccess(yd);
							} catch (NoSuchAlgorithmException e) {
								System.out.println("Fucked");
															
							}
			
							
						} else {
							ytDao.dlFail(yd);
						}
					} catch (IOException e) {
						System.out.println("SOMETHING WENT HORRIBLY WRONG.");
						ytDao.dlFail(yd);
						return;
					} catch (InterruptedException e) {
						System.out.println("INTERRUPTED.");
						ytDao.dlFail(yd);
						return;
					}
					
				}
			}
			
		}
		});
		dlThread.start();
	}
	
	public void stopDownloader() {
		dlThread.interrupt();
	}
	
	public void startDownloader() {
		dlThread.start();
	}
	
}
