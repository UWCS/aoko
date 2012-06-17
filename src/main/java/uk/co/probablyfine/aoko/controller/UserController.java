package uk.co.probablyfine.aoko.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import uk.co.probablyfine.aoko.dao.AccountDao;
import uk.co.probablyfine.aoko.dao.QueueItemDao;
import uk.co.probablyfine.aoko.domain.QueueItem;

@Controller
public class UserController {

	@Autowired
	QueueItemDao qiDao;
	
	@Autowired
	AccountDao acDao;
	
	@RequestMapping("/user/{username}")
	public String getAllFromUser(@PathVariable("username") String username, Model m) {
		
		if (acDao.getFromUsername(username) == null) {
			m.addAttribute("error", username+" is not a registered user.");
			
		} else {
			List<QueueItem> queued = qiDao.allQueuedByUser(acDao.getFromUsername(username));
			if (queued != null) {
				System.out.println(queued);
				m.addAttribute("queued", queued);
				m.addAttribute("user",username);
			} else {
				m.addAttribute("error", "No uploads found from this user");
			}
		}
		
		return "userlist";
	}

	
}
