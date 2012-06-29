package uk.co.probablyfine.aoko.dao;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.co.probablyfine.aoko.domain.Account;
import uk.co.probablyfine.aoko.domain.Account_;
import uk.co.probablyfine.aoko.domain.PlayerState;
import uk.co.probablyfine.aoko.domain.QueueItem;

@Repository
public class AccountDao {

	private final Logger log = LoggerFactory.getLogger(AccountDao.class);
	
	@PersistenceContext
	EntityManager em;
	
	@Transactional(readOnly = true)
	public Account getFromUsername(String username) {
		log.debug("getFromUsername - Getting user - {}",username);

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Account> cq = cb.createQuery(Account.class);
		Root<Account> root = cq.from(Account.class);
		
		cq.where(cb.equal(root.get(Account_.username), username));
		
		try {
			Account a = em.createQuery(cq).setMaxResults(1).getSingleResult();
			log.debug("returning value - {}",a.getUsername());
			return a;
			
		} catch(Exception e) {
			log.error("Exception",e);
			log.error("returning null");
			return null;
		}
		
	}
	
	@Transactional
	public void merge(Account user) {
		em.merge(user);
	}

	@Transactional
	public void setUserAsAdmin(String username) {
		final Account account = getFromUsername(username);
		account.setRole("ROLE_ADMIN");
		em.merge(account);
	}
}
