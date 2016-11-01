package module.interoperability.dispatcher;

import java.util.List;
import java.util.Map;

import org.bson.Document;
import org.hibernate.Session;

import model.entity.ClCellLine;
import model.entity.ClMorphology;
import model.entity.ClTissueStatus;

public class DispatcherMorphology extends DispatcherAbstract {

	/** ================================================================================= */

	public DispatcherMorphology(Session session) {
		super(session);
	}

	/** ================================================================================= 
	 * @throws DispatcherException */

	public void create(Document doc, Map<String, List<Object>> mapOntologyObjects) throws DispatcherException {

		boolean isFound = false;
		ClMorphology morphology = (ClMorphology) makeChoice(mapOntologyObjects, "morphology");
		isFound = !(morphology==null);

		// ===== Try cell line if not found =====
		if (!isFound) {
			ClCellLine cellLine = (ClCellLine) makeChoice(mapOntologyObjects, "cell_line");
			if (cellLine!=null && cellLine.getClMorphology()!=null) {
				morphology = cellLine.getClMorphology();
			}
			isFound = !(morphology==null);
		}
		

		// ===== Check tissue status =====
		// If tissue status is normal or is not tumoral than the morphology can not be used
		ClTissueStatus tissueStatus =  (ClTissueStatus) makeChoice(mapOntologyObjects, "tissue_status");
		if (isFound && tissueStatus!=null && tissueStatus.getIdTissueStatus()<=2) {
			// throw new DispatcherException("Morphology " + morphology + " doesn't match with tissue status " + tissueStatus);
			displayMessage("WARNING! Morphology " + morphology + " doesn't match with tissue status " + tissueStatus + ". Morphology is set to NULL.");
			morphology = null;
			isFound = true;
		}


		if (!isFound) {		
			displayMessage("WARNING! Morphology is not recognized.");
		}
		
		if (morphology!=null) {
			doc.put("id_morphology", morphology.getIdMorphology());
			doc.put("morphology", morphology.getName());
		}
		
	}

	/** ================================================================================= */



}
