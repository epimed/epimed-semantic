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
		
		// String query = "(select * from cl_ontology_keyword) union (select term as id_keyword, id_category from view_ontology_dictionary)";
		
		String query = "select term as id_keyword, id_category from epimed_prod.view_ontology_dictionary where character_length(term)>3";
		
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
