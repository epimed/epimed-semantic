package module.interoperability.dispatcher;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bson.Document;
import org.hibernate.Session;

import dao.ClTissueStageDao;
import model.entity.ClCellLine;
import model.entity.ClTissueStage;

public class DispatcherTissueStage extends DispatcherAbstract {

	/** ================================================================================= */

	public DispatcherTissueStage(Session session) {
		super(session);
	}

	/** ================================================================================= 
	 * @throws DispatcherException */

	public void create(Document doc, Map<String, List<Object>> mapOntologyObjects) throws DispatcherException {

		boolean isFound = false;
		ClTissueStage tissueStage = (ClTissueStage) makeChoice(mapOntologyObjects, "tissue_stage");
		isFound = !(tissueStage==null);

		if (!isFound) {
			ClTissueStageDao tissueStageDao = new ClTissueStageDao(session);
			List<ClTissueStage> listTissueStage = tissueStageDao.list();
			mapOntologyObjects.put("tissue_stage", new ArrayList<Object>());
			for (int i=0; i<listTissueStage.size(); i++) {
				mapOntologyObjects.get("tissue_stage").add(listTissueStage.get(i));
			}
		}


		// ===== Try cell line if not found =====
		if (!isFound) {
			ClCellLine cellLine = (ClCellLine) makeChoice(mapOntologyObjects, "cell_line");
			if (cellLine!=null) {

				if (cellLine.getHistologyType()!=null && cellLine.getHistologyType().contains("embr")) {
					tissueStage = session.get(ClTissueStage.class, 4); // embryonic
				}
				if (cellLine.getHistologyType()!=null && 
						(cellLine.getHistologyType().contains("fetus") || cellLine.getHistologyType().contains("fetal"))) {
					tissueStage = session.get(ClTissueStage.class, 2); // fetal
				}
				else {
					tissueStage = session.get(ClTissueStage.class, 1); // adult;
				}
			}
		}
		isFound = !(tissueStage==null);

		// ===== Default =====
		if (!isFound) {
			tissueStage = session.get(ClTissueStage.class, 1); // adult
			// tissueStage = (ClTissueStage) makeChoice(mapOntologyObjects, "tissue_stage");
		}
		isFound = !(tissueStage==null);

		// === Exception ===
		if (!isFound) {
			throw new DispatcherException("Tissue stage doesn't exist in the database.");
		}

		if (tissueStage!=null) {
			doc.put("id_tissue_stage", tissueStage.getIdTissueStage());
			doc.put("tissue_stage", tissueStage.getStage());
		}

	}

	/** ================================================================================= */



}
