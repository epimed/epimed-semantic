package module.interoperability.dispatcher;

import java.util.List;
import java.util.Map;

import org.bson.Document;
import org.hibernate.Session;

import model.entity.ClCellLine;
import model.entity.ClMorphology;
import model.entity.ClPathology;
import model.entity.ClTissueStatus;

public class DispatcherPathology extends DispatcherAbstract {

	/** ================================================================================= */

	public DispatcherPathology(Session session) {
		super(session);
	}

	/** ================================================================================= 
	 * @throws DispatcherException */

	public void create(Document doc, Map<String, List<Object>> mapOntologyObjects) throws DispatcherException {

		boolean isFound = false;
		ClPathology pathology = (ClPathology) makeChoice(mapOntologyObjects, "pathology");
		isFound = !(pathology==null);

		// ===== Check tissue status =====
		if (!isFound) {
			ClTissueStatus tissueStatus = (ClTissueStatus) makeChoice(mapOntologyObjects, "tissue_status");

			// === If tissue status is normal than pathology is NULL ===
			if (tissueStatus!=null && tissueStatus.getIdTissueStatus().equals(1)) {
				pathology = null;
				isFound = true;
			}

			// === If tissue status is tumoral than pathology is CANCER ===
			if (tissueStatus!=null && tissueStatus.getIdTissueStatus()>=3) {
				pathology = session.get(ClPathology.class, 1); // Cancer
				isFound = true;
			}
		}


		// ===== Check cell line morphology =====
		if (!isFound) {
			ClCellLine cellLine = (ClCellLine) makeChoice(mapOntologyObjects, "cell_line");
			if (cellLine!=null && cellLine.getClMorphology()!=null) {
				pathology = session.get(ClPathology.class, "1"); // Cancer
			}
			isFound = !(pathology==null);
		}

		// ===== Try from morphology =====
		if (!isFound) {
			ClMorphology  morphology = (ClMorphology) makeChoice(mapOntologyObjects, "morphology");
			if (morphology!=null) {
				pathology = session.get(ClPathology.class, "1"); // Cancer
			}
			isFound = !(pathology==null);
		}


		// ===== No pathology by default =====
		if (!isFound) {
			// Probably normal
			displayMessage("WARNING! Pathology is not recognized. Set NULL by default.");
		}

		if (pathology!=null) {
			doc.put("pathology", pathology.getName());
		}
	}

	/** ================================================================================= */



}
