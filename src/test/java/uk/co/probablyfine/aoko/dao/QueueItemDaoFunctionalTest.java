package uk.co.probablyfine.aoko.dao;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static uk.co.probablyfine.aoko.domain.FileType.YOUTUBE;
import static uk.co.probablyfine.aoko.domain.PlayerState.PLAYED;
import static uk.co.probablyfine.aoko.domain.PlayerState.PLAYING;
import static uk.co.probablyfine.aoko.domain.PlayerState.QUEUED;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import uk.co.probablyfine.aoko.domain.Account;
import uk.co.probablyfine.aoko.domain.MusicFile;
import uk.co.probablyfine.aoko.domain.QueueItem;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:test-application-context.xml"})
@Transactional
public class QueueItemDaoFunctionalTest {

	@Autowired private QueueItemDao queue;
	@Autowired private MusicFileDao media;
	
	private final MusicFile file = new MusicFile();
	private final QueueItem item1 = new QueueItem();
	private final QueueItem item2 = new QueueItem();
	private final QueueItem item3 = new QueueItem();
	
	private final Map<String,String> meta = new HashMap<String,String>() {{ put("artist", "bar"); }};
	
	private final Account account = new Account("foo", "bar", "ROLE_USER");;
	
	@Before
	public void setUp() {
		item1.setUserName("foo");
		item1.setFile(file);
		item1.setBucket(0);
		item1.setPosition(1);
		item1.setStatus(PLAYED);
		
		item2.setUserName("bar");
		item2.setFile(file);
		item2.setBucket(1);
		item2.setPosition(1);
		item2.setStatus(PLAYING);
		
		item3.setUserName("baz");
		item3.setFile(file);
		item3.setBucket(2);
		item3.setPosition(1);
		item3.setStatus(QUEUED);

		file.setLocation("foo");
		file.setType(YOUTUBE);
		file.setId(0);
		file.setUniqueId("foo");
		file.setMetaData(meta);
	}
	
	@Test
	public void testGetAll_shouldReturnAllItems() {
		queue.merge(item1);
		queue.merge(item2);
		
		Collection<QueueItem> all = queue.getAll();
		
		assertEquals(2, all.size());
		assertTrue(all.contains(item1));
		assertTrue(all.contains(item2));
	}
	
	@Test
	public void testGetAllUnplayed_shouldReturnAllUnplayedItems() {
		queue.merge(item1);
		queue.merge(item2);
		queue.merge(item3);
		
		List<QueueItem> allUnplayed = queue.getAllUnplayed();
		
		assertEquals(2, allUnplayed.size());
		assertTrue(allUnplayed.contains(item2));
		assertTrue(allUnplayed.contains(item3));
	}
	
	@Test
	public void testAllQueuedByUser_shouldReturnAllQueuedItemsByUser() {
		queue.merge(item1);
		queue.merge(item2);
		queue.merge(item3);
		
		List<QueueItem> allUnplayed = queue.allQueuedByUser(account);
		
		assertEquals(1, allUnplayed.size());
		assertTrue(allUnplayed.contains(item1));
	}
	
	@Test
	public void testGetFromUserAndBucket_shouldReturnCorrectItem() {
		queue.merge(item1);
		
		QueueItem result = queue.getFromBucketAndUser(item1.getBucket(), item1.getUserName());

		assertEquals(result, item1);
	}
	
}