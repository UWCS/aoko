package uk.co.probablyfine.aoko.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import uk.co.probablyfine.aoko.dao.AccountDao;
import uk.co.probablyfine.aoko.dao.QueueItemDao;

@Controller
public class HomeController {
	
	@Autowired private QueueItemDao queue;
	@Autowired private AccountDao users;
	
	@RequestMapping("/")
	public String home(Model m, Principal p) {
		if (null != p) {
			System.out.println("Current logged in user - "+p.getName());
			m.addAttribute("username", p.getName());
		}
		m.addAttribute("queue", queue.getAllUnplayed());
		return "index";
	}
	
	@RequestMapping("/about")
	public String about(Model m) { 
		m.addAttribute("admins", users.getAdmins());
		
		return "about"; 
	}
	
}