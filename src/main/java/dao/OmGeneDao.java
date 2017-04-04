package dao;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import javax.persistence.NoResultException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.hibernate.Session;

import model.entity.OmGene;

public class OmGeneDao extends BaseDao {

	private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

	public OmGeneDao(Session session) {
		super(session);
	}

	public OmGene createGene(Integer entrez, String geneSymbol) {
		
		if (entrez==null) {
			return null;
		}

		if (geneSymbol==null) {
			geneSymbol = "unknown";
		}

		OmGene gene = null;

		// Initialization
		gene = new OmGene();
		gene.setIdGene(entrez);
		gene.setTitle("unknown");
		gene.setStatus("unknown");
		gene.setType("unknown");
		gene.setSource("ncbi");
		gene.setRemoved(false);
		gene.setLocusGroup("unknown");
		gene.setGeneSymbol(geneSymbol);

		try {
			gene.setLastUpdate(dateFormat.parse("1900-01-01"));
			gene.setDateModified(dateFormat.parse("1900-01-01"));
		} 
		catch (ParseException e) {
			// nothing to do
		}

		session.saveOrUpdate(gene);

		System.out.println("New gene created " + gene);

		return gene;


	}

	/** ============================================================= */

	public OmGene find(Integer entrez) {
		try {
			CriteriaBuilder builder = session.getCriteriaBuilder();

			CriteriaQuery<OmGene> criteria = builder.createQuery(OmGene.class);
			Root<OmGene> gene = criteria.from(OmGene.class);
			criteria.select(gene).where(builder.equal(gene.get("idGene"), entrez));

			return session.createQuery(criteria).getSingleResult();

		}
		catch (NoResultException ex) {
			return null;
		}
	}
	/** ============================================================= */
}
