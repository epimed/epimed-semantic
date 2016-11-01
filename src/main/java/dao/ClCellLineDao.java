package dao;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import model.entity.ClCellLine;



public class ClCellLineDao extends BaseDao {

	public ClCellLineDao(Session session) {
		super(session);
	}

	/** ==================================================================== */

	public ClCellLine find(String idCellLine) {

		return (ClCellLine) session
				.createCriteria(ClCellLine.class)
				.add(Restrictions.eq("idCellLine", idCellLine))
				.uniqueResult();

	}

	/** ==================================================================== */

	@SuppressWarnings("unchecked")
	public List<ClCellLine> list() {
		return session
				.createCriteria(ClCellLine.class)
				.list();

	}

	/** ==================================================================== */

	@SuppressWarnings("unchecked")
	public List<ClCellLine> listUndefined() {
		return session
				.createCriteria(ClCellLine.class)
				.add(Restrictions.or(
						Restrictions.isNull("clMorphology.idMorphology"),
						Restrictions.isNull("clTopology.idTopology")
						))
				.list();

	}

	/** ==================================================================== */


	public String getCellLineCode(String name) {
		return name.replaceAll("\\p{Punct}", "").replaceAll("\\p{Space}", "").toLowerCase();
	}

	/** ==================================================================== */

}
