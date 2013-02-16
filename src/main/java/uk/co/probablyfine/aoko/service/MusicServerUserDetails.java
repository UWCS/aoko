package uk.co.probablyfine.aoko.service;

import javax.persistence.NoResultException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import uk.co.probablyfine.aoko.dao.AccountDao;

@Service
public class MusicServerUserDetails implements UserDetailsService {

	@Autowired private AccountDao users;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		try {
			return users.getFromUsername(username).toUser();
		} catch (NoResultException e) {
			throw new UsernameNotFoundException(username, e);
		}
	}
}