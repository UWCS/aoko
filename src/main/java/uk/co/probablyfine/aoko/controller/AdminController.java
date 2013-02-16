package uk.co.probablyfine.aoko.controller;

import java.security.Principal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import uk.co.probablyfine.aoko.dao.AccountDao;
import uk.co.probablyfine.aoko.player.MusicPlayer;

@Controller
@RequestMapping("/admin/")
@PreAuthorize("hasRole('ROLE_ADMIN')")
public class AdminController {

	@Autowired private AccountDao accounts;
	
	@Autowired private MusicPlayer player;

	private final Logger log = LoggerFactory.getLogger(AdminController.class);

	@RequestMapping("make/{username}") 
	public String setUserAsAdmin(@PathVariable("username") String username, Principal p) {
		log.debug("{} is setting {} as an admin",p.getName(),username);
		accounts.setUserAsAdmin(username);
		return "redirect:/";
	}
	
	@RequestMapping("kill")
	public String stopPlaying(Principal p) {
		log.debug("{} has attempted to stop the currently playing track");
		player.stopTrack();
		return "redirect:/";
	}
	
	@RequestMapping("remove/{username}")
	public String removeUser(@PathVariable("username") String username) {
		return "redirect:/";
	}
	
}