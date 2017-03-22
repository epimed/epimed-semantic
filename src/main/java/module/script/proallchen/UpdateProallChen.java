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

public class UpdateProallChen {

	private String gseNumber = "PROALL_CHEN";
	private boolean commit = true;


	public UpdateProallChen() {

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
			
			expGroup.put("histology_type","B-ALL");
			expGroup.put("os_months", parameters.get("os_months"));
			expGroup.put("dfs_months", parameters.get("dfs_months"));
			expGroup.put("id_patient", parameters.get("Patient_ID"));
			expGroup.put("wbc", parameters.get("WBC"));
			
			if (parameters.get("Death")!=null && !parameters.get("Death").equals("NA") && ((Double) parameters.get("Death")==1.0)) {
				expGroup.put("dead", true);
			}
			if (parameters.get("Death")!=null && !parameters.get("Death").equals("NA") && ((Double) parameters.get("Death")==0.0)) {
				expGroup.put("dead", false);
			}
			
			
			if (parameters.get("Relapse")!=null && !parameters.get("Relapse").toString().contains("NA") && ((Double) parameters.get("Relapse")==1.0)) {
				expGroup.put("relapsed", true);
			}
			if (parameters.get("Relapse")!=null && !parameters.get("Relapse").toString().contains("NA") && ((Double) parameters.get("Relapse")==0.0)) {
				expGroup.put("relapsed", false);
			}
			
			if (parameters.get("RNA_seq_analysis")!=null && ((Double) parameters.get("RNA_seq_analysis")==1.0)) {
				expGroup.put("rna_seq", true);
			}
			else {
				expGroup.put("rna_seq", false);
			}
			
			if (parameters.get("CR")!=null && ((Double) parameters.get("CR")==1.0)) {
				expGroup.put("complete_remission", true);
			}
			else {
				expGroup.put("complete_remission", false);
			}
			
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
		new UpdateProallChen();
	}

	/** ============================================================== */


}
