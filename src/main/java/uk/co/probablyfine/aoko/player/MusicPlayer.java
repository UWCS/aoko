package uk.co.probablyfine.aoko.player;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.google.common.io.Files;

import uk.co.probablyfine.aoko.dao.QueueItemDao;
import uk.co.probablyfine.aoko.domain.Account;
import uk.co.probablyfine.aoko.domain.FileType;
import uk.co.probablyfine.aoko.domain.MusicFile;
import uk.co.probablyfine.aoko.domain.PlayerState;
import uk.co.probablyfine.aoko.domain.QueueItem;

@Service
public class MusicPlayer {

	@Value("#{settings['path.player']}")
	String playerPath;
	
	@Autowired
	QueueItemDao qiDao;
	
	@PostConstruct
	public void playTracks() throws InterruptedException {
						
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				while (true) {
					QueueItem qi = qiDao.nextTrack();
					if (qi != null) {
						try {
							Thread.sleep(2000);
							
							qiDao.startedPlaying(qi);
							System.out.println(Arrays.toString(new String[] {playerPath, qi.getFile().getLocation()}));
							Runtime.getRuntime().exec(new String[] {playerPath, qi.getFile().getLocation()}).waitFor();
							
						} catch (InterruptedException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						} finally {
							qiDao.finishedPlaying(qi);
						}
					}

				}
				
			}
		}).start();
		
	}
	
}