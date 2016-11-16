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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bson.Document;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.result.UpdateResult;

import config.HibernateUtil;
import config.MongoUtil;
import model.entity.ClPathology;
import model.entity.ClTissueStatus;
import module.BaseModule;
import service.ExcelService;

public class ImportSupplementaryGSE25219 extends BaseModule {

	@SuppressWarnings({ "unused", "unchecked" })
	public ImportSupplementaryGSE25219 () {


		// ===== Session PostgreSQL =====
		SessionFactory sessionFactory = HibernateUtil.buildSessionFactory("config/epimed_semantic.hibernate.cfg.xml");
		Session session = sessionFactory.openSession();

		// ===== Session Mongo =====

		MongoClient mongoClient = MongoUtil.buildMongoClient();
		MongoDatabase db = mongoClient.getDatabase("epimed_experiments");

		MongoCollection<Document> collection = db.getCollection("samples");

		// ===== Excel data loader =====

		String inputfile = this.getInputDirectory() + this.getDirSeparator() + "NIHMS321722-supplement-7.xlsx";
		System.out.println("LOADING \t " + inputfile);
		ExcelService excelService = new ExcelService();
		excelService.load(inputfile);

		// ===== Format raw data into data structures ======

		List < Map <String, String> > listMap = new ArrayList<Map <String, String>>();
		List <String> headerMap = new ArrayList<String>();
		Map <String, String> mapBrain = new HashMap <String, String> ();

		for (int i=0; i<excelService.getData().size(); i++) {
			List<Object> dataLine = excelService.getData().get(i);

			String brainCode = (String) dataLine.get(0);
			if (brainCode!=null) {
				mapBrain = new HashMap <String, String> ();
			}

			// Existing brain code
			if (dataLine!=null  && dataLine.size()>2 && dataLine.get(1)!=null && dataLine.get(2)!=null) {
				// System.out.println(dataLine.get(1) + " = " + dataLine.get(2));
				mapBrain.put(dataLine.get(1).toString().trim(), dataLine.get(2).toString().trim());
			}


			if (brainCode!=null) {
				// New Brain code

				// System.out.println("brain code " + brainCode);
				headerMap.add(brainCode);
				listMap.add(mapBrain);
			}
		}


		// ===== Recognize data =====

		for (int i=0; i<headerMap.size(); i++) {
			System.out.println("----------------------------");
			String code = headerMap.get(i);
			System.out.println(i +  " " + code);
			Map <String, String> map = listMap.get(i);

			
			Map <String, String> updatedMap = new HashMap <String, String> ();

			for (Map.Entry<String, String> entry : map.entrySet()) {
				String key = entry.getKey();
				String value = entry.getValue();

				
				if (!key.toLowerCase().equals("age") 
						// && !key.toLowerCase().equals("ethnicity")
						// && !key.toLowerCase().equals("sex")
						&& !value.toLowerCase().equals("no data")) {
					updatedMap.put(key, value);
				}
						
				// System.out.println(key + " -> " + value);
			}

			List<Document> listDocuments = collection.find(
					Filters.and(
							Filters.eq("exp_group.main_gse_number", "GSE25219"), 
							Filters.eq("parameters.brain code", code))
					)
					.into(new ArrayList<Document>());
			System.out.println("Number of corresponding Mongo documents " + listDocuments.size());
			System.out.println(updatedMap);
			
			
			for (int j=0; j<listDocuments.size(); j++) {
				Document doc = listDocuments.get(j);
				
				Document parameters = (Document) doc.get("parameters");
				parameters.putAll(updatedMap);
				System.out.println("\t" + parameters);
				
				// Update Mongo document
				doc.put("parameters", parameters);
				doc.put("analyzed", true);
				UpdateResult updateResult = collection.updateOne(Filters.eq("_id", doc.get("_id")), new Document("$set", doc));
				
			}
			
		}

		

		if (session.isOpen()) {session.close();}
		sessionFactory.close();

		mongoClient.close();	
	}

	/** =============================================================== */

	public static void main(String[] args) {
		new ImportSupplementaryGSE25219();
	}

	/** ============================================================== */

}
