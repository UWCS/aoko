package uk.co.probablyfine.aoko.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import uk.co.probablyfine.aoko.dao.QueueItemDao;
import uk.co.probablyfine.aoko.domain.QueueItem;

@Controller
@RequestMapping("/a/")
public class QueueController {

	@Autowired
	QueueItemDao qiDao;
	
	@RequestMapping("/move/${direction}/${id}")
	public void moveSong(	@PathVariable("direction") String direction,
							@PathVariable("id") int trackId, Principal p) {
		if (null == p)
			return;
			
		if (direction == "up") {
			QueueItem qi = qiDao.getFromId(trackId);	
			qiDao.shiftUp(p.getName(), qi.getBucket());
		} else if (direction == "down") {
			QueueItem qi = qiDao.getFromId(trackId);	
			qiDao.shiftDown(p.getName(), qi.getBucket());
		} else {
			//Do nothing
		}
		
		
		
	}
	
	
}
