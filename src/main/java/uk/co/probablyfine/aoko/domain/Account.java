package uk.co.probablyfine.aoko.domain;

import java.util.Collections;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Entity
public class Account {

	public Account() {}
	
	public Account(String username, String password) {
		this.username = username;
		this.password = password;
	}
	
	@Id
	@GeneratedValue
	private int id;
	
	@Column(nullable = false)
	private String username;
	
	@Column(nullable = false)
	private String password;
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public UserDetails toUser() {
		//Namespace collision :(
		return new org.springframework.security.core.userdetails.User(username, password, Collections.<GrantedAuthority>emptySet());
	}
	
}