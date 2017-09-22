package dao;

import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.hibernate.Session;

import model.entity.ClMorphology;

public class ClMorphologyDao extends BaseDao {

	public ClMorphologyDao(Session session) {
		super(session);
	}


	/** ========================================================= */

	public ClMorphology find(String id) {
		try {		
			CriteriaBuilder builder = session.getCriteriaBuilder();
			CriteriaQuery<ClMorphology> criteria = builder.createQuery(ClMorphology.class);
			Root<ClMorphology> root = criteria.from(ClMorphology.class);
			criteria.select(root).where(builder.equal(root.get("idMorphology"), id));
			return session.createQuery(criteria).getSingleResult();
		}
		catch (NoResultException e) {
			return null;
		}
	}

	/** ========================================================= */

	public List<ClMorphology> listMorphologyWithSpecialCharacters() {

		CriteriaBuilder builder = session.getCriteriaBuilder();
		CriteriaQuery<ClMorphology> criteria = builder.createQuery(ClMorphology.class);
		Root<ClMorphology> root = criteria.from(ClMorphology.class);
		criteria.select(root).where(
				builder.or(
						builder.like(root.<String>get("name"), "%(%"),
						builder.like(root.<String>get("name"), "%[%")
						)
				);

		return session.createQuery(criteria).getResultList();
	}

	/** ========================================================= */

}
