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
package module.script.emtab365;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.result.UpdateResult;

import config.MongoUtil;
import module.BaseModule;
import service.ExcelService;
import service.MongoService;

public class ImportSamplesEMTAB365 extends BaseModule {

	private MongoService mongoService = new MongoService();
	private ExcelService excelService = new ExcelService();

	public ImportSamplesEMTAB365 () {


		// ===== Connection =====

		MongoClient mongoClient = MongoUtil.buildMongoClient();
		MongoDatabase db = mongoClient.getDatabase("epimed_experiments");

		// ===== Collections ======
		MongoCollection<Document> collectionPlatforms = db.getCollection("platforms");
		MongoCollection<Document> collectionSeries = db.getCollection("series");
		MongoCollection<Document> collectionSamples = db.getCollection("samples");

		// ===== Excel data loader =====

		String inputfile = this.getInputDirectory() + this.getDirSeparator() + "E-MTAB-365.sdrf.xlsx";
		System.out.println("LOADING \t " + inputfile);
		excelService.load(inputfile);

		// ===== Init values ======

		String idSeries = "E-MTAB-365";
		List<String> listSeries = new ArrayList<String>();
		listSeries.add(idSeries);
		Document docSeries = collectionSeries.find(Filters.eq("_id", idSeries)).first();
		String organism = "Homo sapiens";

		// ==== Header processing ====

		Map<Integer, String> mapHeader = new HashMap<Integer, String>();
		for (int i=0; i<excelService.getHeader().size(); i++) {
			String headerItem = (String) excelService.getHeader().get(i);
			if (headerItem!=null && headerItem.contains("[")) {
				String [] parts = headerItem.split("[\\[\\]]");
				headerItem = parts[1];
				headerItem = headerItem.replaceAll("[:_\\.]", " ");
			}
			mapHeader.put(i, headerItem.trim());
		}
		System.out.println(mapHeader);



		for (int i=0; i<excelService.getData().size(); i++) {
			// for (int i=0; i<1; i++) {

			List<Object> dataline = excelService.getData().get(i);
			String idSample = (String) dataline.get(0);

			if (!idSample.equals("pool XX")) {

				String idPlatform = ((String) dataline.get(54)).trim();
				if (idPlatform.contains("A-AFFY-44")) {
					idPlatform = "GPL570";
				}
				else {
					Document docPlatform = mongoService.createPlatform(idPlatform, null, "9606", "Homo sapiens",
							null, null, null, null);
					UpdateResult res = collectionPlatforms.updateOne(Filters.eq("_id", docPlatform.getString("_id")), new Document("$set", docPlatform));
					if (res.getMatchedCount()==0) {
						collectionPlatforms.insertOne(docPlatform);
					}
				}

				Document docSample = mongoService.createSample(idSample, idSeries, listSeries, organism,
						(Date) docSeries.get("submission_date"), (Date) docSeries.get("last_update"), false);

				// === exp_group ===

				Document expgroup = mongoService.createExpGroup(docSample, idPlatform, null, null);
				docSample.append("exp_group", expgroup);

				// === parameters ===

				Map<String, Object> mapParameters = new HashMap<String, Object>();

				for (int j=0; j<dataline.size(); j++) {

					String key = mapHeader.get(j);
					Object value = dataline.get(j);

					if (value instanceof String) {
						String valueString = ((String) value).trim();
						if (valueString!=null && !valueString.isEmpty() && !valueString.equals("NA") && !valueString.equals("ND")) {
							value = valueString;
						}
						else {
							value = null;
						}
					}

					if (key!=null && value!=null) {
						mapParameters.put(key, value);
						// System.out.println(key + "='" + value+"'");
					}
				}

				Document parameters = mongoService.createParameters(docSample, mapParameters);
				docSample.append("parameters", parameters);


				// === Delete if already exist ===
				collectionSamples.deleteOne(Filters.eq("_id", docSample.getString("_id")));

				// ===== Insert data =====
				collectionSamples.insertOne(docSample);

				System.out.println(docSample);

			}	
		}


		mongoClient.close();	
	}



	/** =============================================================== */

	public static void main(String[] args) {
		new ImportSamplesEMTAB365();
	}

	/** ============================================================== */

}
