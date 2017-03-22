package dao;

import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.hibernate.Session;

import model.entity.ClOntologyDictionary;
import model.entity.ViewOntologyDictionary;

public class ClOntologyDictionaryDao extends BaseDao {

	public ClOntologyDictionaryDao(Session session) {
		super(session);
	}

	/** =================================================*/


	public List<ClOntologyDictionary> list(String term, String idCategory) {

		CriteriaBuilder builder = session.getCriteriaBuilder();
		CriteriaQuery<ClOntologyDictionary> criteria = builder.createQuery(ClOntologyDictionary.class);
		Root<ClOntologyDictionary> root = criteria.from(ClOntologyDictionary.class);
		criteria.select(root).where(
				builder.and(
						builder.equal(root.get("id").get("term"), term),
						builder.equal(root.get("clOntologyCategory").get("idCategory"), idCategory)
						)
				);
		return session.createQuery(criteria).getResultList();
	}

	/** =================================================*/

	public List<ViewOntologyDictionary> listView (String[] terms, String idCategory) {


		CriteriaBuilder builder = session.getCriteriaBuilder();
		CriteriaQuery<ViewOntologyDictionary> criteria = builder.createQuery(ViewOntologyDictionary.class);
		Root<ViewOntologyDictionary> root = criteria.from(ViewOntologyDictionary.class);
		criteria.select(root).where(
				builder.and(
						root.get("id").get("term").in((Object[])terms),
						builder.equal(root.get("id").get("idCategory"), idCategory)
						)
				);
		return session.createQuery(criteria).getResultList();

		// System.out.println("category=" + idCategory + ", terms=" + Arrays.toString(terms));
		// System.out.println("result=" + list);

	}


	/** =================================================*/

	public List<ClOntologyDictionary> list(String[] terms, String idCategory) {

		CriteriaBuilder builder = session.getCriteriaBuilder();
		CriteriaQuery<ClOntologyDictionary> criteria = builder.createQuery(ClOntologyDictionary.class);
		Root<ClOntologyDictionary> root = criteria.from(ClOntologyDictionary.class);
		criteria.select(root).where(
				builder.and(
						root.get("id").get("term").in((Object[])terms),
						builder.equal(root.get("clOntologyCategory").get("idCategory"), idCategory)
						)
				);
		return session.createQuery(criteria).getResultList();

		// System.out.println("result=" + list);
	}

	/** =================================================*/
}
