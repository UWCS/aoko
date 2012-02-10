package uk.co.probablyfine.aoko.player;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import uk.co.probablyfine.aoko.dao.QueueItemDao;
import uk.co.probablyfine.aoko.domain.QueueItem;

@Service
public class MplayerPlayer {

	@Value("#{settings['media.downloadtarget']}")
	String test;
	
	@Autowired
	QueueItemDao dao;
	
	@PostConstruct
	public void playTracks() throws InterruptedException {
						
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				while (true) {
					try {
						Thread.sleep(2000);
						System.out.println(test);
						/*QueueItem qi = dao.nextTrack();
						if (qi != null) {
							dao.startedPlaying(qi);
							System.out.println(qi.toString());
							dao.finishedPlaying(qi);
						}*/
						
					} catch (InterruptedException e) {
						e.printStackTrace();
					}

					
				}
				
			}
		}).start();
		
		
		
		
	}
	
}
