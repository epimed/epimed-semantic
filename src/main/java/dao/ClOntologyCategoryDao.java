package dao;

import javax.persistence.NoResultException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.hibernate.Session;

import model.entity.ClOntologyCategory;

public class ClOntologyCategoryDao extends BaseDao {

	public ClOntologyCategoryDao(Session session) {
		super(session);
	}

	/** =================================================*/

	public ClOntologyCategory find(String idCategory) {
		try {
			CriteriaBuilder builder = session.getCriteriaBuilder();
			CriteriaQuery<ClOntologyCategory> criteria = builder.createQuery(ClOntologyCategory.class);
			Root<ClOntologyCategory> root = criteria.from(ClOntologyCategory.class);
			criteria.select(root).where(builder.equal(root.get("idCategory"), idCategory));
			return session.createQuery(criteria).getSingleResult();
		}
		catch (NoResultException e) {
			return null;
		}
	}

	/** =================================================*/
}
