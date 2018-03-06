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


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bson.Document;
import org.bson.conversions.Bson;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;

import config.MongoUtil;
import module.BaseModule;
import service.FormatService;

public class AddSeriesToSamples extends BaseModule {

	public AddSeriesToSamples () {

		// ===== Service =====
		FormatService formatService = new FormatService ();


		// ===== Session Mongo =====

		MongoClient mongoClient = MongoUtil.buildMongoClient();
		MongoDatabase db = mongoClient.getDatabase("epimed_experiments");

		Set<String> setProjects = new HashSet<String>();

		MongoCollection<Document> collection = db.getCollection("sample");

		Bson filters = Filters.and(Filters.in("series", "PRJNA270632"));

		List<Document> listDocuments = collection.find(filters).into(new ArrayList<Document>());

		for (int i=0; i<listDocuments.size(); i++) {

			Document doc = listDocuments.get(i);
			Document expgroup = doc.get("exp_group", Document.class);
			
			if (expgroup.get("exp_Mcount")!=null) {
				
				List<String> projects = doc.get("series", ArrayList.class);
				setProjects.clear();
				setProjects.addAll(projects);
				setProjects.add("TISSUE_SPECIFIC_GENES_HS");
				doc.put("series", setProjects);
				System.out.println(doc.getString("_id") + " " + projects + " -> " + setProjects);
				
				collection.updateOne(Filters.eq("_id", doc.getString("_id")), new Document("$set", doc));
			}
			
			

			
		}

		mongoClient.close();	
	}

	/** =============================================================== */

	public static void main(String[] args) {
		new AddSeriesToSamples();
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
