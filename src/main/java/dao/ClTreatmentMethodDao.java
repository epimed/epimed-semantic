package dao;

import javax.persistence.NoResultException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import model.entity.ClTreatmentMethod;

public class ClTreatmentMethodDao extends BaseDao {

	public ClTreatmentMethodDao(Session session) {
		super(session);
	}

	/** ============================================================= */

	public ClTreatmentMethod findById(Integer id) {
		try {
			CriteriaBuilder builder = session.getCriteriaBuilder();
			CriteriaQuery<ClTreatmentMethod> criteria = builder.createQuery(ClTreatmentMethod.class);
			Root<ClTreatmentMethod> root = criteria.from(ClTreatmentMethod.class);
			criteria.select(root).where(builder.equal(root.get("idTreatmentMethod"), id));
			return session.createQuery(criteria).getSingleResult();
		}
		catch (NoResultException e) {
			return null;
		}
	}

	/** ============================================================= */

	public ClTreatmentMethod findByName(String name) {
		try {
			CriteriaBuilder builder = session.getCriteriaBuilder();
			CriteriaQuery<ClTreatmentMethod> criteria = builder.createQuery(ClTreatmentMethod.class);
			Root<ClTreatmentMethod> root = criteria.from(ClTreatmentMethod.class);
			criteria.select(root).where(builder.equal(root.get("name"), name));
			return session.createQuery(criteria).getSingleResult();
		}
		catch (NoResultException e) {
			return null;
		}
	}

	/** ============================================================= */

}
