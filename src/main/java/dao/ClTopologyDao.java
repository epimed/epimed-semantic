package dao;

import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import model.entity.ClTopology;


public class ClTopologyDao extends BaseDao {

	public ClTopologyDao(Session session) {
		super(session);
	}

	public ClTopology find(String idTopology) {
		ClTopology result = (ClTopology) session
				.createCriteria(ClTopology.class)
				.add(Restrictions.eq("idTopology", idTopology) )
				.uniqueResult();
		return result;
	}
}
