package dao;

import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import model.entity.ClTreatmentMethod;


public class ClTreatementMethodDao extends BaseDao {

	public ClTreatementMethodDao(Session session) {
		super(session);
	}

	public ClTreatmentMethod find(Integer id) {
		ClTreatmentMethod result = (ClTreatmentMethod) session
				.createCriteria(ClTreatmentMethod.class)
				.add(Restrictions.eq("idTreatmentMethod", id) )
				.uniqueResult();
		return result;
	}
	
	public ClTreatmentMethod find(String name) {
		ClTreatmentMethod result = (ClTreatmentMethod) session
				.createCriteria(ClTreatmentMethod.class)
				.add(Restrictions.eq("name", name) )
				.uniqueResult();
		return result;
	}
	
}
