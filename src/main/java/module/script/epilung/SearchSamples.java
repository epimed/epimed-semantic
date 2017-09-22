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
package module.script.epilung;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bson.Document;
import org.bson.conversions.Bson;

import com.mongodb.Block;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Accumulators;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;

import config.MongoUtil;
import module.BaseModule;

public class SearchSamples extends BaseModule {

	public SearchSamples () {


		// ===== Connection =====

		MongoClient mongoClient = MongoUtil.buildMongoClient();
		MongoDatabase db = mongoClient.getDatabase("epimed_experiments");
		MongoCollection<Document> collectionSamples = db.getCollection("samples");
		MongoCollection<Document> collectionPlatforms = db.getCollection("platforms");


		Bson filters = Filters.and(
				Filters.in("exp_group.id_platform", new String [] {"GPL13534", "GPL8490", "GPL21145"}),
				Filters.eq("exp_group.id_tissue_status", 1),
				Filters.ne("exp_group.id_topology", null)
				);

		/*
		List<Document> list = collectionSamples
				.find(filters)
				.into(new ArrayList<Document>());
		*/

		List<Document> list = collectionSamples
				.aggregate(
						Arrays.asList(
								Aggregates.match(filters),
								Aggregates.group("$exp_group.topology", Accumulators.sum("total", 1)),
								Aggregates.sort(Sorts.orderBy(Sorts.descending("total")))
								))
				.into(new ArrayList<Document>());
		 
		for (int i=0; i<list.size(); i++) {
			System.out.println((i+1) + " " + list.get(i));
		}

		collectionPlatforms.find(Filters.regex("title", ".*ethyl.*")).forEach(printBlock);

		mongoClient.close();	

	}

	/** =============================================================== */

	public static void main(String[] args) {
		new SearchSamples();
	}


	/** =============================================================== */

	Block<Document> printBlock = new Block<Document>() {
		public void apply(final Document document) {
			System.out.println(document.toJson());
		}
	};

	/** =============================================================== */


}
