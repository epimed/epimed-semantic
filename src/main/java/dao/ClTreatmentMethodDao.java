package dao;

import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import model.entity.ClTreatmentMethod;


public class ClTreatmentMethodDao extends BaseDao {

	public ClTreatmentMethodDao(Session session) {
		super(session);
	}

	public ClTreatmentMethod findById(Integer id) {
		ClTreatmentMethod result = (ClTreatmentMethod) session
				.createCriteria(ClTreatmentMethod.class)
				.add(Restrictions.eq("idTreatmentMethod", id) )
				.uniqueResult();
		return result;
	}
	
	public ClTreatmentMethod findByName(String name) {
		ClTreatmentMethod result = (ClTreatmentMethod) session
				.createCriteria(ClTreatmentMethod.class)
				.add(Restrictions.eq("name", name) )
				.uniqueResult();
		return result;
	}
	
}
