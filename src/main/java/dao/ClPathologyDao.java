package dao;

import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import model.entity.ClPathology;



public class ClPathologyDao extends BaseDao {

	public ClPathologyDao(Session session) {
		super(session);
	}

	public ClPathology find(Integer id) {
		ClPathology result = (ClPathology) session
				.createCriteria(ClPathology.class)
				.add(Restrictions.eq("idPathology", id))
				.uniqueResult();
		return result;
	}
	
	public ClPathology find(String name) {
		ClPathology result = (ClPathology) session
				.createCriteria(ClPathology.class)
				.add(Restrictions.eq("name",  name))
				.uniqueResult();
		return result;
	}
}
