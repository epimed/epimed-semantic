package dao;

import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.hibernate.Session;

import model.entity.ClTissueStatus;

public class ClTissueStatusDao extends BaseDao {

	public ClTissueStatusDao(Session session) {
		super(session);
	}

	/** ==================================================================== */

	public List<ClTissueStatus> list() {	
		CriteriaBuilder builder = session.getCriteriaBuilder();
		CriteriaQuery<ClTissueStatus> criteria = builder.createQuery(ClTissueStatus.class);
		Root<ClTissueStatus> root = criteria.from(ClTissueStatus.class);
		criteria.select(root);
		return session.createQuery(criteria).getResultList();
	}

	/** ==================================================================== */

	public ClTissueStatus find(Integer id) {

		try {
			CriteriaBuilder builder = session.getCriteriaBuilder();
			CriteriaQuery<ClTissueStatus> criteria = builder.createQuery(ClTissueStatus.class);
			Root<ClTissueStatus> root = criteria.from(ClTissueStatus.class);
			criteria.select(root).where(builder.equal(root.get("idTissueStatus"), id));
			return session.createQuery(criteria).getSingleResult();
		}
		catch (NoResultException e) {
			return null;
		}
	}

	/** ==================================================================== */

	public ClTissueStatus find(String name) {
		try {
			CriteriaBuilder builder = session.getCriteriaBuilder();
			CriteriaQuery<ClTissueStatus> criteria = builder.createQuery(ClTissueStatus.class);
			Root<ClTissueStatus> root = criteria.from(ClTissueStatus.class);
			criteria.select(root).where(builder.equal(root.get("name"), name));
			return session.createQuery(criteria).getSingleResult();
		}
		catch (NoResultException e) {
			return null;
		}
	}

	/** ==================================================================== */

}
