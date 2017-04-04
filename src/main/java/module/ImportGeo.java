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

import static com.mongodb.client.model.Filters.eq;

import java.util.ArrayList;
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
import service.MongoService;
import service.WebService;

public class ImportGeo {


	private String [] listGseNumber = {"GSE61304"};

	private WebService webService = new WebService();
	private MongoService mongoService = new MongoService();

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

			Document docSeries = mongoService.createSeries(gse.getGseNumber(), gse.getTitle(), 
					gse.getListGpl(), gse.getSubmissionDate(), gse.getLastUpdate());
			

			UpdateResult updateResult = collectionSeries.updateOne(Filters.eq("_id", gse.getGseNumber()), new Document("$set", docSeries));
			if (updateResult.getMatchedCount()==0) {
				collectionSeries.insertOne(docSeries);
			}

			
			// ===== Platforms =====

			MongoCollection<Document> collectionPlatforms = db.getCollection("platforms");

			for (int i=0; i<gse.getListGpl().size(); i++) {

				NcbiGeoGpl gpl = new NcbiGeoGpl(webService.loadGeo(gse.getListGpl().get(i)));

				System.out.println("\t Import platform " + gpl.getGplNumber());

				Document docPlatforms = mongoService.createPlatform(gpl.getGplNumber(), gpl.getTitle(), 
						gpl.getTaxid(), gpl.getOrganism(), gpl.getManufacturer(), gpl.getSubmissionDate(), gpl.getLastUpdate());
				

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

				Document docSample = mongoService.createSample(gsm.getGsmNumber(), gse.getGseNumber(), 
						gsm.getListGse(), gsm.getOrganism(), gsm.getSubmissionDate(), gsm.getLastUpdate(), analysed);

				// ===== Mandatory parameters =====

				// Preserve "exp_group" if the document exists already

				Document expGroup = null;
				if (docAlreadyExist) {
					expGroup = (Document) docSampleExist.get("exp_group");
				}
				else {
					expGroup = mongoService.createExpGroup(docSample, gsm.getGplNumber(), gsm.getTitle(), gsm.getSourceName());
					
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

		// String regex = "[:]";
		String regex = "[:=]";
		List<String> listText = new ArrayList<String>();

		for (String rawLine : list) {

			// String [] lines = rawLine.split(","); // Split into several entries
			String [] lines = rawLine.split("[,;]"); // Split into several entries

			// System.out.println("------------------------------------------");
			// System.out.println("rawLine=" + rawLine);
			// System.out.println("lines=" + Arrays.toString(lines));
			
			
			for (String line : lines) {

				line = line.trim();
				
				if (line.contains(":") || line.contains("=")
						) {

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
						// System.out.println(key + "=" + value);
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
