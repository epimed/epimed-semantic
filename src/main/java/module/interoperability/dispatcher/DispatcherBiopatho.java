package module.interoperability.dispatcher;

import java.util.List;
import java.util.Map;

import org.bson.Document;
import org.hibernate.Session;

import model.entity.ClCellLine;
import model.entity.ClCollectionMethod;


public class DispatcherBiopatho extends DispatcherAbstract {

	/** ================================================================================= */

	public DispatcherBiopatho(Session session) {
		super(session);
	}

	/** ================================================================================= */

	public void create(Document doc, Map<String, List<Object>> mapOntologyObjects) throws DispatcherException {

		String histologyType = null;
		String histologySubtype = null;
		Double ageMin = null;
		Double ageMax = null;

		// ===== Histology type =====
		histologyType = (String) makeChoice(mapOntologyObjects, "histology_type");

		// ===== Histology subtype =====
		histologySubtype = (String) makeChoice(mapOntologyObjects, "histology_subtype");

		// === Cell line ===
		ClCollectionMethod collectionMethod =  (ClCollectionMethod) makeChoice(mapOntologyObjects, "collection_method");
		ClCellLine cellLine = (ClCellLine) makeChoice(mapOntologyObjects, "cell_line");

		if (cellLine!=null) {
			if (cellLine.getHistologyType()!=null) {
				histologyType = cellLine.getHistologyType();
			}
			histologySubtype = cellLine.getIdCellLine();
		}

		// === Isolated cells ===
		String cellName = (String) makeChoice(mapOntologyObjects, "isolated_cells");
		if (cellName!=null && (collectionMethod==null ||  collectionMethod.getCollectionMethod().equals("isolated cells"))) {
			histologySubtype = cellName;
		}


		// ===== Age =====

		// Age is set only if the sample is not cell line nor isolated cells
		if (cellLine==null && cellName==null) {

			List<Object> age = mapOntologyObjects.get("age");

			if (age==null) {
				this.displayMessage("WARNING! Age is not recognized.");
			}

			if (age!=null && age.size()==1) {
				ageMin = (Double) age.get(0);
				ageMax = (Double) age.get(0);
			}

			if (age!=null && age.size()==2) {
				ageMin = (Double) age.get(0);
				ageMax = (Double) age.get(1);
			}

			if (age!=null && age.size()>2) {
				this.displayMessage("WARNING! More than 3 ages are recognized: " + age + ". Skipped.");
			}

		}

		doc.put("age_min", ageMin);
		doc.put("age_max", ageMax);
		doc.put("histology_type", histologyType);
		doc.put("histology_subtype", histologySubtype);


	}

	/** ================================================================================= */



}
