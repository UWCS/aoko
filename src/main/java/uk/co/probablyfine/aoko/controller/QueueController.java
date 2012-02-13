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
	
	@RequestMapping("move/{direction}/{id}")
	public String moveSong(	@PathVariable("direction") String direction,
							@PathVariable("id") int bucketId, Principal p) {
		if (null == p) {
			System.out.println("Not logged in");
			return "redirect:/";
		}
			
		
		System.out.println("Direction = "+direction);
		System.out.println("Bucket id = "+bucketId);
			
		if (direction.matches("up")) {
			System.out.println("Shifting up");
			qiDao.shiftUp(p.getName(), bucketId);
		} else if (direction.matches("down")) {
			System.out.println("Shifting down");
			qiDao.shiftDown(p.getName(), bucketId);
		} else {
			System.out.println(":(");
			//Do nothing
		}
		
		return "redirect:/";
		
		
	}
	
	
}
