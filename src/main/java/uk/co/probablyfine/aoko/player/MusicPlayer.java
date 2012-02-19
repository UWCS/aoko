package uk.co.probablyfine.aoko.player;

import java.io.IOException;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import uk.co.probablyfine.aoko.dao.QueueItemDao;
import uk.co.probablyfine.aoko.domain.QueueItem;

@Service
public class MusicPlayer {

	@Value("${path.player}")
	String playerPath;
	
	@Autowired
	QueueItemDao qiDao;
	
	@PostConstruct
	public void play() throws InterruptedException {
		new Thread(new Runnable() {
			@Override
			public void run() {
				while (true) {
					try {
						Thread.sleep(2000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					QueueItem qi = qiDao.nextTrack();
					if (qi != null)
						playTrack(qi);
				}
			}
		}).start();
	}
	
	public void playTrack(QueueItem qi) {
		try {
			
			qiDao.startedPlaying(qi);
			Runtime.getRuntime().exec(new String[] {playerPath, qi.getFile().getLocation()}).waitFor();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			qiDao.finishedPlaying(qi);
		}
	}
	
}