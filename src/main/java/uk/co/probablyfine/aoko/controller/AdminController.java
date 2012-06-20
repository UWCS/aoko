package uk.co.probablyfine.aoko.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import uk.co.probablyfine.aoko.dao.AccountDao;
import uk.co.probablyfine.aoko.player.MusicPlayer;

@Controller
@RequestMapping("/admin/")
public class AdminController {

	@Autowired
	private AccountDao accounts;
	
	@Autowired
	private MusicPlayer player;

	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@RequestMapping("make/{username}") 
	public String setUserAsAdmin(@PathVariable("username") String username) {
		accounts.setUserAsAdmin(username);
		return "redirect:/";
	}
	
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@RequestMapping("kill")
	public String stopPlaying() {
		player.stopTrack();
		return "redirect:/";
	}
	
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@RequestMapping("remove/{username}")
	public String removeUser(@PathVariable("username") String username) {
		//TODO
		return "redirect:/";
	}
	
}