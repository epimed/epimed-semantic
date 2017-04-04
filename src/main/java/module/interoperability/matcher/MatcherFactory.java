/**
 * EpiMed - Information system for bioinformatics developments in the field of epigenetics
 * 
 * This software is a computer program which performs the data management 
 * for EpiMed platform of the Institute for Advances Biosciences (IAB)
 *
 * Copyright University of Grenoble Alps (UGA)
 * GNU GENERAL PUBLIC LICENSE
 * Please check LICENSE file
 *
 * Author: Ekaterina Flin 
 *
 */
package module.interoperability.matcher;

import java.util.List;

import org.hibernate.Session;

import model.entity.ClOntologyCategory;


public class MatcherFactory {

	Session session;


	public MatcherFactory(Session session) {
		super();
		this.session = session;
	}

	public MatcherAbstract getMatcher(ClOntologyCategory ontologyCategory, List<String> listLines) {


		if (ontologyCategory!=null) {
			
			if (ontologyCategory.getIdCategory().equals("biomarker")) {
				return new MatcherBiomarker(session, listLines);
			}
			
			if (ontologyCategory.getIdCategory().equals("treatment")) {
				return new MatcherTreatment(session, listLines);
			}
			
			if (ontologyCategory.getIdCategory().equals("tnm")) {
				return new MatcherTnm(session, listLines);
			}
			
			if (ontologyCategory.getIdCategory().equals("histology_subtype")) {
				return new MatcherHistologySubtype(session, listLines);
			}
			
			if (ontologyCategory.getIdCategory().equals("survival")) {
				return new MatcherSurvival(session, listLines);
			}
			
			if (ontologyCategory.getIdCategory().equals("exposure")) {
				return new MatcherExposure(session, listLines);
			}
			
			if (ontologyCategory.getIdCategory().equals("pathology")) {
				return new MatcherPathology(session, listLines);
			}

			if (ontologyCategory.getIdCategory().equals("histology_type")) {
				return new MatcherHistologyType(session, listLines);
			}

			if (ontologyCategory.getIdCategory().equals("isolated_cells")) {
				return new MatcherIsolatedCells(session, listLines);
			}
			
			if (ontologyCategory.getIdCategory().equals("morphology")) {
				return new MatcherMorphology(session, listLines);
			}
			
			if (ontologyCategory.getIdCategory().equals("cell_line")) {
				return new MatcherCellLine(session, listLines);
			}
			
			if (ontologyCategory.getIdCategory().equals("collection_method")) {
				return new MatcherCollectionMethod(session, listLines);
			}
			
			if (ontologyCategory.getIdCategory().equals("tissue_stage")) {
				return new MatcherTissueStage(session, listLines);
			}
			
			if (ontologyCategory.getIdCategory().equals("tissue_status")) {
				return new MatcherTissueStatus(session, listLines);
			}

			if (ontologyCategory.getIdCategory().equals("sex")) {
				return new MatcherSex(session, listLines);
			}
			
			if (ontologyCategory.getIdCategory().equals("age")) {
				return new MatcherAge(session, listLines);
			}

			if (ontologyCategory.getIdCategory().equals("ethnic_group")) {
				return new MatcherEthnicGroup(session, listLines);
			}
			
			if (ontologyCategory.getIdCategory().equals("topology")) {
				return new MatcherTopology(session, listLines);
			}

			if (ontologyCategory.getIdCategory().equals("other")) {
				return new MatcherOther(session, listLines);
			}

		}

		return null;

	}

}
