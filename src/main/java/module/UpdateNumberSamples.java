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
import org.bson.conversions.Bson;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;

import config.MongoUtil;

public class UpdateNumberSamples extends BaseModule {

	public UpdateNumberSamples () {

		// ===== Connection =====

		MongoClient mongoClient = MongoUtil.buildMongoClient();
		MongoDatabase db = mongoClient.getDatabase("epimed_experiments");

		MongoCollection<Document> collectionSeries = db.getCollection("series");
		MongoCollection<Document> collectionSamples = db.getCollection("sample");

		// String [] listIdSeries = {"TISSUE_SPECIFIC_GENES_MM"};
		// List<Document> listSeries = collectionSeries.find(Filters.in("_id", listIdSeries)).into(new ArrayList<Document>());
		
		List<Document> listSeries = collectionSeries.find().into(new ArrayList<Document>());
		
		for (Document ser :listSeries) {
			System.out.println(ser);
			
			String idSeries = ser.getString("_id");
			Bson filter = Filters.in("series", idSeries);
			
			Long nbSamples = collectionSamples.count(filter); 
			
			System.out.println(idSeries + " " + nbSamples);
			
			ser.append("nb_samples", nbSamples);
			collectionSeries.updateOne(Filters.eq("_id", idSeries), new Document("$set", ser));
			
		}
		

		mongoClient.close();	

	}



	/** =============================================================== */

	public static void main(String[] args) {
		new UpdateNumberSamples();
	}

	/** ============================================================== */

}
