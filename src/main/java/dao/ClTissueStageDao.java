package dao;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import model.entity.ClTissueStage;


@SuppressWarnings("unchecked")
public class ClTissueStageDao extends BaseDao {

	public ClTissueStageDao(Session session) {
		super(session);
	}
	
	/** ==================================================================== */
	public List<ClTissueStage> list() {
		List<ClTissueStage> result = session
				.createCriteria(ClTissueStage.class)
				.list();
		return result;
	}
	
	/** ==================================================================== */

	public ClTissueStage find(Integer id) {
		ClTissueStage result = (ClTissueStage) session
				.createCriteria(ClTissueStage.class)
				.add(Restrictions.eq("idTissueStage", id) )
				.uniqueResult();
		return result;
	}
	
	/** ==================================================================== */
	
	public ClTissueStage find(String stage) {
		ClTissueStage result = (ClTissueStage) session
				.createCriteria(ClTissueStage.class)
				.add(Restrictions.eq("stage", stage) )
				.uniqueResult();
		return result;
	}
	
	/** ==================================================================== */
}
