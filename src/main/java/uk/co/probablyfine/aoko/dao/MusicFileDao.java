package uk.co.probablyfine.aoko.dao;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.util.concurrent.MoreExecutors;

import uk.co.probablyfine.aoko.domain.MusicFile;
import uk.co.probablyfine.aoko.domain.MusicFile_;

@Repository
public class MusicFileDao {

	@PersistenceContext
	EntityManager em;
	
	@Transactional(readOnly = true)
	public boolean containsFile(String hexVal) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<MusicFile> cq = cb.createQuery(MusicFile.class);
		Root<MusicFile> root = cq.from(MusicFile.class);
		cq.where(cb.equal(root.get(MusicFile_.uniqueId),hexVal));
		
		try {
			MusicFile mf = em.createQuery(cq).getSingleResult();
		} catch (Exception e) {
			return false;
		}
		
		return true;
		
	}

	@Transactional(readOnly = true)
	public MusicFile getFromUniqueId(String hexVal) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<MusicFile> cq = cb.createQuery(MusicFile.class);
		Root<MusicFile> root = cq.from(MusicFile.class);
		cq.where(cb.equal(root.get(MusicFile_.uniqueId),hexVal));
		
		return em.createQuery(cq).getSingleResult();
	}

}
