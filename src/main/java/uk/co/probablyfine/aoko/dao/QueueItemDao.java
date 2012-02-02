package uk.co.probablyfine.aoko.dao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Repository;

import uk.co.probablyfine.aoko.domain.MusicFile;
import uk.co.probablyfine.aoko.domain.QueueItem;
import uk.co.probablyfine.aoko.domain.QueueItem_;
import uk.co.probablyfine.aoko.domain.User;

@Repository
public class QueueItemDao {

	@PersistenceContext
	private EntityManager em;
	
	public void queueTrack(User user2, MusicFile track) {
		
		
		
	}
	
	
}
