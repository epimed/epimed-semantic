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
package module.test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
						Filters.or(
								// Filters.ne("exp_group.dead", null),
								// Filters.ne("exp_group.relapsed", null),
								Filters.ne("exp_group.dfs_months", null),
								Filters.ne("exp_group.os_months", null)
								),
						// Filters.exists("exp_group.triple_negative"),
						Filters.eq("exp_group.id_topology", "C50.9"),
						Filters.ne("exp_group.id_morphology", null)
						// Filters.eq("exp_group.id_platform", "GPL570")
						)
						)
				.into(new ArrayList<Document>());

		

		Set<String> setGse = new HashSet<String>();
		List<String> listGse = new ArrayList<String>();
		
		Set<String> setGpl = new HashSet<String>();
		List<String> listGpl = new ArrayList<String>();
		
		for (int i=0; i<list.size(); i++) {
			Document doc = list.get(i); 
			setGse.add(doc.getString("main_gse_number"));
			setGpl.add( ((Document) doc.get("exp_group")).getString("id_platform"));
		}
		listGse.addAll(setGse);
		Collections.sort(listGse);
		listGpl.addAll(setGpl);
		Collections.sort(listGpl);
		
		System.out.println("Found " + list.size() + " samples");
		System.out.println("List GSE " + listGse.size() + ": " + listGse);
		System.out.println("List GPL " + listGpl.size() + ": " + listGpl);

		mongoClient.close();	

	}

	/** =============================================================== */

	public static void main(String[] args) {
		new SearchSamples();
	}


	/** =============================================================== */

}
