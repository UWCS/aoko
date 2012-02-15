package uk.co.probablyfine.aoko.download;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
	protected YouTubeArtDownloader artDownloader;
	
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
						File tempDir = Files.createTempDir();
						String outputFormat = tempDir.getAbsolutePath()+File.separator+"%(stitle)s.%(ext)s";
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
								File downloadedFile = tempDir.listFiles()[0];
								hash = Files.getDigest(downloadedFile, MessageDigest.getInstance("SHA1"));
								String hexVal = new BigInteger(hash).toString(16);
								Account user = userDao.getFromUsername(yd.getQueuedBy());
								
								MusicFile file;
								
								if (mfDao.containsFile(hexVal)) {
									file = mfDao.getFromUniqueId(hexVal);
								} else {
									File newFile = new File(mediaPath+downloadedFile.getName());
									Files.move(downloadedFile, newFile);
									
									tempDir.delete();
									
									file = new MusicFile();
									
									//TRYING TO GET FILE ART HERE
									Pattern pat = Pattern.compile("(?<=v=).*?(?=&|$)");
									Matcher m = pat.matcher(yd.getUrl());
									m.find();
									String id = m.group(0);
									
									try {
										artDownloader.getAlbumArt(id);
										file.setArtLocation(artPath+id+".jpg");
									} catch (Exception e) {
										System.out.println(" "+e);
									}
									
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
