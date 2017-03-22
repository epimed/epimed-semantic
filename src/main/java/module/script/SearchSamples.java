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
import java.util.List;

import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;

import config.MongoUtil;
import module.BaseModule;

public class SearchSamples extends BaseModule {

	public SearchSamples () {


		// ===== Connection =====

		MongoClient mongoClient = MongoUtil.buildMongoClient();
		MongoDatabase db = mongoClient.getDatabase("epimed_experiments");
		MongoCollection<Document> collectionSamples = db.getCollection("samples");

		List<Document> list = collectionSamples
				.find(Filters.and(
						Filters.ne("exp_group.dead", null)
						,
						Filters.eq("exp_group.id_topology_group", "C50")
						)
						)
				.into(new ArrayList<Document>());
		
		System.out.println("Found " + list.size() + " samples");
		
		for (int i=0; i<list.size(); i++) {
			Document doc = list.get(i); 
			System.out.println(doc);
		}

		mongoClient.close();	

	}

	/** =============================================================== */

	public static void main(String[] args) {
		new SearchSamples();
	}


	/** =============================================================== */

}
