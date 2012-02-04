package uk.co.probablyfine.aoko.dao;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.co.probablyfine.aoko.domain.YoutubeDownload;
import uk.co.probablyfine.aoko.domain.YoutubeDownload_;
import uk.co.probablyfine.aoko.util.DownloadState;

@Repository
public class YoutubeDao {

	@PersistenceContext
	EntityManager em;
	
	@Transactional(readOnly = true)
	public YoutubeDownload next() {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<YoutubeDownload> cq = cb.createQuery(YoutubeDownload.class);
		final Root<YoutubeDownload> dl = cq.from(YoutubeDownload.class);
		cq.where(cb.equal(dl.get(YoutubeDownload_.state), DownloadState.WAITING));
		return em.createQuery(cq).getSingleResult();
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
	public void merge(YoutubeDownload dl) {
		em.merge(dl);
	}
}
