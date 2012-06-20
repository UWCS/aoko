package uk.co.probablyfine.aoko.dao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.co.probablyfine.aoko.domain.DownloadState;
import uk.co.probablyfine.aoko.domain.YoutubeDownload;
import uk.co.probablyfine.aoko.domain.YoutubeDownload_;

@Repository
public class YoutubeDao {

	private final Logger log = LoggerFactory.getLogger(YoutubeDao.class);
	
	@PersistenceContext
	EntityManager em;
	
	private int currentBucket = 0;
	
	@Transactional(readOnly = true)
	public YoutubeDownload next() {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<YoutubeDownload> cq = cb.createQuery(YoutubeDownload.class);
		final Root<YoutubeDownload> dl = cq.from(YoutubeDownload.class);
		cq.where(cb.equal(dl.get(YoutubeDownload_.state), DownloadState.WAITING));
		cq.orderBy(cb.asc(dl.get(YoutubeDownload_.bucket)));
		YoutubeDownload yt = null;
		
		try {
			List<YoutubeDownload> list = em.createQuery(cq).getResultList();
			
			if (!list.isEmpty() || null == list) {
				yt = list.get(0);
				
				if (currentBucket != yt.getBucket())
					currentBucket = yt.getBucket();
			}
			
		} catch (Exception e) {
			log.error("Error getting next track: ",e);
		}
		
		log.debug("{}",yt);
		
		return yt;
		
	}
	
	@Transactional
	public void dlSuccess(YoutubeDownload dl) {
		dl.setState(DownloadState.DOWNLOADED);
		em.merge(dl);
	}
	
	@Transactional
	public void dlFail(YoutubeDownload dl) {
		dl.setState(DownloadState.ERROR);
		em.merge(dl);
	}
	
	@Transactional
	public void queueDownload(final YoutubeDownload download) {
		log.debug("Queueing download from {}",download.getQueuedBy());
		
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<YoutubeDownload> cq = cb.createQuery(YoutubeDownload.class);
		final Root<YoutubeDownload> root = cq.from(YoutubeDownload.class);

		cq.where(
				cb.and(
					cb.equal(root.get(YoutubeDownload_.queuedBy), download.getQueuedBy()),
					cb.notEqual(root.get(YoutubeDownload_.state), DownloadState.DOWNLOADED)
				)
		);
		
		List<YoutubeDownload> yt = new ArrayList<YoutubeDownload>();
		
		log.debug("{}",yt);
		
		try {
			yt = em.createQuery(cq).getResultList();
		} catch (Exception e) {
			log.error("Exception: {}",e);
		}
		
		if (yt.isEmpty()){
			log.debug("Setting {} bucket to current bucket.", download.getUrl());
			download.setBucket(currentBucket);
		} else {
			log.debug("Setting {} bucket to last known bucket + 1", download.getUrl());
			download.setBucket(Collections.max(yt).getBucket()+1);
		}
		
		em.persist(download);

	}
	
	@Transactional
	public void merge(YoutubeDownload dl) {
		em.merge(dl);
	}

	@Transactional
	public void remove(YoutubeDownload dl) {
		em.remove(dl);
	}
	
	@Transactional(readOnly = true)
	public List<YoutubeDownload> getAllQueued() {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<YoutubeDownload> cq = cb.createQuery(YoutubeDownload.class);
		final Root<YoutubeDownload> dl = cq.from(YoutubeDownload.class);
		
		cq.where(cb.equal(dl.get(YoutubeDownload_.state), DownloadState.WAITING));
		cq.orderBy(cb.asc(dl.get(YoutubeDownload_.bucket)));
		
		List<YoutubeDownload> videoList = new ArrayList<YoutubeDownload>();
		
		try {
			videoList = em.createQuery(cq).getResultList();
		} catch (Exception e) {
			System.out.println("Could not get single result");
		}
		
		Collections.sort(videoList);
		
		return videoList;
		
	}

	@Transactional
	public void delete(int videoId, String name) {
		final CriteriaBuilder cb = em.getCriteriaBuilder();
		final CriteriaQuery<YoutubeDownload> cq = cb.createQuery(YoutubeDownload.class);
		final Root<YoutubeDownload> dl = cq.from(YoutubeDownload.class);

		cq.where(
				cb.and(
					cb.equal(dl.get(YoutubeDownload_.queuedBy), name),
					cb.equal(dl.get(YoutubeDownload_.id), videoId)
				)
		);
		
		YoutubeDownload yt = null;
		try {
			yt = em.createQuery(cq).getSingleResult();
		} catch (NonUniqueResultException e) {
			log.debug("Could not get result for this track, returning null");
			log.error("Exception: {}",e);
		}
	
		if (null == yt)
			return;
		
		remove(yt);
	}
}
