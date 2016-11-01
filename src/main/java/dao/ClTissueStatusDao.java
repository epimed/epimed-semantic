package dao;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import model.entity.ClTissueStatus;


@SuppressWarnings("unchecked")
public class ClTissueStatusDao extends BaseDao {

	public ClTissueStatusDao(Session session) {
		super(session);
	}
	
	/** ==================================================================== */
	
	public List<ClTissueStatus> list() {
		List<ClTissueStatus> result = session
				.createCriteria(ClTissueStatus.class)
				.list();
		return result;
	}

	/** ==================================================================== */

	public ClTissueStatus find(Integer id) {
		ClTissueStatus result = (ClTissueStatus) session
				.createCriteria(ClTissueStatus.class)
				.add(Restrictions.eq("idTissueStatus", id) )
				.uniqueResult();
		return result;
	}

	/** ==================================================================== */

	public ClTissueStatus find(String name) {
		ClTissueStatus result = (ClTissueStatus) session
				.createCriteria(ClTissueStatus.class)
				.add(Restrictions.eq("name", name))
				.uniqueResult();
		return result;
	}

	/** ==================================================================== */

}
