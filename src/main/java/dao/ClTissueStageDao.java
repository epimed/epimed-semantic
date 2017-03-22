package dao;

import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.hibernate.Session;

import model.entity.ClTissueStage;


public class ClTissueStageDao extends BaseDao {

	public ClTissueStageDao(Session session) {
		super(session);
	}

	/** ==================================================================== */
	public List<ClTissueStage> list() {

		CriteriaBuilder builder = session.getCriteriaBuilder();
		CriteriaQuery<ClTissueStage> criteria = builder.createQuery(ClTissueStage.class);
		Root<ClTissueStage> root = criteria.from(ClTissueStage.class);
		criteria.select(root);
		return session.createQuery(criteria).getResultList();
	}

	/** ==================================================================== */

	public ClTissueStage find(Integer id) {
		try {
			CriteriaBuilder builder = session.getCriteriaBuilder();
			CriteriaQuery<ClTissueStage> criteria = builder.createQuery(ClTissueStage.class);
			Root<ClTissueStage> root = criteria.from(ClTissueStage.class);
			criteria.select(root).where(builder.equal(root.get("idTissueStage"), id));
			return session.createQuery(criteria).getSingleResult();
		}
		catch (NoResultException e) {
			return null;
		}
	}

	/** ==================================================================== */

	public ClTissueStage find(String stage) {
		try {
			CriteriaBuilder builder = session.getCriteriaBuilder();
			CriteriaQuery<ClTissueStage> criteria = builder.createQuery(ClTissueStage.class);
			Root<ClTissueStage> root = criteria.from(ClTissueStage.class);
			criteria.select(root).where(builder.equal(root.get("stage"), stage));
			return session.createQuery(criteria).getSingleResult();
		}
		catch (NoResultException e) {
			return null;
		}
	}

	/** ==================================================================== */
}
