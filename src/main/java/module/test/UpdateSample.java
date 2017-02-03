package module.test;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.result.UpdateResult;

import config.MongoUtil;

public class UpdateSample {

	private String gseNumber = "GSE30219";
	private boolean commit = false;


	public UpdateSample() {

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
			Document parameters = (Document) doc.get("parameters");
			
			System.out.println(i + " " + doc.get("_id") + " " + doc.get("analyzed") + " " + parameters);
			
			if (commit) {
				// UpdateResult updateResult = collection.updateOne(Filters.eq("_id", doc.get("_id")), new Document("$set", doc));
			}
		}

	}
	
	/** =============================================================== */

	public static void main(String[] args) {
		new UpdateSample();
	}

	/** ============================================================== */


}
