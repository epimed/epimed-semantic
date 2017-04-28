/**
 * EpiMed - Information system for bioinformatics developments in the field of epigenetics
 * 
 * This software is a computer program which performs the data management 
 * for EpiMed platform of the Institute for Advances Biosciences (IAB)
 *
 * Copyright University of Grenoble Alps (UGA)
 * GNU GENERAL PUBLIC LICENSE
 * Please check LICENSE file
 *
 * Author: Ekaterina Bourova-Flin 
 *
 */
package module;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.result.UpdateResult;

import config.MongoUtil;


public class ClearGeoExpGroup extends BaseModule {

	private String gseNumber = "GSE47361";

	private boolean commit = true;


	public ClearGeoExpGroup () {


		// ===== Session Mongo =====

		MongoClient mongoClient = MongoUtil.buildMongoClient();
		MongoDatabase db = mongoClient.getDatabase("epimed_experiments");

		MongoCollection<Document> collection = db.getCollection("samples");
		List<Document> listDocuments = collection
				.find(Filters.in("series", gseNumber))
				.into(new ArrayList<Document>());


		// ===== Analyse ======

		for (int i=0; i<listDocuments.size(); i++) {

			Document doc = listDocuments.get(i);
			String id = doc.getString("_id");
			Document expGroup = (Document) doc.get("exp_group");
			this.clear(expGroup);
			expGroup.remove("er");
			expGroup.remove("pr");
			expGroup.remove("her2");
			expGroup.remove("triple_negative");

			// Update Mongo document
			doc.put("exp_group", expGroup);
			doc.put("analyzed", false);
			if (commit) {
				UpdateResult updateResult = collection.updateOne(Filters.eq("_id", id), new Document("$set", doc));
				
			}

		}

		mongoClient.close();	
	}


	/** =============================================================== */

	private void clear(Document doc) {
		doc.put("sex", null);
		doc.put("ethnic_group", null);
		doc.put("age_min", null);
		doc.put("age_max", null);
		doc.put("id_tissue_stage", null);
		doc.put("tissue_stage", null);
		doc.put("id_tissue_status", null);
		doc.put("tissue_status", null);
		doc.put("id_pathology", null);
		doc.put("pathology", null);
		doc.put("collection_method", null);
		doc.put("id_topology", null);
		doc.put("topology", null);
		doc.put("id_topology_group", null);
		doc.put("topology_group", null);
		doc.put("id_morphology", null);
		doc.put("morphology", null);
		doc.put("histology_type", null);
		doc.put("histology_subtype", null);
		doc.put("t", null);
		doc.put("n", null);
		doc.put("m", null);
		doc.put("tnm_stage", null);
		doc.put("tnm_grade", null);
		doc.put("dfs_months", null);
		doc.put("os_months", null);
		doc.put("relapsed", null);
		doc.put("dead", null);
		doc.put("treatment", null);
		doc.put("exposure", null);
	}

	/** =============================================================== */

	public static void main(String[] args) {
		new ClearGeoExpGroup();
	}

	/** ============================================================== */

}
