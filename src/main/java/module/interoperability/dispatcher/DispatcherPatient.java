package module.interoperability.dispatcher;

import java.util.List;
import java.util.Map;

import org.bson.Document;
import org.hibernate.Session;

import model.entity.ClCellLine;
import model.entity.ClTopology;

public class DispatcherPatient extends DispatcherAbstract {

	/** ================================================================================= */

	public DispatcherPatient(Session session) {
		super(session);
	}

	/** ================================================================================= */

	public void create(Document doc, Map<String, List<Object>> mapOntologyObjects) throws DispatcherException {

		String sex = (String) makeChoice(mapOntologyObjects, "sex");
		if (sex!=null && sex.equals("null")) {
			sex = null;
		}
		String ethnicGroup = (String) makeChoice(mapOntologyObjects, "ethnic_group");
		if (ethnicGroup!=null && ethnicGroup.equals("null")) {
			ethnicGroup = null;
		}
		ClCellLine cellLine = (ClCellLine) makeChoice(mapOntologyObjects, "cell_line");

		// ===== Sex  from topology =====
		if (sex==null) {
			ClTopology topology = (ClTopology) makeChoice(mapOntologyObjects, "topology");

			// Female organs
			if (topology!=null && (topology.getIdTopology().startsWith("C51")
					|| topology.getIdTopology().startsWith("C52") 
					|| topology.getIdTopology().startsWith("C53") 
					|| topology.getIdTopology().startsWith("C54") 
					|| topology.getIdTopology().startsWith("C55") 
					|| topology.getIdTopology().startsWith("C56") 
					|| topology.getIdTopology().startsWith("C57") 
					)) {
				sex="F";
			}

			// Male organs
			if (topology!=null && (topology.getIdTopology().startsWith("C60") 
					|| topology.getIdTopology().startsWith("C61") 
					|| topology.getIdTopology().startsWith("C62") 
					|| topology.getIdTopology().startsWith("C63")
					|| topology.getIdTopology().startsWith("E63")
					)) {
				sex="M";
			}
		}

		// === Sex from cell line ====
		if (sex==null && cellLine!=null) {
			sex = cellLine.getSex();
		}


		if (sex==null) {
			this.displayMessage("WARNING! Sex is not recognized.");
		}

		if (sex!=null) {
			doc.put("sex", sex);
		}
		if (ethnicGroup!=null) {
			doc.put("ethnic_group", ethnicGroup);
		}

	}

	/** ================================================================================= */



}
