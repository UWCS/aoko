package uk.co.probablyfine.aoko.dao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.co.probablyfine.aoko.domain.Account;
import uk.co.probablyfine.aoko.domain.MusicFile;
import uk.co.probablyfine.aoko.domain.PlayerState;
import uk.co.probablyfine.aoko.domain.QueueItem;
import uk.co.probablyfine.aoko.domain.QueueItem_;

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
		
		final List<QueueItem> process = new ArrayList<QueueItem>(Collections2.filter(results, new Predicate<QueueItem>() {
			@Override
			public boolean apply(QueueItem input) {
				return input.getStatus() != PlayerState.PLAYED;
			}
		}));
		
		Collections.sort(process);
		
		Collections.sort(results);
		
		final int finalBucket;
		final int position;
		
		//The bucket we're in at the moment
		if (!process.isEmpty()) {
			
			log.debug("The waiting queue is not empty, finding bucket to add into.");
			
			int currentbucket = Collections.min(process).getBucket();
		
			log.debug("Currently playing bucket {}",currentbucket);

			List<QueueItem> bucketItems = new ArrayList<QueueItem>();
			
			bucketItems.addAll(Collections2.filter(results, new Predicate<QueueItem>() {
				
				final int currentbucket = Collections.min(process).getBucket();
				
				@Override
				public boolean apply(QueueItem input) {
					return input.getUserName().equals(user.getUsername()) && input.getBucket() >= currentbucket;
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
			
			log.debug("User has queued in upcoming buckets, in order - {}",buckets);

			while (buckets.contains(currentbucket)) {
				log.debug("Buckets contain {}, increasing.");
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
				log.debug("Nothing in selected bucket, creating a new one.");
				position = 1;
			} else {
				log.debug("Appending to end of current bucket.");
				position = Collections.max(currentBucketList).getPosition()+1;
			}
			
		} else if (results.size() != 0) {
			log.debug("Queue not empty, but things have been played, creating new bucket.");
			finalBucket = Collections.max(results).getBucket()+1;
			position = 1;
		} else {
			log.debug("Queue was completely empty, adding to start");
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
		cq.orderBy(cb.asc(root.get(QueueItem_.bucket)));
		
		QueueItem qi = null;
		
		try {
			 qi = em.createQuery(cq).setMaxResults(1).getSingleResult();
		} catch (Exception e) {
			if (e.getMessage().equals("No entity found for query")) {
				log.debug("Cannot return track, nothing found.");
			} else {
				log.error("Cannot return track",e);
			}
			return qi;
		}
		
		log.debug("Succesfully returning new track with id {}",qi.getId());
		return qi;
		
	}
	@Transactional(readOnly = true)
	public List<List<QueueItem>> getAll() {
		log.debug("Trying to get all items");
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<QueueItem> cq = cb.createQuery(QueueItem.class);
		Root<QueueItem> root = cq.from(QueueItem.class);
		cq.where(cb.notEqual(root.get(QueueItem_.status), PlayerState.PLAYED));
		List<QueueItem> resultsList = em.createQuery(cq).getResultList();
	
		List<List<QueueItem>> bucketList = new ArrayList<List<QueueItem>>();
		
		if (resultsList.size() == 0)
			return new ArrayList<List<QueueItem>>();
		
		Collections.sort(resultsList);
		
		int firstBucket = resultsList.get(0).getBucket();
		
		List<QueueItem> currentBucket = new ArrayList<QueueItem>();
		
		for(int i = 0; i < resultsList.size(); i++) {
			if (resultsList.get(i).getBucket() == firstBucket) {
				currentBucket.add(resultsList.get(i));
			} else {
				firstBucket = resultsList.get(i).getBucket();
				bucketList.add(currentBucket);
				currentBucket = new ArrayList<QueueItem>();
				currentBucket.add(resultsList.get(i));
			}
		}
		
		if (!currentBucket.isEmpty()) 
			bucketList.add(currentBucket);
		
		return bucketList;
	}
	
	@Transactional
	public void finishedPlaying(QueueItem qi) {
		log.debug("Setting {} as finished playing",qi.getFile().getUniqueId());
		qi.setStatus(PlayerState.PLAYED);
		em.merge(qi);
	}
	
	@Transactional
	public void startedPlaying(QueueItem qi) {
		log.debug("Setting {} as playing",qi.getFile().getUniqueId());
		qi.setStatus(PlayerState.PLAYING);
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
		
		List<QueueItem> results = new ArrayList<QueueItem>();
		results.addAll(em.createQuery(cq).getResultList());
		
		//If we have less than 2 results, then there's nothing to do.
		if (results.size() < 2)	{
			log.debug("Only returned {} results",results.size());
			return;
		}
		
		Collections.sort(results);
		
		QueueItem qi1 = results.get(0);
		QueueItem qi2 = results.get(1);
		
		log.debug("BEFORE - Item 1. Bucket = {}, Pos = {}",qi1.getBucket(),qi1.getPosition());
		log.debug("BEFORE - Item 2. Bucket = {}, Pos = {}",qi2.getBucket(),qi1.getPosition());
		
		MusicFile file1 = qi1.getFile();
		
		qi1.setFile(qi2.getFile());
		qi2.setFile(file1);

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
	public QueueItem getFromBucketAndUser(int bucket, String user) {
		
		log.debug("trying to remove {} in {}",user,bucket);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<QueueItem> cq = cb.createQuery(QueueItem.class);
		Root<QueueItem> root = cq.from(QueueItem.class);
		
		cq.where(
			cb.and(
				cb.equal(root.get(QueueItem_.bucket), bucket),
				cb.equal(root.get(QueueItem_.userName), user)
			)
		);	
				
		QueueItem qi = null;
		
		try {
			 qi = em.createQuery(cq).setMaxResults(1).getSingleResult();
		} catch (Exception e) {
			log.debug("Returning null");
			log.debug("Exception",e);
			return qi;
		}
		
		
		log.debug("Returning {}",qi);
		return qi; 
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

	@Transactional
	public void deleteItem(int bucket, String user) {
		log.debug("Deleting from {} by {}",bucket,user);
		QueueItem qi = getFromBucketAndUser(bucket, user);
		log.debug("Deleting {}",qi);
		log.debug("Deleting id {}",qi.getId());
		em.remove(qi);
	}
	
	@Transactional
	public void deleteItem(QueueItem qi) {
		em.remove(qi);
	}

	@Transactional(readOnly = true)
	public List<QueueItem> allQueuedByUser(Account a) {
		log.debug("Getting all items from {}",a.getUsername());
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<QueueItem> cq = cb.createQuery(QueueItem.class);
		Root<QueueItem> root = cq.from(QueueItem.class);
		
		cq.where(cb.equal(root.get(QueueItem_.userName), a.getUsername()));
		List<QueueItem> qi = null;
		try {
			 qi = em.createQuery(cq).getResultList();
		} catch (Exception e) {
			log.error("Exception: ",e);
		}
		
		return qi;
		

	}
	
	
}
