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

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.result.UpdateResult;

import config.MongoUtil;
import model.bind.NcbiGeoGpl;
import service.MongoService;
import service.WebService;

public class ImportPlatform {


	private WebService webService = new WebService();
	private MongoService mongoService = new MongoService();
	private Date today = new Date();

	public ImportPlatform () {

		// ===== Connection =====

		MongoClient mongoClient = MongoUtil.buildMongoClient();
		MongoDatabase db = mongoClient.getDatabase("epimed_experiments");
		MongoCollection<Document> collectionPlatforms = db.getCollection("platforms");
		MongoCollection<Document> collectionSamples = db.getCollection("samples");
		MongoCollection<Document> collectionSeries = db.getCollection("series");

		// ===== Platforms =====


		List<String> listGpl =  collectionSamples.distinct("exp_group.id_platform", String.class).into(new ArrayList<String>());

		for (String idPlatform : listGpl) {
			Document doc = collectionPlatforms.find(Filters.in("_id", idPlatform)).first();
			if (doc.getString("type")==null) {
				System.out.println(idPlatform + ": " + doc);
			}
		}


		mongoClient.close();
	}


	/** =============================================================== */

	public static void main(String[] args) {
		new ImportPlatform();
	}

	/** ============================================================== */

}
