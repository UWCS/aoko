package uk.co.probablyfine.aoko.controller;

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
	public String home(Model m) {
		m = m.addAttribute("queue", dao.getAll());
		return "index";
	}
	
}
