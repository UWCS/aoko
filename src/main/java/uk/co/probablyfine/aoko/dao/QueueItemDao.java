package uk.co.probablyfine.aoko.dao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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

import uk.co.probablyfine.aoko.domain.MusicFile;
import uk.co.probablyfine.aoko.domain.PlayerState;
import uk.co.probablyfine.aoko.domain.QueueItem;
import uk.co.probablyfine.aoko.domain.QueueItem_;
import uk.co.probablyfine.aoko.domain.Account;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;

@Repository
public class QueueItemDao {
	
	private Logger log = LoggerFactory.getLogger(QueueItemDao.class);
	
	@PersistenceContext
	EntityManager em;
	
	@Transactional
	public void queueTrack(final Account user, final MusicFile track) {
		log.debug("queueTrack - Queueing track from {}",user.getUsername());
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<QueueItem> cq = cb.createQuery(QueueItem.class);
		Root<QueueItem> root = cq.from(QueueItem.class);
		
		//Get all queued items that haven't been played or are playing
		
		List<QueueItem> results = em.createQuery(cq).getResultList();
		log.debug("Received {} results",results.size());
		List<QueueItem> process = new ArrayList<QueueItem>(Collections2.filter(results, new Predicate<QueueItem>() {
			@Override
			public boolean apply(QueueItem input) {
				return input.getState() != PlayerState.PLAYED;
			}
		}));
		
		Collections.sort(process);
		Collections.sort(results);
		
		final int finalBucket;
		final int position;
		
		//The bucket we're in at the moment
		if (process.size() != 0) {
			
			
			int currentbucket = process.get(0).getBucket();
			
			List<QueueItem> bucketItems = new ArrayList<QueueItem>();
			bucketItems.addAll(Collections2.filter(process, new Predicate<QueueItem>() {
				@Override
				public boolean apply(QueueItem input) {
					return input.getUserName().equals(user.getUsername());
				}
			}));
			
			log.debug("User has currently queued, in order - {}",bucketItems);
			
			List<Integer> buckets = new ArrayList<Integer>();
			buckets.addAll(Collections2.transform(bucketItems, new Function<QueueItem, Integer>() {
				@Override
				public Integer apply(QueueItem input) {
					return input.getBucket();
				}
			}));
			
			while (buckets.contains(currentbucket)) {
				currentbucket++;
			}
			
			finalBucket = currentbucket;
			
			List<QueueItem> currentBucketList = new ArrayList<QueueItem>();
			currentBucketList.addAll(Collections2.filter(results, new Predicate<QueueItem>() {
				@Override
				public boolean apply(QueueItem input) {
					return input.getBucket() == finalBucket;
				}
			}));
			
			if (currentBucketList.size() == 0) {
				position = 1;
			} else {
				position = Collections.max(currentBucketList).getPosition()+1;
			}
			
		} else if (results.size() != 0) {
			finalBucket = results.get(results.size()-1).getBucket()+1;
			position = 1;
		} else {
			finalBucket = 1;
			position = 1;
		}
		
		QueueItem qi = new QueueItem(user, track);
		qi.setBucket(finalBucket);
		qi.setPosition(position);
		log.debug("Final Bucket = {}, Final Position = {}",finalBucket,position);

		em.merge(qi);
		
	}
	
	@Transactional(readOnly = true)
	public QueueItem nextTrack() {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<QueueItem> cq = cb.createQuery(QueueItem.class);
		Root<QueueItem> root = cq.from(QueueItem.class);
		cq.where(cb.notEqual(root.get(QueueItem_.status), PlayerState.PLAYED));
		
		QueueItem qi = null;
		try {
			 qi = em.createQuery(cq).setMaxResults(1).getSingleResult();
		} catch (Exception e) {
			log.debug("Cannot return new track");
			log.error("Exception",e);
			return qi;
		}
		
		log.debug("Succesfully returning new track with id {}",qi.getId());
		return qi; 
		
	}
	@Transactional(readOnly = true)
	public List<QueueItem> getAll() {
		log.debug("Trying to get all items");
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<QueueItem> cq = cb.createQuery(QueueItem.class);
		Root<QueueItem> root = cq.from(QueueItem.class);
		cq.where(cb.notEqual(root.get(QueueItem_.status), PlayerState.PLAYED));
		List<QueueItem> resultsList = em.createQuery(cq).getResultList();
		Collections.sort(resultsList);
		return resultsList;
	}
	
	@Transactional
	public void finishedPlaying(QueueItem qi) {
		log.debug("Setting {} as finished playing",qi.getFile().getUniqueId());
		qi.setState(PlayerState.PLAYED);
		em.merge(qi);
	}
	
	@Transactional
	public void startedPlaying(QueueItem qi) {
		log.debug("Setting {} as playing",qi.getFile().getUniqueId());
		qi.setState(PlayerState.PLAYING);
		em.merge(qi);
	}
	
	@Transactional
	public void merge(QueueItem qi) {
			em.merge(qi);
	}
	
	@Transactional
	public void shift(String user, int bucket, int mod) {
		
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<QueueItem> cq = cb.createQuery(QueueItem.class);
		Root<QueueItem> root = cq.from(QueueItem.class);
		
		/* Get all items with:
		 *  - Name = User
		 *  - Bucket Index = Equal to this one or the one above/below it
		 *  - Status = Not yet played/playing
		 */

		log.debug("Bucket = {}, Bucket+Mod = {}",bucket,bucket+mod);
		
		cq.where(
			cb.and(
				cb.equal(root.get(QueueItem_.userName), user),
				cb.or(
						cb.equal(root.get(QueueItem_.bucket), bucket),
						cb.equal(root.get(QueueItem_.bucket), bucket+mod)
					),
				cb.equal(root.get(QueueItem_.status), PlayerState.QUEUED)
				)
			);

		/*cq.where(cb.equal(root.get(QueueItem_.userName), user));
		cq.where(cb.between(root.get(QueueItem_.bucket), bucket, bucket+mod));
		cq.where(cb.equal(root.get(QueueItem_.status), PlayerState.QUEUED));
		*/
		
		List<QueueItem> results = new ArrayList<QueueItem>();
		results.addAll(em.createQuery(cq).getResultList());
		
		//If we have less than 2 results, then there's nothing to do.
		if (results.size() < 2)	{
			log.debug("Only returned {} results",results.size());
			return;
		}
		
		Collections.sort(results);
		
		QueueItem qi1 = em.merge(results.get(0));
		QueueItem qi2 = em.merge(results.get(1));
		
		int bucket1 = qi1.getBucket();
		int bucket2 = qi2.getBucket();
		
		int pos1 = qi1.getPosition();
		int pos2 = qi2.getPosition();

		log.debug("Item 1. Bucket = {}, Pos = {}",bucket1,pos1);
		log.debug("Item 2. Bucket = {}, Pos = {}",bucket2,pos1);
		
		qi1.setBucket(bucket2);
		qi1.setPosition(pos2);
		
		//em.merge(qi1);		
		
		qi2.setPosition(pos1);
		qi2.setBucket(bucket1);
		
		//em.persist(qi2);
		
	}
	
	@Transactional
	public void shiftUp(String user, int bucket) {
		log.debug("Starting upshift on {}, {}",user,bucket);
		this.shift(user, bucket, -1);
	}
	
	@Transactional
	public void shiftDown(String user, int bucket) {
		log.debug("Starting downshift on {}, {}",user,bucket);
		this.shift(user, bucket, 1);
	}

	@Transactional
	public QueueItem getFromId(int trackId) {
		log.debug("Getting track from id {}",trackId);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<QueueItem> cq = cb.createQuery(QueueItem.class);
		Root<QueueItem> root = cq.from(QueueItem.class);
		
		cq.where(cb.notEqual(root.get(QueueItem_.id), trackId));
		QueueItem qi = null;
		try {
			 qi = em.createQuery(cq).setMaxResults(1).getSingleResult();
		} catch (Exception e) {
			return qi;
		}
		
		return qi; 
	}
	
	
}