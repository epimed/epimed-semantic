package dao;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import model.entity.ClOntologyDictionary;
import model.entity.ViewOntologyDictionary;

public class ClOntologyDictionaryDao extends BaseDao {

	public ClOntologyDictionaryDao(Session session) {
		super(session);
	}

	/** =================================================*/

	@SuppressWarnings("unchecked")
	public List<ClOntologyDictionary> list(String term, String idCategory) {
		return session
				.createCriteria(ClOntologyDictionary.class)
				.add(Restrictions.eq("id.term", term))
				.add(Restrictions.eq("clOntologyCategory.idCategory", idCategory))
				.list();
	}

	/** =================================================*/

	@SuppressWarnings("unchecked")
	public List<ViewOntologyDictionary> listView (String[] terms, String idCategory) {
		
		

		List<ViewOntologyDictionary> list =  session
				.createCriteria(ViewOntologyDictionary.class)
				.add(Restrictions.in("id.term", (Object[]) terms))
				.add(Restrictions.eq("id.idCategory", idCategory))
				.list();

		// System.out.println("category=" + idCategory + ", terms=" + Arrays.toString(terms));
		// System.out.println("result=" + list);

		return list;
	}


	/** =================================================*/

	@SuppressWarnings("unchecked")
	public List<ClOntologyDictionary> list(String[] terms, String idCategory) {


		List<ClOntologyDictionary> list =  session
				.createCriteria(ClOntologyDictionary.class)
				.add(Restrictions.in("id.term", (Object[]) terms))
				.add(Restrictions.eq("clOntologyCategory.idCategory", idCategory))
				.list();


		// System.out.println("result=" + list);

		return list;
	}

	/** =================================================*/
}
