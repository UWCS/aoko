package uk.co.probablyfine.aoko.service;

import static com.google.common.collect.Collections2.filter;
import static com.google.common.collect.Multimaps.index;
import static java.util.Collections.min;
import static uk.co.probablyfine.aoko.domain.PlayerState.PLAYED;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import uk.co.probablyfine.aoko.dao.QueueItemDao;
import uk.co.probablyfine.aoko.domain.Account;
import uk.co.probablyfine.aoko.domain.MusicFile;
import uk.co.probablyfine.aoko.domain.QueueItem;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;

@Component
public class QueueService {

	private final Function<QueueItem, Integer> INDEX_BY_BUCKET = new Function<QueueItem, Integer>() {
		public Integer apply(QueueItem arg0) {
			return arg0.getBucket();
		}
	};

	private final Predicate<QueueItem> UNPLAYED = new Predicate<QueueItem>() {
		public boolean apply(QueueItem input) { return input.getStatus() != PLAYED; }
	};
	
	private final Function<QueueItem, Integer> QUEUE_ITEM_TO_BUCKET = new Function<QueueItem, Integer>() {
		public Integer apply(QueueItem input) {	return input.getBucket(); }
	};
	
	@Autowired private QueueItemDao queue;
	
	private Logger log = LoggerFactory.getLogger(QueueService.class);
	
	public Collection<Collection<QueueItem>> getQueueLayout() {
		
		List<QueueItem> resultsList = queue.getAllUnplayed();
		
		if (resultsList.size() == 0)
			return new ArrayList<Collection<QueueItem>>();
		
		Collections.sort(resultsList);
		
		return index(resultsList, INDEX_BY_BUCKET).asMap().values();
	}
	
	public void queueTrack(final Account user, final MusicFile track) {
		
		List<QueueItem> results = new ArrayList<QueueItem>(queue.getAll());
		log.debug("Queueing track from {}",user.getUsername());
		final List<QueueItem> process = new ArrayList<QueueItem>(filter(results, UNPLAYED));
		
		Collections.sort(process);
		Collections.sort(results);
		
		final int finalBucket, position;
		
		if (!process.isEmpty()) {
			
			log.debug("The waiting queue is not empty, finding bucket to add into.");
			
			int currentbucket = min(process).getBucket();
		
			log.debug("Currently playing bucket {}",currentbucket);

			List<QueueItem> bucketItems = new ArrayList<QueueItem>();
			
			bucketItems.addAll(filter(results, new Predicate<QueueItem>() {
				final int currentbucket = Collections.min(process).getBucket();
				
				@Override
				public boolean apply(QueueItem input) {
					return input.getUserName().equals(user.getUsername()) && input.getBucket() >= currentbucket;
				}
			}));

			log.debug("User has currently queued, in order - {}",bucketItems);
			
			List<Integer> buckets = new ArrayList<Integer>();
			
			buckets.addAll(Collections2.transform(bucketItems, QUEUE_ITEM_TO_BUCKET));
			
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
		
		log.debug("Final Bucket = {}, Final Position = {}", finalBucket, position);

		queue.merge(qi);
		
	}
		
}
