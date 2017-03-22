package dao;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.hibernate.Session;

import model.entity.ClOntologyKeyword;


public class ClOntologyKeywordDao extends BaseDao {

	public ClOntologyKeywordDao(Session session) {
		super(session);
	}
	
	/** =================================================*/

	public List<ClOntologyKeyword> list() {
	
		CriteriaBuilder builder = session.getCriteriaBuilder();
		CriteriaQuery<ClOntologyKeyword> criteria = builder.createQuery(ClOntologyKeyword.class);
		Root<ClOntologyKeyword> root = criteria.from(ClOntologyKeyword.class);
		criteria.select(root).where(builder.isTrue(root.get("enabled")));
		return session.createQuery(criteria).getResultList();
		
	}
	
	/** =================================================*/

	public List<ClOntologyKeyword> listAssembledKeywords() {
		
		
		List<ClOntologyKeyword> list1 = this.list();
		
		String query = "select term as id_keyword, id_category, true as enabled from epimed_semantic.view_ontology_dictionary where character_length(term)>3";

		List<ClOntologyKeyword> list2 = session.createNativeQuery(query,ClOntologyKeyword.class).getResultList();
	
		List <ClOntologyKeyword> list = new ArrayList<ClOntologyKeyword>();
		list.addAll(list1);
		list.addAll(list2);
		
		
		return list;
	}

	/** =================================================*/
}
