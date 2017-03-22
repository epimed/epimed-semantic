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
package module.script.proallchen;

import static com.mongodb.client.model.Filters.eq;

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
import module.BaseModule;
import service.ExcelService;

public class ImportProallChenOriginal extends BaseModule {

	private Date today = new Date();

	public ImportProallChenOriginal () {


		// ===== Connection =====

		MongoClient mongoClient = MongoUtil.buildMongoClient();
		MongoDatabase db = mongoClient.getDatabase("epimed_experiments");

		// ===== Samples ======

		MongoCollection<Document> collectionSamples = db.getCollection("samples");

		// ===== Excel data loader =====

		String inputfile = this.getInputDirectory() + this.getDirSeparator() + "PROALL_CHEN_clinical.xlsx";
		System.out.println("LOADING \t " + inputfile);
		ExcelService excelService = new ExcelService();
		excelService.load(inputfile);

		System.out.println(excelService.getHeader());

		String idSeries = "PROALL_CHEN";
		List<String> listSeries = new ArrayList<String>();
		listSeries.add(idSeries);

		for (int i=0; i<excelService.getData().size(); i++) {

			List<Object> line = excelService.getData().get(i);

			String idSample = "ESM" + line.get(0);

			System.out.println(idSample + " " + line);

			Document docSample = collectionSamples.find(Filters.eq("_id", idSample.trim())).first();

			System.out.println(docSample);

			Document parameters = (Document) docSample.get("parameters");

			for (int j=0; j<excelService.getHeader().size(); j++) {

				String header = (String) excelService.getHeader().get(j);
				Object value = line.get(j);
				// System.out.println(header + " = " + value);

				parameters.append(header, value);

			}

			System.out.println(parameters);

			// Update Mongo document
			docSample.put("parameters", parameters);
			UpdateResult updateResult = collectionSamples.updateOne(Filters.eq("_id", docSample.get("_id")), new Document("$set", docSample));
		}

		mongoClient.close();	
	}



	/** =============================================================== */

	public static void main(String[] args) {
		new ImportProallChenOriginal();
	}

	/** ============================================================== */

}
