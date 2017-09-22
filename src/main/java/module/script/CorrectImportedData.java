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

import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Accumulators;
import com.mongodb.client.model.Projections;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;
import com.mongodb.client.result.UpdateResult;
import com.mongodb.Block;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import config.MongoUtil;
import module.BaseModule;
import service.FormatService;

public class CorrectImportedData extends BaseModule {

	private String gseNumber = "GSE6791";

	public CorrectImportedData () {

		// ===== Service =====
		FormatService formatService = new FormatService ();


		// ===== Session Mongo =====

		MongoClient mongoClient = MongoUtil.buildMongoClient();
		MongoDatabase db = mongoClient.getDatabase("epimed_experiments");


		MongoCollection<Document> collection = db.getCollection("samples");
		List<Document> listDocuments = collection
				.find(Filters.and(
						Filters.eq("main_gse_number", gseNumber)
						))
				.into(new ArrayList<Document>());

		for (int i=0; i<listDocuments.size(); i++) {

			Document doc = listDocuments.get(i);
			Document expGroup = (Document) doc.get("exp_group");

			String gsmNumber = doc.getString("_id");

			if (expGroup.getString("id_topology").equals("C53.9")) {
				expGroup.put("id_topology", "C76.0");
				expGroup.put("topology", "Head, face or neck, NOS");
				expGroup.put("id_topology_group", "C76");
				expGroup.put("topology_group", "OTHER AND ILL-DEFINED SITES");
				System.out.println(expGroup);


				// Update Mongo document
				doc.put("exp_group", expGroup);
				doc.put("analyzed", true);

				UpdateResult updateResult = collection.updateOne(Filters.eq("_id", gsmNumber), new Document("$set", doc));
			}

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
