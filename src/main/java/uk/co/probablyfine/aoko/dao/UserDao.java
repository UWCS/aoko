package uk.co.probablyfine.aoko.dao;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;

import uk.co.probablyfine.aoko.domain.User;

@Repository
public class UserDao {

	@PersistenceContext
	EntityManager em;
	
	public User getFromUsername(String queuedBy) {
		// TODO Auto-generated method stub
		return null;
	}

}
