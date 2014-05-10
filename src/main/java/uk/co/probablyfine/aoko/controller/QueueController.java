package uk.co.probablyfine.aoko.controller;

import java.security.Principal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import uk.co.probablyfine.aoko.dao.AccountDao;
import uk.co.probablyfine.aoko.dao.QueueItemDao;

@Controller
@RequestMapping("/a/")
public class QueueController {

	private final Logger log = LoggerFactory.getLogger(QueueController.class);
	
	@Autowired private QueueItemDao queue;
	@Autowired private AccountDao accounts;
	
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

    @RequestMapping("/delete/{queueItemID}")
    public String deleteSong(@PathVariable("queueItemID") int id, Principal p) {
        if (p == null) return "redirect:/";

        queue.deleteItem(queue.getFromId(id));

        return "redirect:/";
    }
	
}
