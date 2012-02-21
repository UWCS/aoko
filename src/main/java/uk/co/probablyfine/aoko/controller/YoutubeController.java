package uk.co.probablyfine.aoko.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import uk.co.probablyfine.aoko.dao.YoutubeDao;

@Controller

public class YoutubeController {

	@Autowired
	YoutubeDao ytDao;
	@RequestMapping("/youtube/")
	public String getAllQueued(Model m) {
	
		m.addAttribute("queued", ytDao.getAllQueued());
		
		return "youtube";
	}
	
}
