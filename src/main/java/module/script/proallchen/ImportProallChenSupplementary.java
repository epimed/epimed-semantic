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

import config.MongoUtil;
import module.BaseModule;
import service.ExcelService;

public class ImportProallChenSupplementary extends BaseModule {

	private Date today = new Date();

	public ImportProallChenSupplementary () {


		// ===== Connection =====

		MongoClient mongoClient = MongoUtil.buildMongoClient();
		MongoDatabase db = mongoClient.getDatabase("epimed_experiments");

		// ===== Samples ======

		MongoCollection<Document> collectionSamples = db.getCollection("samples");

		// ===== Excel data loader =====

		String inputfile = this.getInputDirectory() + this.getDirSeparator() + "DB_ALL_JIN_RNASEC_clinical_data_supplementary.xlsx";
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

			// ===== Sample Document =====

			Document docSample = new Document();

			docSample
			.append("_id", idSample)
			.append("main_gse_number", idSeries)
			.append("series", listSeries)
			.append("organism", "Homo sapiens")
			.append("submission_date", today)
			.append("last_update", today)
			.append("import_date", today)
			.append("analyzed", false)
			;

			// ===== Mandatory parameters =====
			Document expGroup = generateExpGroup(idSeries, idSample);
			docSample.append("exp_group", expGroup);


			// ===== Supplementary parameters =====

			Document parameters = new Document();
			parameters.append("id_sample", idSample);

			// === Attributes ===

			for (int j=0; j<excelService.getHeader().size(); j++) {

				String header = (String) excelService.getHeader().get(j);
				Object value = line.get(j);
				// System.out.println(header + " = " + value);

				parameters.append(header, value);

			}
			docSample.append("parameters", parameters);

			System.out.println(docSample);
			

			// === Delete if already exist ===
			collectionSamples.deleteOne(eq("_id", idSample));

			// ===== Insert data =====
			collectionSamples.insertOne(docSample);
			
		}


		mongoClient.close();	
	}

	/** =============================================================== */


	public Document generateExpGroup(String gse, String gsm) {


		Document expGroup = new Document();

		expGroup
		.append("id_sample", gsm)
		.append("main_gse_number", gse)
		.append("id_platform", null)
		.append("sample_title", null)
		.append("sample_source", null)
		.append("sex", null)
		.append("ethnic_group", null)
		.append("age_min", null)
		.append("age_max", null)
		.append("id_tissue_stage", null)
		.append("tissue_stage", null)
		.append("id_tissue_status", null)
		.append("tissue_status", null)
		.append("id_pathology", null)
		.append("pathology", null)
		.append("collection_method", null)
		.append("id_topology", null)
		.append("topology", null)
		.append("id_topology_group", null)
		.append("topology_group", null)
		.append("id_morphology", null)
		.append("morphology", null)
		.append("histology_type", null)
		.append("histology_subtype", null)
		.append("t", null)
		.append("n", null)
		.append("m", null)
		.append("tnm_stage", null)
		.append("tnm_grade", null)
		.append("dfs_months", null)
		.append("os_months", null)
		.append("relapsed", null)
		.append("dead", null)
		.append("treatment", null)
		.append("exposure", null)
		;

		return expGroup;
	}


	/** =============================================================== */

	public static void main(String[] args) {
		new ImportProallChenSupplementary();
	}

	/** ============================================================== */

}
