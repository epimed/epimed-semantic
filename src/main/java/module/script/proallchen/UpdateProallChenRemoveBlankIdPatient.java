package module.script.proallchen;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.result.UpdateResult;

import config.MongoUtil;

public class UpdateProallChenRemoveBlankIdPatient {

	private String gseNumber = "PROALL_CHEN";
	private boolean commit = true;


	public UpdateProallChenRemoveBlankIdPatient() {

		// ===== Session Mongo =====

		MongoClient mongoClient = MongoUtil.buildMongoClient();
		MongoDatabase db = mongoClient.getDatabase("epimed_experiments");

		MongoCollection<Document> collection = db.getCollection("samples");

		List<Document> listDocuments = collection
				.find(Filters.in("series", gseNumber))
				// .find(Filters.and(Filters.in("series", gseNumber), Filters.eq("analyzed", false)))
				.into(new ArrayList<Document>());


		for (int i=0; i<listDocuments.size(); i++) {
			Document doc = listDocuments.get(i);
			Document expGroup = (Document) doc.get("exp_group");
			Document parameters = (Document) doc.get("parameters");
			
			String trimmedIdPatient = expGroup.getString("id_patient").trim();
			
			expGroup.put("id_patient", trimmedIdPatient);
			
			
			doc.put("exp_group", expGroup);
			
			System.out.println(i + " " + doc.get("_id") + " " + doc.get("analyzed") + " " + expGroup);
			
			if (commit) {
				UpdateResult updateResult = collection.updateOne(Filters.eq("_id", doc.get("_id")), new Document("$set", doc));
			}
		}

		mongoClient.close();
	}
	
	/** =============================================================== */

	public static void main(String[] args) {
		new UpdateProallChenRemoveBlankIdPatient();
	}

	/** ============================================================== */


}
