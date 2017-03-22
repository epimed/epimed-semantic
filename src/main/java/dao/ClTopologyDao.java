package dao;

import javax.persistence.NoResultException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.hibernate.Session;

import model.entity.ClTopology;

public class ClTopologyDao extends BaseDao {

	public ClTopologyDao(Session session) {
		super(session);
	}

	/** ============================================================= */

	public ClTopology find(String idTopology) {
		try {
			CriteriaBuilder builder = session.getCriteriaBuilder();
			CriteriaQuery<ClTopology> criteria = builder.createQuery(ClTopology.class);
			Root<ClTopology> root = criteria.from(ClTopology.class);
			criteria.select(root).where(builder.equal(root.get("idTopology"), idTopology));
			return session.createQuery(criteria).getSingleResult();
		}
		catch (NoResultException e) {
			return null;
		}
	}

	/** ============================================================= */
}
