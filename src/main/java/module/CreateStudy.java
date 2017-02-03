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

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.result.UpdateResult;

import config.MongoUtil;
import service.ExcelService;

public class CreateStudy extends BaseModule {

	private Date today = new Date();
	private Set<String> setGpl = new HashSet<String>();

	@SuppressWarnings("unchecked")
	public CreateStudy() {


		// ===== Connection =====

		MongoClient mongoClient = MongoUtil.buildMongoClient();
		MongoDatabase db = mongoClient.getDatabase("epimed_experiments");

		// === Excel data loader ===

		String inputfile = this.getInputDirectory() + this.getDirSeparator() + "prolung2_expgrp4.xlsx";
		System.out.println("LOADING \t " + inputfile);
		ExcelService excelService = new ExcelService();
		excelService.load(inputfile);
		List<Object> listCel = excelService.extractColumn(0);

		Integer indCel = excelService.getHeaderMap().get("gse8894_sample_cel");

		// ===  New Series === 
		MongoCollection<Document> collectionSeries = db.getCollection("series");
		Document docSeries = new Document();
		docSeries
		.append("_id", "PROLUNG")
		.append("title", "Lung cancerous and non-cancerous samples")
		.append("platforms", null)
		.append("submission_date", today)
		.append("last_update", today)
		.append("import_date", today)
		;

		UpdateResult updateResult = collectionSeries.updateOne(Filters.eq("_id", docSeries.get("_id")), new Document("$set", docSeries));
		if (updateResult.getMatchedCount()==0) {
			collectionSeries.insertOne(docSeries);
		}

		// === Add samples to new series ===
		MongoCollection<Document> collectionSamples = db.getCollection("samples");
		for (int i=0; i<listCel.size(); i++) {

			String gsm = this.getGsm(listCel.get(i));

			Document docSample = collectionSamples.find(Filters.eq("_id", gsm)).first();

			if (docSample == null) {
				System.err.println("ERROR! Sample " + gsm + "doesn't exist. Try another column.");

				gsm = this.getGsm(excelService.getData().get(i).get(indCel));
				docSample = collectionSamples.find(Filters.eq("_id", gsm)).first();

				if (docSample==null) {
					System.err.println("ERROR! Sample " + gsm + " doesn't exist. Exit.");
					System.exit(0);
				}
				else {
					System.err.println("Found " + gsm);
				}
			}

			Document expGroup = (Document) docSample.get("exp_group");
			setGpl.add(expGroup.get("id_platform").toString());
			
			List<String> listSeries = (List<String>) docSample.get("series");
			listSeries.add(docSeries.getString("_id"));
			docSample.put("series", listSeries);

			System.out.println(docSample);
			// updateResult = collectionSamples.updateOne(Filters.eq("_id", docSample.get("_id")), new Document("$set", docSample));
		}


		// === Update platforms of the series ===
		
		System.out.println(setGpl);
		
		docSeries.put("platforms", setGpl);
		updateResult = collectionSeries.updateOne(Filters.eq("_id", docSeries.get("_id")), new Document("$set", docSeries));
		if (updateResult.getMatchedCount()==0) {
			collectionSeries.insertOne(docSeries);
		}

	}

	/** =============================================================== */

	private String getGsm(Object obj) {
		String cel = obj.toString();
		String gsm = cel.toString().trim();
		if (gsm.contains(".") || gsm.contains("_")) {
			gsm = gsm.split("[\\._]")[0];
		}
		return gsm;
	}

	/** =============================================================== */

	public static void main(String[] args) {
		new CreateStudy();
	}

	/** ============================================================== */
}
