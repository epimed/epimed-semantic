package dao;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;

import model.entity.ClOntologyKeyword;


public class ClOntologyKeywordDao extends BaseDao {

	public ClOntologyKeywordDao(Session session) {
		super(session);
	}
	
	/** =================================================*/

	@SuppressWarnings("unchecked")
	public List<ClOntologyKeyword> list() {
		return session
				.createCriteria(ClOntologyKeyword.class)
				.list();
	}
	
	/** =================================================*/

	@SuppressWarnings("unchecked")
	public List<ClOntologyKeyword> listAssembledKeywords() {
		
		
		List<ClOntologyKeyword> list1 = session
				.createCriteria(ClOntologyKeyword.class)
				.list();
		
		String query = "select term as id_keyword, id_category, true as enabled from epimed_semantic.view_ontology_dictionary where character_length(term)>3";
		
		// System.out.println(query);
		
		List<ClOntologyKeyword> list2 = session
				.createSQLQuery(query)
				.addEntity(ClOntologyKeyword.class)
				.list();
		
		List <ClOntologyKeyword> list = new ArrayList<ClOntologyKeyword>();
		list.addAll(list1);
		list.addAll(list2);
		
		
		return list;
	}

	/** =================================================*/
}
