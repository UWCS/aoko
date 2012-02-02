package uk.co.probablyfine.aoko.player;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import uk.co.probablyfine.aoko.dao.QueueItemDao;

@Service
public class MplayerPlayer {

	@Autowired
	public QueueItemDao dao;
	
}
