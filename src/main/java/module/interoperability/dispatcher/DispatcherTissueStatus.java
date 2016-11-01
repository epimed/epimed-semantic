package module.interoperability.dispatcher;

import java.util.List;
import java.util.Map;

import org.bson.Document;
import org.hibernate.Session;

import model.entity.ClCellLine;
import model.entity.ClMorphology;
import model.entity.ClPathology;
import model.entity.ClTissueStatus;

public class DispatcherTissueStatus extends DispatcherAbstract {

	/** ================================================================================= */

	public DispatcherTissueStatus(Session session) {
		super(session);
	}

	/** ================================================================================= 
	 * @throws DispatcherException */

	public void create(Document doc, Map<String, List<Object>> mapOntologyObjects) throws DispatcherException {

		boolean isFound = false;
		ClTissueStatus tissueStatus = (ClTissueStatus) makeChoice(mapOntologyObjects, "tissue_status");
		isFound = !(tissueStatus==null);

		
		// ====== Try from pathology =====
		if (!isFound) {
			ClPathology pathology = (ClPathology) makeChoice(mapOntologyObjects, "pathology");
			if (pathology!=null && !pathology.getName().toLowerCase().equals("cancer")) {
				tissueStatus = session.get(ClTissueStatus.class, 2); // Pathological non tumoral
			}
		}
		isFound = !(tissueStatus==null);
		

		// ===== Try cell line if not found =====
		if (!isFound) {
			ClCellLine cellLine = (ClCellLine) makeChoice(mapOntologyObjects, "cell_line");
			if (cellLine!=null && cellLine.getClMorphology()!=null) {
				tissueStatus = session.get(ClTissueStatus.class, 3); // Pathological tumoral
			}
		}
		isFound = !(tissueStatus==null);
		
		
		// ===== Try from morphology =====
		if (!isFound) {
			ClMorphology  morphology = (ClMorphology) makeChoice(mapOntologyObjects, "morphology");
			if (morphology!=null) {
				tissueStatus = session.get(ClTissueStatus.class, 3); // Pathological tumoral primary site
			}
		}
		isFound = !(tissueStatus==null);
		
		// ===== NORMAL by default =====
		if (!isFound) {
			// Normal by default
			tissueStatus = session.get(ClTissueStatus.class, 1); // Normal
			displayMessage("WARNING! Tissue status is not recognized. Set NORMAL TISSUE by default.");

			// throw new DispatcherException("Tissue status doesn't exist in the database.");
		}

		if (tissueStatus!=null) {
			doc.put("id_tissue_status", tissueStatus.getIdTissueStatus());
			doc.put("tissue_status", tissueStatus.getName());
		}

	}

	/** ================================================================================= */



}
