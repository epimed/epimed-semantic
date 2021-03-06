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
package module.script;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bson.Document;
import org.bson.conversions.Bson;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;

import config.MongoUtil;
import module.BaseModule;
import service.FormatService;

public class CorrectImportedData extends BaseModule {

	private String gseNumber = "PRJNA270632";

	public CorrectImportedData () {

		// ===== Service =====
		FormatService formatService = new FormatService ();


		// ===== Session Mongo =====

		MongoClient mongoClient = MongoUtil.buildMongoClient();
		MongoDatabase db = mongoClient.getDatabase("epimed_experiments");


		MongoCollection<Document> collection = db.getCollection("sample");

		Bson filters = Filters.and(Filters.eq("main_gse_number", gseNumber));

		List<Document> listDocuments = collection.find(filters).into(new ArrayList<Document>());

		for (int i=0; i<listDocuments.size(); i++) {

			Document doc = listDocuments.get(i);
			Document expgroup = (Document) doc.get("exp_group");
			Document parameters = (Document) doc.get("parameters");

			expgroup.append("id_tissue_stage", 2);
			expgroup.append("tissue_stage", "fetal");
			
			// Update Mongo document
			doc.put("exp_group", expgroup);
			// doc.put("parameters", parameters);
			doc.put("analyzed", true);

			System.out.println(expgroup);

			collection.updateOne(Filters.eq("_id", doc.getString("_id")), new Document("$set", doc));
		}

		mongoClient.close();	
	}

	/** =============================================================== */

	public static void main(String[] args) {
		new CorrectImportedData();
	}

	/** ============================================================== */

	public void display(List<Document> list) {

		for (Document document : list) {
			System.out.println(document);
		}
	}


	/** ============================================================== */

	public void displayMatrix(List<Object> list) {
		for (Object item : list) {
			Object[] line = (Object[]) item;
			System.out.println(Arrays.toString(line));
		}
	}

}
