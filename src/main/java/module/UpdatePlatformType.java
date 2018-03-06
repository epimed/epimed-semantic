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
import java.util.Date;
import java.util.List;

import org.bson.Document;
import org.bson.conversions.Bson;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.result.UpdateResult;

import config.MongoUtil;
import model.bind.NcbiGeoGpl;
import service.MongoService;
import service.WebService;

public class UpdatePlatformType {


	public UpdatePlatformType () {

		// ===== Connection =====

		MongoClient mongoClient = MongoUtil.buildMongoClient();
		MongoDatabase db = mongoClient.getDatabase("epimed_experiments");
		MongoCollection<Document> collectionPlatforms = db.getCollection("platform");
		MongoCollection<Document> collectionSeries = db.getCollection("series");


		// ===== Platforms =====

		Bson filters = Filters.eq("type", "unknown");
		List<Document> listPlatforms =  collectionPlatforms.find(filters).into(new ArrayList<Document>());

		for (Document platform : listPlatforms) {
			System.out.println("--------------------------------------------------------------");

			String idPlatform = platform.getString("_id");
			System.out.println(platform);
			List<Document> listSeries = collectionSeries.find(Filters.in("platforms", idPlatform)).into(new ArrayList<Document>());
			for (Document series: listSeries) {
				System.out.println(" \t -> " + series.getString("_id") + " " + series.getString("title"));
			}

			// platform.append("type", "expression array");
			// collectionPlatforms.updateOne(Filters.eq("_id", idPlatform), new Document("$set", platform));

		}


		mongoClient.close();
	}


	/** =============================================================== */

	public static void main(String[] args) {
		new UpdatePlatformType();
	}

	/** ============================================================== */

}
