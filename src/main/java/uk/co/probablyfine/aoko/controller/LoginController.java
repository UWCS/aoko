package uk.co.probablyfine.aoko.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import uk.co.probablyfine.aoko.dao.AccountDao;
import uk.co.probablyfine.aoko.domain.Account;

@Controller
@RequestMapping("/login/")
public class LoginController {

	@Autowired
	AccountDao users;
	
	@Autowired
	PasswordEncoder pass;
	
	@RequestMapping("/")
	public String home() {
		return "login";
	}
	
	@RequestMapping("register")
	public String register() {
		return "register";
	}
	
	@RequestMapping(value = "register",method = RequestMethod.POST)
	public String processNewUser(@RequestParam String j_username, @RequestParam String j_password, Model m) {
		
		if (users.getFromUsername(j_username) == null) {
			Account user = new Account(j_username,pass.encode(j_password), "ROLE_USER");
			users.merge(user);
			m.addAttribute("register", "Succesfully registered, can now log in");
		} else {
			m.addAttribute("error", "Username already exists, pick another!");
			return "redirect:/login/register";
		}
		
		return "redirect:/login/";
	}
	
}
