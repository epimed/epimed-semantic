package dao;

import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.hibernate.Session;

import model.entity.ClCellLine;

public class ClCellLineDao extends BaseDao {

	public ClCellLineDao(Session session) {
		super(session);
	}

	/** ==================================================================== */

	public ClCellLine find(String idCellLine) {
		try {
			CriteriaBuilder builder = session.getCriteriaBuilder();
			CriteriaQuery<ClCellLine> criteria = builder.createQuery(ClCellLine.class);
			Root<ClCellLine> root = criteria.from(ClCellLine.class);
			criteria.select(root).where(builder.equal(root.get("idCellLine"), idCellLine));
			return session.createQuery(criteria).getSingleResult();
		}
		catch (NoResultException e) {
			return null;
		}
	}

	/** ==================================================================== */

	public List<ClCellLine> list() {
		CriteriaBuilder builder = session.getCriteriaBuilder();
		CriteriaQuery<ClCellLine> criteria = builder.createQuery(ClCellLine.class);
		Root<ClCellLine> root = criteria.from(ClCellLine.class);
		criteria.select(root);
		return session.createQuery(criteria).getResultList();
	}

	/** ==================================================================== */

	public List<ClCellLine> listUndefined() {

		CriteriaBuilder builder = session.getCriteriaBuilder();
		CriteriaQuery<ClCellLine> criteria = builder.createQuery(ClCellLine.class);
		Root<ClCellLine> root = criteria.from(ClCellLine.class);
		criteria.select(root).where(
				builder.or(
						builder.isNull(root.get("clMorphology").get("idMorphology")),
						builder.isNull(root.get("clTopology").get("idTopology"))
						)
				);

		return session.createQuery(criteria).getResultList();

	}

	/** ==================================================================== */


	public String getCellLineCode(String name) {
		return name.replaceAll("\\p{Punct}", "").replaceAll("\\p{Space}", "").toLowerCase();
	}

	/** ==================================================================== */

}
