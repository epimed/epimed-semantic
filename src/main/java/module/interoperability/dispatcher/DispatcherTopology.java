package module.interoperability.dispatcher;

import java.util.List;
import java.util.Map;

import org.bson.Document;
import org.hibernate.Session;

import model.entity.ClCellLine;
import model.entity.ClEpimedGroup;
import model.entity.ClTopology;

public class DispatcherTopology extends DispatcherAbstract {

	/** ================================================================================= */

	public DispatcherTopology(Session session) {
		super(session);
	}

	/** ================================================================================= 
	 * @throws DispatcherException */

	public void create(Document doc, Map<String, List<Object>> mapOntologyObjects) throws DispatcherException {

		boolean isFound = false;
		ClTopology topology = (ClTopology) makeChoice(mapOntologyObjects, "topology");
		isFound = !(topology==null);


		// ===== Try cell line if not found =====
		if (!isFound) {
			ClCellLine cellLine = (ClCellLine) makeChoice(mapOntologyObjects, "cell_line");
			if (cellLine!=null && cellLine.getClTopology()!=null) {
				topology = cellLine.getClTopology();
			}

		}
		isFound = !(topology==null);

		
		if (!isFound) {
			displayMessage("WARNING! Topology doesn't exist.");
		}
		

		if (isFound) {
			doc.put("id_topology", topology.getIdTopology());
			doc.put("topology", topology.getName());
			doc.put("id_topology_group", topology.getClTopologyGroup().getIdGroup());
			doc.put("topology_group", topology.getClTopologyGroup().getName());
			
			ClEpimedGroup grp1 = topology.getClEpimedGroup();
			ClEpimedGroup grp2 = grp1.getParent();
			ClEpimedGroup grp3 = grp2.getParent();
			doc.put("tissue_group_level1", grp1.getName());
			doc.put("tissue_group_level2", grp2.getName());
			doc.put("tissue_group_level3", grp3.getName());
			
		}
	}

	/** ================================================================================= */



}
