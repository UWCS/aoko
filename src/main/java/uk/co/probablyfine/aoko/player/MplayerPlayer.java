package uk.co.probablyfine.aoko.player;

import java.util.HashMap;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import uk.co.probablyfine.aoko.dao.QueueItemDao;
import uk.co.probablyfine.aoko.domain.MusicFile;
import uk.co.probablyfine.aoko.domain.QueueItem;
import uk.co.probablyfine.aoko.domain.User;
import uk.co.probablyfine.aoko.util.FileType;

@Service
public class MplayerPlayer {

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
						QueueItem qi = dao.nextTrack();
						if (qi != null) {
							dao.startedPlaying(qi);
							System.out.println(qi.toString());
							dao.finishedPlaying(qi);
						}
						
					} catch (InterruptedException e) {
						e.printStackTrace();
					}

					
				}
				
			}
		}).start();
		
		
		
		
	}
	
}
