package uk.co.probablyfine.aoko.controller;

import java.security.Principal;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/a/")
public class QueueController {

	public void moveSongUp(String trackId, Principal p) {
		if (null == p) 
			return;
		
		String currentUser = p.getName();
		
		
		
		
	}
	
	
}
