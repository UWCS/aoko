package uk.co.probablyfine.aoko.player;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import uk.co.probablyfine.aoko.dao.QueueItemDao;

@Service
public class MplayerPlayer {

	@Autowired
	public QueueItemDao dao;
	
	@PostConstruct
	public void playTracks() throws InterruptedException {
		
		while (true) {
			Thread.sleep(2000);
			

			
		}
		
		
	}
	
}
