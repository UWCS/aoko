package uk.co.probablyfine.aoko.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import uk.co.probablyfine.aoko.dao.YoutubeDao;

@Controller
@RequestMapping("/youtube/")
public class YoutubeController {

	@Autowired
	YoutubeDao videos;
	
	@RequestMapping("/")
	public String getAllQueued(Model m) {
		m.addAttribute("queued", videos.getAllQueued());
		return "youtube";
	}
	
	@RequestMapping("delete/{id}")
	public String deleteSong(@PathVariable("id") int videoId, Principal p) {
		
		if (null == p) {
			return "redirect:/";
		}
				
		videos.delete(videoId,p.getName());
			
		return "redirect:/";
		
	}
	
}
