package uk.co.probablyfine.aoko.player;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import uk.co.probablyfine.aoko.dao.QueueItemDao;
import uk.co.probablyfine.aoko.domain.QueueItem;

@Service
public class MusicPlayer {

	@Value("#{settings['path.player']}")
	String playerPath;
	
	@Autowired
	QueueItemDao qiDao;
	
	//@PostConstruct
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