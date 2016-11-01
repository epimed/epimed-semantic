package dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import model.entity.ClMorphology;


public class ClMorphologyDao extends BaseDao {

	public ClMorphologyDao(Session session) {
		super(session);
	}

	
	/** ========================================================= */
	
	public ClMorphology find(String id) {
		ClMorphology result = (ClMorphology) session
				.createCriteria(ClMorphology.class)
				.add(Restrictions.eq("idMorphology", id) )
				.uniqueResult();
		return result;
	}
	
	
	/** ========================================================= */
	
	@SuppressWarnings("unchecked")
	public List<ClMorphology> listMorphologyWithSpecialCharacters() {
		Criteria crit = session
				.createCriteria(ClMorphology.class)
				.add( Restrictions.or(
						Restrictions.like("name", "%(%"),
						Restrictions.like("name", "%[%")
					));
		
		return crit.list();
	}
	
	
	/** ========================================================= */
	
}
