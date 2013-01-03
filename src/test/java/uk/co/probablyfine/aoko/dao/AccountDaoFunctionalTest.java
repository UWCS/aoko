package uk.co.probablyfine.aoko.dao;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import uk.co.probablyfine.aoko.domain.Account;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:test-application-context.xml"})
@Transactional
public class AccountDaoFunctionalTest {

	private static final String USERNAME = "foo";

	@Autowired AccountDao accountDao;
	
	@Test public void testUpdatePrivileges() {
		Account account = new Account(USERNAME, "bar", "ROLE_USER");
		accountDao.merge(account);
		
		accountDao.setUserAsAdmin(USERNAME);
		account = accountDao.getFromUsername(USERNAME);
		
		assertEquals("ROLE_ADMIN", account.getRole());
		
		accountDao.removeAdmin(USERNAME);
		account = accountDao.getFromUsername(USERNAME);

		assertEquals("ROLE_USER", account.getRole());
	}
	
	@Test public void testGetAdminsList() {
		Account user = new Account(USERNAME, "bar", "ROLE_USER");
		Account admin = new Account("ADMIN", "bar", "ROLE_ADMIN");
		accountDao.merge(user);
		accountDao.merge(admin);
		
		List<Account> admins = new ArrayList<Account>(accountDao.getAdmins());
		assertEquals(1, admins.size());
		assertEquals(admin.getUsername(), admins.get(0).getUsername());
	}
	
}
