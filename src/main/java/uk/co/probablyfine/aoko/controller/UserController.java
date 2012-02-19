package uk.co.probablyfine.aoko.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import uk.co.probablyfine.aoko.dao.AccountDao;
import uk.co.probablyfine.aoko.dao.QueueItemDao;
import uk.co.probablyfine.aoko.domain.Account;
import uk.co.probablyfine.aoko.domain.QueueItem;

@Controller
@RequestMapping("/user/")
public class UserController {

	@Autowired
	QueueItemDao qiDao;
	
	@Autowired
	AccountDao acDao;
	
	@RequestMapping("{username}")
	public String getAllFromUser(@RequestParam String username, Model m) {
		
		if (acDao.getFromUsername(username) == null) {
			m.addAttribute("error", username+" is not a registered user.");
			
		} else {
			List<QueueItem> queued = qiDao.allQueuedByUser(acDao.getFromUsername(username));
			if (queued != null) {
				m.addAttribute("queued", queued);
				m.addAttribute("user",username);
			} else {
				m.addAttribute("error", "No uploads found from this user");
			}
		}
		
		
		return "foo";
	}
	
}
