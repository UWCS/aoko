package uk.co.probablyfine.aoko.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;
import javax.persistence.criteria.Predicate.BooleanOperator;

import org.springframework.stereotype.Repository;

import uk.co.probablyfine.aoko.domain.MusicFile;
import uk.co.probablyfine.aoko.domain.QueueItem;
import uk.co.probablyfine.aoko.domain.QueueItem_;
import uk.co.probablyfine.aoko.domain.User;

@Repository
public class QueueItemDao {

	@PersistenceContext
	private EntityManager em;
	
	public void queueTrack(User user, MusicFile track) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Integer> cq = cb.createQuery(Integer.class);
		Root<QueueItem> root = cq.from(QueueItem.class);
		cq.select(root.get(QueueItem_.bucket));
		cq.where(cb.equal(root.get(QueueItem_.userName),user.getUsername()));
	}
	
	
}
