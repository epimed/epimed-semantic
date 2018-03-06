package dao;

import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.hibernate.Session;

import model.entity.ClEpimedGroup;

public class ClEpimedGroupDao extends BaseDao {

	public ClEpimedGroupDao(Session session) {
		super(session);
	}

	/** ============================================================= */

	public ClEpimedGroup findById(Integer idEpimedGroup) {
		try {
			CriteriaBuilder builder = session.getCriteriaBuilder();
			CriteriaQuery<ClEpimedGroup> criteria = builder.createQuery(ClEpimedGroup.class);
			Root<ClEpimedGroup> root = criteria.from(ClEpimedGroup.class);
			criteria.select(root).where(builder.equal(root.get("id_epimed_group"), idEpimedGroup));
			return session.createQuery(criteria).getSingleResult();
		}
		catch (NoResultException e) {
			return null;
		}
	}

	/** ============================================================= */

	public ClEpimedGroup findByNameAndLevel(String name, int level) {
		try {
			CriteriaBuilder builder = session.getCriteriaBuilder();
			CriteriaQuery<ClEpimedGroup> criteria = builder.createQuery(ClEpimedGroup.class);
			Root<ClEpimedGroup> root = criteria.from(ClEpimedGroup.class);
			criteria.select(root).where(
					builder.and(
							builder.equal(root.get("name"), name),
							builder.equal(root.get("level"), level)
							)
					);
			return session.createQuery(criteria).getSingleResult();
		}
		catch (NoResultException e) {
			return null;
		}
	}

	/** ============================================================= */

	public List<ClEpimedGroup> findAll() {

		CriteriaBuilder builder = session.getCriteriaBuilder();
		CriteriaQuery<ClEpimedGroup> criteria = builder.createQuery(ClEpimedGroup.class);
		Root<ClEpimedGroup> root = criteria.from(ClEpimedGroup.class);
		criteria.select(root);
		criteria.orderBy(builder.asc(root.get("id_epimed_group")));
		return session.createQuery(criteria).getResultList();
	}


	/** ============================================================= */
}
