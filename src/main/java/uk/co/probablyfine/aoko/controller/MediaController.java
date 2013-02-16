package uk.co.probablyfine.aoko.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import uk.co.probablyfine.aoko.dao.MusicFileDao;

@Controller
public class MediaController {

	@Autowired private MusicFileDao musicFiles;
	
	@RequestMapping("/media/")
	public String getAllQueuedTracks(Model m) {
		m.addAttribute("tracks", musicFiles.getAll());
		return "media";
		
	}
}
