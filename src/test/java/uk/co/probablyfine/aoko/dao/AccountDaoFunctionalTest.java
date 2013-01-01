package uk.co.probablyfine.aoko.dao;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import uk.co.probablyfine.aoko.domain.Account;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:test-application-context.xml"})
public class AccountDaoFunctionalTest {

	private static final String USERNAME = "foo";

	@Autowired AccountDao accountDao;
	
	@Test public void testAdministering() {
		Account account = new Account(USERNAME, "bar", "ROLE_USER");
		accountDao.merge(account);
		
		account = accountDao.getFromUsername(USERNAME);
		assertEquals("ROLE_USER", account.getRole());
		
		accountDao.setUserAsAdmin(USERNAME);
		
		List<Account> admins = new ArrayList<Account>(accountDao.getAdmins());
		assertEquals(1, admins.size());
		assertEquals("ROLE_ADMIN", admins.get(0).getRole());
		
	}
	
}
