package uk.co.probablyfine.aoko.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import uk.co.probablyfine.aoko.player.MusicPlayer;

@RequestMapping("/admin/")
@PreAuthorize("hasRole('ROLE_ADMIN')")
public class AdminController {

	@Autowired
	private AdminController accounts;
	
	@Autowired
	private MusicPlayer player;

	@RequestMapping("make/{username}") 
	public String setUserAsAdmin(@PathVariable("username") String username) {
		accounts.setUserAsAdmin(username);
		return "redirect:/";
		
	}
	
	@RequestMapping("kill")
	public String stopPlaying() {
		player.stopTrack();
		return "redirect:/";
	}
	
	@RequestMapping("remove/{username}")
	public String removeUser(@PathVariable("username") String username) {
		accounts.removeUser(username);
		return "redirect:/";
	}
	
}