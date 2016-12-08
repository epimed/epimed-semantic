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
package module.interoperability.dispatcher;

import java.util.List;
import java.util.Map;

import org.bson.Document;
import org.hibernate.Session;

public class DispatcherFactory {

	Session session;


	public DispatcherFactory(Session session) {
		super();
		this.session = session;
	}
	


	public void getObject(Document doc, Map<String, List<Object>> mapOntologyObjects, String objectType) throws DispatcherException {


		if (objectType!=null) {

			if (objectType.equals("treatment")) {
				(new DispatcherTreatment(session)).create(doc, mapOntologyObjects);
			}
			
			if (objectType.equals("biopatho")) {
				(new DispatcherBiopatho(session)).create(doc, mapOntologyObjects);
			}
			
			if (objectType.equals("exposure")) {
				(new DispatcherExposure(session)).create(doc, mapOntologyObjects);
			}
			
			if (objectType.equals("survival")) {
				(new DispatcherSurvival(session)).create(doc, mapOntologyObjects);
			}
			
			if (objectType.equals("tnm")) {
				(new DispatcherTnm(session)).create(doc, mapOntologyObjects);
			}
			
			if (objectType.equals("patient")) {
				(new DispatcherPatient(session)).create(doc, mapOntologyObjects);
			}
			
			if (objectType.equals("pathology")) {
				(new DispatcherPathology(session)).create(doc, mapOntologyObjects);
			}
			
			
			if (objectType.equals("tissue_status")) {
				(new DispatcherTissueStatus(session)).create(doc, mapOntologyObjects);
			}
			
			if (objectType.equals("tissue_stage")) {
				(new DispatcherTissueStage(session)).create(doc, mapOntologyObjects);
			}
			
			if (objectType.equals("collection_method")) {
				(new DispatcherCollectionMethod(session)).create(doc, mapOntologyObjects);
			}
			
			if (objectType.equals("topology")) {
				(new DispatcherTopology(session)).create(doc, mapOntologyObjects);
			}
			
			if (objectType.equals("morphology")) {
				(new DispatcherMorphology(session)).create(doc, mapOntologyObjects);
			}
			
		}

	}

}
