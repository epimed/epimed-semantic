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
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Accumulators;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;

import config.MongoUtil;
import module.BaseModule;
import service.MongoService;

public class CreateProbesets extends BaseModule {

	private MongoService mongoService = new MongoService();

	public CreateProbesets () {


		// ===== Connection =====

		MongoClient mongoClient = MongoUtil.buildMongoClient();
		MongoDatabase db = mongoClient.getDatabase("epimed_experiments");
		MongoCollection<Document> collectionProbesets = db.getCollection("probesets");

		Document docProbeset = mongoService.createProbeset("GPL570", "1007_s_at");
		collectionProbesets.insertOne(docProbeset);
		mongoClient.close();	

	}

	/** =============================================================== */

	public static void main(String[] args) {
		new CreateProbesets();
	}


	/** =============================================================== */

}
