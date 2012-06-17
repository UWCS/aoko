package uk.co.probablyfine.aoko.controller;

import java.security.Principal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import uk.co.probablyfine.aoko.dao.AccountDao;
import uk.co.probablyfine.aoko.dao.QueueItemDao;

@Controller
@RequestMapping("/a/")
public class QueueController {

	private final Logger log = LoggerFactory.getLogger(QueueController.class);
	
	@Autowired
	QueueItemDao queue;
	
	@Autowired
	AccountDao accounts;
	
	@RequestMapping("move/{direction}/{id}")
	public String moveSong(	@PathVariable("direction") String direction,
							@PathVariable("id") int bucketId, Principal p) {
		if (null == p) {
			log.debug("User not logged in, redirecting to home");
			return "redirect:/";
		}
			log.debug("User = {}, Bucket = {}, Direction = {}", new Object[] {p.getName(),bucketId,direction });
			
		if (direction.matches("up")) {
			queue.shiftUp(p.getName(), bucketId);
		} else if (direction.matches("down")) {
			queue.shiftDown(p.getName(), bucketId);
		} else {

		}
		
		log.debug("Returning user to homepage");
		return "redirect:/";
		
	}
	
	@RequestMapping("delete/{id}")
	public String deleteSong(@PathVariable("id") int bucketId, Principal p) {
		
		if (null == p) {
			log.debug("User not logged in, redirecting to home");
			return "redirect:/";
		}
				
		queue.deleteItem(bucketId,p.getName());
			
		log.debug("Returning user to homepage");
		
		return "redirect:/";
		
	}
	
	@RequestMapping("admin/{username}") 
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public String setUserAsAdmin(@PathVariable("username") String username) {
		
		accounts.setUserAsAdmin(username);
		
		return "redirect:/";
		
	}
	
	
	
}
