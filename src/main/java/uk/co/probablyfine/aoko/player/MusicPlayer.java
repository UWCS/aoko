package uk.co.probablyfine.aoko.player;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import uk.co.probablyfine.aoko.dao.QueueItemDao;
import uk.co.probablyfine.aoko.domain.QueueItem;

@Service
public class MusicPlayer {

	private final Logger log = LoggerFactory.getLogger(MusicPlayer.class);
	
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
		log.debug("Trying to play track - {} - {}",qi.getFile().getLocation(), qi.getFile().getMetaData().get("originalname"));
		try {
			qiDao.startedPlaying(qi);
			Process p = Runtime.getRuntime().exec(new String[] {playerPath, qi.getFile().getLocation()});
			
			BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
			
			String line;
			
			while ((line = reader.readLine()) != null) {
				log.trace(line);
			}
			
			int code = p.waitFor();
			
			log.debug("Player exited with code = {}",code);
			
		} catch (IOException e) {
			log.error("IOException: ",e);
		} catch (InterruptedException e) {
			log.error("InterruptedException: ",e);
		} finally {
			qiDao.finishedPlaying(qi);
		}
	}
	
}