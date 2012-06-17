package uk.co.probablyfine.aoko.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import uk.co.probablyfine.aoko.dao.QueueItemDao;

@Controller
public class HomeController {
	
	@Autowired
	QueueItemDao dao;
	
	@RequestMapping("/")
	public String home(Model m, Principal p) {
		if (null != p) {
			System.out.println("Current logged in user - "+p.getName());
			m.addAttribute("username", p.getName());
		}
		m.addAttribute("queue", dao.getAll());
		return "index";
	}
	
	@RequestMapping("/about")
	public String about() { return "about"; }
	
}