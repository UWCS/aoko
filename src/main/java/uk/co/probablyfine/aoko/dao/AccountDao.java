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
import uk.co.probablyfine.aoko.domain.QueueItem;
import uk.co.probablyfine.aoko.util.PlayerState;

@Repository
public class AccountDao {

	private final Logger log = LoggerFactory.getLogger(AccountDao.class);
	
	@PersistenceContext
	EntityManager em;
	
	@Transactional(readOnly = true)
	public Account getFromUsername(String username) {
		log.error("getFromUsername - Getting user - {}",username);

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Account> cq = cb.createQuery(Account.class);
		Root<Account> root = cq.from(Account.class);
		
		cq.where(cb.equal(root.get(Account_.username), username));
		
		try {
			Account a = em.createQuery(cq).setMaxResults(1).getSingleResult();
			log.error("returning value - {} : {}",a.getUsername(),a.getPassword());
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
}
