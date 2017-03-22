package dao;

import javax.persistence.NoResultException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.hibernate.Session;

import model.entity.ClPathology;


public class ClPathologyDao extends BaseDao {

	public ClPathologyDao(Session session) {
		super(session);
	}

	/** ============================================================ */

	public ClPathology find(Integer id) {
		try {
			CriteriaBuilder builder = session.getCriteriaBuilder();
			CriteriaQuery<ClPathology> criteria = builder.createQuery(ClPathology.class);
			Root<ClPathology> root = criteria.from(ClPathology.class);
			criteria.select(root).where(builder.equal(root.get("idPathology"), id));
			return session.createQuery(criteria).getSingleResult();
		}
		catch (NoResultException e) {
			return null;
		}
	}

	/** ============================================================ */

	public ClPathology find(String name) {
		try {
			CriteriaBuilder builder = session.getCriteriaBuilder();
			CriteriaQuery<ClPathology> criteria = builder.createQuery(ClPathology.class);
			Root<ClPathology> root = criteria.from(ClPathology.class);
			criteria.select(root).where(builder.equal(root.get("name"), name));
			return session.createQuery(criteria).getSingleResult();
		}
		catch (NoResultException e) {
			return null;
		}
	}

	/** ============================================================ */
}
