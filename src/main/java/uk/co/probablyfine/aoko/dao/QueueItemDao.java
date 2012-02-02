package uk.co.probablyfine.aoko.dao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;

import uk.co.probablyfine.aoko.domain.MusicFile;
import uk.co.probablyfine.aoko.domain.QueueItem;
import uk.co.probablyfine.aoko.domain.QueueItem_;
import uk.co.probablyfine.aoko.domain.User;
import uk.co.probablyfine.aoko.util.PlayerState;

@Repository
public class QueueItemDao {

	@PersistenceContext
	private EntityManager em;
	
	@Transactional
	public void queueTrack(final User user, final MusicFile track) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<QueueItem> cq = cb.createQuery(QueueItem.class);
		Root<QueueItem> root = cq.from(QueueItem.class);
		
		//Get all queued items that haven't been played or are playing
		cq.where(cb.notEqual(root.get(QueueItem_.status), PlayerState.PLAYED));
		
		List<QueueItem> results = em.createQuery(cq).getResultList();
		List<QueueItem> process = new ArrayList<QueueItem>();
		
		process.addAll(process);
				
		Collections.sort(process);
		
		//The bucket we're in at the moment
		int currentbucket = process.get(0).getBucket();

		//
		Collections2.filter(process, new Predicate<QueueItem>() {
			@Override
			public boolean apply(QueueItem input) {
				return input.getUserName() == user.getUsername();
			}
		});
				
		Collections2.transform(process, new Function<QueueItem, Integer>() {

			@Override
			public Integer apply(QueueItem input) {
				return input.getBucket();
			}
		});
		
		while (process.contains(currentbucket)) {
			currentbucket++;
		}
		
		final int finalBucket = currentbucket;
		
		int max = Collections.max(Collections2.filter(results, new Predicate<QueueItem>() {
			@Override
			public boolean apply(QueueItem input) {
				return input.getBucket() == finalBucket;
			}
		})).getPosition() ;
		
		QueueItem qi = new QueueItem(user, track);
		qi.setBucket(finalBucket);
		qi.setPosition(max);

		em.merge(qi);
		
	}
	
	@Transactional(readOnly = true)
	public QueueItem nextTrack() {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<QueueItem> cq = cb.createQuery(QueueItem.class);
		Root<QueueItem> root = cq.from(QueueItem.class);
		
		cq.where(cb.notEqual(root.get(QueueItem_.status), PlayerState.PLAYED));
	
		return em.createQuery(cq).setMaxResults(1).getSingleResult();
		
		
	}
	
	public void merge(QueueItem qi) {
		em.merge(qi);
	}
	
	
}