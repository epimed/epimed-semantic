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
import model.bind.NcbiGeoGse;
import model.bind.NcbiGeoGsm;
import service.WebService;

import static com.mongodb.client.model.Filters.*;

public class ImportGeo {


	private String [] listGseNumber = {"GSE9014"};

	private WebService webService = new WebService();
	private Date today = new Date();

	public ImportGeo () {

		// ===== Connection =====

		MongoClient mongoClient = MongoUtil.buildMongoClient();
		MongoDatabase db = mongoClient.getDatabase("epimed_experiments");
		// MongoDatabase db = mongoClient.getDatabase("geo");

		// ===== Insert data =====

		for (int k=0; k<listGseNumber.length; k++) {


			String gseNumber = listGseNumber[k];

			System.out.println("------------------------------------------");
			System.out.println(k + " Import " + gseNumber);

			// ===== Load GSE =====
			NcbiGeoGse gse = new NcbiGeoGse(webService.loadGeo(gseNumber));	
			System.out.println(gse);

			// ===== Series =====
			MongoCollection<Document> collectionSeries = db.getCollection("series");

			Document docSeries = new Document();
			docSeries
			.append("_id", gse.getGseNumber())
			.append("title", gse.getTitle())
			.append("platforms", gse.getListGpl())
			.append("submission_date", gse.getSubmissionDate())
			.append("last_update", gse.getLastUpdate())
			.append("import_date", today)
			;

			UpdateResult updateResult = collectionSeries.updateOne(Filters.eq("_id", gse.getGseNumber()), new Document("$set", docSeries));
			if (updateResult.getMatchedCount()==0) {
				collectionSeries.insertOne(docSeries);
			}

			// ===== Platforms =====

			MongoCollection<Document> collectionPlatforms = db.getCollection("platforms");

			for (int i=0; i<gse.getListGpl().size(); i++) {

				NcbiGeoGpl gpl = new NcbiGeoGpl(webService.loadGeo(gse.getListGpl().get(i)));

				System.out.println("\t Import platform " + gpl.getGplNumber());

				Document docPlatforms = new Document();
				docPlatforms
				.append("_id", gpl.getGplNumber())
				.append("title", gpl.getTitle())
				.append("id_organism", gpl.getTaxid())
				.append("organism", gpl.getOrganism())
				.append("manufacturer", gpl.getManufacturer())
				.append("submission_date", gpl.getSubmissionDate())
				.append("last_update", gpl.getLastUpdate())
				.append("import_date", today)
				;

				UpdateResult res = collectionPlatforms.updateOne(Filters.eq("_id", gpl.getGplNumber()), new Document("$set", docPlatforms));
				if (res.getMatchedCount()==0) {
					collectionPlatforms.insertOne(docPlatforms);
				}
			}


			// ===== Samples ======

			MongoCollection<Document> collectionSamples = db.getCollection("samples");

			// for (int i=0; i<1; i++) {
			for (int i=0; i<gse.getListGsm().size(); i++) {

				NcbiGeoGsm gsm = new NcbiGeoGsm(webService.loadGeo(gse.getListGsm().get(i)));

				Document docSampleExist = collectionSamples.find(Filters.eq("_id", gsm.getGsmNumber())).first();
				boolean docAlreadyExist = docSampleExist!=null;

				boolean analysed = false;

				if (docAlreadyExist) {
					analysed = (Boolean) docSampleExist.get("analyzed");
					System.out.println(i + "/" + gse.getListGsm().size() + "\t " + gse.getGseNumber() + "\t " + gsm.getGsmNumber() + ":  already exists in the database, analyzed=" + analysed);
				}
				else {
					System.out.println(i + "/" + gse.getListGsm().size() + "\t " +  gse.getGseNumber() + "\t " + gsm.getGsmNumber());
				}

				// ===== Sample Document =====

				Document docSample = new Document();

				docSample
				.append("_id", gsm.getGsmNumber())
				.append("main_gse_number", gse.getGseNumber())
				.append("series", gsm.getListGse())
				.append("organism", gsm.getOrganism())
				.append("submission_date", gsm.getSubmissionDate())
				.append("last_update", gsm.getLastUpdate())
				.append("import_date", today)
				.append("analyzed", analysed)
				;

				// ===== Mandatory parameters =====

				// Preserve "exp_group" if the document exists already

				Document expGroup = null;
				if (docAlreadyExist) {
					expGroup = (Document) docSampleExist.get("exp_group");
				}
				else {
					expGroup = generateExpGroup(gse, gsm);
				}
				docSample.append("exp_group", expGroup);

				// ===== Supplementary parameters =====

				Document parameters = generateParameters(gsm);
				docSample.append("parameters", parameters);


				// === Delete if already exist ===
				collectionSamples.deleteOne(eq("_id", gsm.getGsmNumber()));

				// ===== Insert data =====
				collectionSamples.insertOne(docSample);

			}

		}

		mongoClient.close();	
	}

	/** =============================================================== */


	public Document generateExpGroup(NcbiGeoGse gse, NcbiGeoGsm gsm) {


		Document expGroup = new Document();

		expGroup
		.append("id_sample", gsm.getGsmNumber())
		.append("main_gse_number", gse.getGseNumber())
		.append("id_platform", gsm.getGplNumber())
		.append("sample_title", gsm.getTitle())
		.append("sample_source", gsm.getSourceName())
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

	public Document generateParameters(NcbiGeoGsm gsm) {
		Document parameters = new Document();
		parameters.append("id_sample", gsm.getGsmNumber());
		this.append(parameters, gsm.getDescription());
		this.append(parameters, gsm.getListCharacteristics());
		// parameters.append("extract_protocol", gsm.getExtractProtocol())
		;
		return parameters;
	}

	/** =============================================================== */

	public void append(Document doc, List<String> list) {

		String regex = "[:=]";
		List<String> listText = new ArrayList<String>();

		for (String rawLine : list) {

			String [] lines = rawLine.split("[,;]"); // Split into several entries

			for (String line : lines) {

				line = line.trim();
				
				if (line.contains(":") || line.contains("=")) {

					String [] parts = line.split(regex);

					if (parts!=null && parts.length>1) {

						String key = parts[0].trim();
						key = key.replaceAll("\\.", " ");
						String value = "";

						for (int i=1; i<parts.length; i++) {
							value = value + parts[i].trim();
							if (i!=parts.length-1) {
								value =value  + ": ";
							}
						}
						value = value.trim();
						String existingValue = doc.getString(key);
						if (existingValue!=null && !existingValue.isEmpty()) {
							value = existingValue + ", " + value;
						}
						doc.append(key, value);
					}
				}

				else {
					listText.add(line);
				}
			}
		}

		if (!listText.isEmpty()) {
			doc.append("text", listText.toString().replaceAll("[\\[\\]]", ""));
		}

	}
	/** =============================================================== */

	public static void main(String[] args) {
		new ImportGeo();
	}

	/** ============================================================== */

}
