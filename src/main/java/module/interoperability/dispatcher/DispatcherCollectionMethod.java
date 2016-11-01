package module.interoperability.dispatcher;

import java.util.List;
import java.util.Map;

import org.bson.Document;
import org.hibernate.Session;

import model.entity.ClCellLine;
import model.entity.ClCollectionMethod;

public class DispatcherCollectionMethod extends DispatcherAbstract {

	/** ================================================================================= */

	public DispatcherCollectionMethod(Session session) {
		super(session);
	}

	/** ================================================================================= 
	 * @throws DispatcherException */

	public void create(Document doc, Map<String, List<Object>> mapOntologyObjects) throws DispatcherException {

		ClCollectionMethod collectionMethod = (ClCollectionMethod) makeChoice(mapOntologyObjects, "collection_method");


		// ===== Cell line =====
		if (collectionMethod==null) {
			ClCellLine cellLine = (ClCellLine) makeChoice(mapOntologyObjects, "cell_line");
			if (cellLine!=null) {
				collectionMethod = session.get(ClCollectionMethod.class, "cell line");
			}
		}


		// ===== Biopsy by default =====
		if (collectionMethod==null) {
			collectionMethod = session.get(ClCollectionMethod.class, "biopsy");
			displayMessage("WARNING! Collection method is not recognized. Set by default " + collectionMethod + ".");

		}

		if (collectionMethod!=null) {
			doc.put("collection_method", collectionMethod.getCollectionMethod());
		}

	}

	/** ================================================================================= */



}
