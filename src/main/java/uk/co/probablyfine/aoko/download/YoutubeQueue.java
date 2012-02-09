package uk.co.probablyfine.aoko.download;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import uk.co.probablyfine.aoko.dao.MusicFileDao;
import uk.co.probablyfine.aoko.dao.QueueItemDao;
import uk.co.probablyfine.aoko.dao.UserDao;
import uk.co.probablyfine.aoko.dao.YoutubeDao;
import uk.co.probablyfine.aoko.domain.MusicFile;
import uk.co.probablyfine.aoko.domain.User;
import uk.co.probablyfine.aoko.domain.YoutubeDownload;
import uk.co.probablyfine.aoko.util.FileType;

import com.google.common.io.Files;

@Service
public class YoutubeQueue {

	private String path;
	
	@Autowired
	YoutubeDao ytDao;
	
	@Autowired
	MusicFileDao mfDao;
	
	@Autowired
	QueueItemDao qiDao;
	
	@Autowired
	UserDao userDao;
	
	private Thread dlThread;
	
	@PostConstruct
	public void downloadVideos() {
		dlThread = new Thread(new Runnable() {
			@Override
			public void run() {
				YoutubeDownload yd = ytDao.next();
				try {
					int code = Runtime.getRuntime().exec(new String[] {path, "-o", "/var/tmp/"+yd.getId(), yd.getUrl()}).waitFor();
					
					if (code == 0) {
						byte[] hash;
						try {
							//Get the filehash
							hash = Files.getDigest(new File("/var/tmp/"+yd.getId()), MessageDigest.getInstance("SHA1"));
							String hexVal = new BigInteger(hash).toString();
							
							User user = userDao.getFromUsername(yd.getQueuedBy());
							
							MusicFile file;
							
							if (mfDao.containsFile(hexVal)) {
								file = mfDao.getFromUniqueId(hexVal);
							} else {
								Files.move(new File("/var/tmp/"+yd.getId()), new File("/home/media/something"));
								
								file = new MusicFile();
								file.setLocation("/var/tmp/"+yd.getId());
								file.setType(FileType.YOUTUBE);
								file.setUniqueId(hexVal);
							}
							
							qiDao.queueTrack(user, file);
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
