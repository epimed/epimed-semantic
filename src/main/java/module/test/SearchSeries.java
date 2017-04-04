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

import java.text.ParseException;
import java.text.SimpleDateFormat;
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

public class SearchSeries extends BaseModule {

	private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

	public SearchSeries () throws ParseException {

		// ===== Connection =====

		MongoClient mongoClient = MongoUtil.buildMongoClient();
		MongoDatabase db = mongoClient.getDatabase("epimed_experiments");
		MongoCollection<Document> collectionSamples = db.getCollection("series");


		String [] listSeries = {"E-MTAB-365", "GSE3156", "GSE3744", "GSE4922", "GSE5764", "GSE6367",
				"GSE6883", "GSE8977", "GSE9014", "GSE9195", "GSE10885", "GSE11078",
				"GSE11352", "GSE12080", "GSE12237", "GSE12276", "GSE14017", "GSE16391",
				"GSE17215", "GSE17907", "GSE19615", "GSE21653", "GSE28681", "GSE38867"};


		List<Document> list = collectionSamples
				.find(Filters.in("_id", listSeries))
				.into(new ArrayList<Document>());



		Set<String> setGse = new HashSet<String>();
		List<String> listGse = new ArrayList<String>();
		for (int i=0; i<list.size(); i++) {
			Document doc = list.get(i); 
			setGse.add(doc.getString("_id"));
		}
		listGse.addAll(setGse);
		Collections.sort(listGse);

		System.out.println("Found " + list.size() + " samples");
		System.out.println("List GSE " + listGse);

		mongoClient.close();	

	}

	/** =============================================================== */

	public static void main(String[] args) {
		try {
			new SearchSeries();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	/** =============================================================== */

}
