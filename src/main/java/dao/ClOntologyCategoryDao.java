package dao;

import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import model.entity.ClOntologyCategory;



public class ClOntologyCategoryDao extends BaseDao {

	public ClOntologyCategoryDao(Session session) {
		super(session);
	}

	/** =================================================*/

	public ClOntologyCategory find(String idCategory) {
		return (ClOntologyCategory) session
				.createCriteria(ClOntologyCategory.class)
				.add(Restrictions.eq("idCategory", idCategory))
				.uniqueResult();
	}

	/** =================================================*/
}
