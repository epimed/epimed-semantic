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

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;

import config.MongoUtil;

public class UpdateTissueStatus extends BaseModule {

	public UpdateTissueStatus () {

		// ===== Connection =====

		MongoClient mongoClient = MongoUtil.buildMongoClient();
		MongoDatabase db = mongoClient.getDatabase("epimed_experiments");
		MongoCollection<Document> collectionSample = db.getCollection("sample");
		
		List<Document> samples = collectionSample.find().into(new ArrayList<Document>());;
		
		for (Document sample : samples) {
			
			Document expgroup = sample.get("exp_group", Document.class);
			Integer idTissueStatus = expgroup.getInteger("id_tissue_status");
			if (idTissueStatus!=null) {
				if (idTissueStatus.equals(1)) {
					expgroup.put("tissue_status", "normal");
				}
				if (idTissueStatus.equals(2)) {
					expgroup.put("tissue_status", "pathological_non_tumoral");
				}
				if (idTissueStatus.equals(3)) {
					expgroup.put("tissue_status", "tumoral");
				}
				
				sample.put("exp_group", expgroup);
				
				System.out.println(sample.get("exp_group", Document.class));
				
				collectionSample.updateOne(Filters.eq("_id", sample.getString("_id")), new Document("$set", sample));
				
			}
			
		}
		
		mongoClient.close();	

	}



	/** =============================================================== */

	public static void main(String[] args) {
		new UpdateTissueStatus();
	}

	/** ============================================================== */

}
