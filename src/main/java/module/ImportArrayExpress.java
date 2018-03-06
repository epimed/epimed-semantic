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
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.result.UpdateResult;

import config.MongoUtil;
import model.bind.AESeries;
import service.MongoService;
import service.WebService;

public class ImportArrayExpress {

	private static String columnSeparator = "\t";
	private static String lineSeparator = "\n";
	private static String defaultOrganism = "Homo sapiens";
	private static String defaultPlatform = "rna-seq";

	private String [] listAccessions = {"E-MTAB-2919"};
	private boolean commit = true;
	private boolean formatIdSample = false; // concatenation with accession (recommended true)

	private WebService webService = new WebService();
	private MongoService mongoService = new MongoService();

	public ImportArrayExpress () {

		// ===== Connection =====

		MongoClient mongoClient = MongoUtil.buildMongoClient();
		MongoDatabase db = mongoClient.getDatabase("epimed_experiments");
		MongoCollection<Document> collectionSeries = db.getCollection("series");
		MongoCollection<Document> collectionSamples = db.getCollection("sample");

		// ===== Pattern =====
		String patternText = "\\[[\\p{Print}\\p{Space}]+\\]";;
		Pattern pattern = Pattern.compile(patternText);



		// ===== Series =====

		for (String accession : listAccessions) {

			List<String> accessionAsList = new ArrayList<String>();
			accessionAsList.add(accession);

			String urlString = "https://www.ebi.ac.uk/arrayexpress/files/" + accession + "/" + accession + ".idf.txt";
			System.out.println(urlString);
			String text = webService.loadUrl(urlString);

			String [] parts = text.split(lineSeparator);
			List<String> dataSeries = new ArrayList<String>(Arrays.asList(parts));

			AESeries series = new AESeries(dataSeries);
			System.out.println(series);

			// ===== Check if already imported as a GSE ===== 
			boolean isGseFound = false;
			String gseNumber = null;
			for (String secondaryAccession : series.getListAccessions()) {
				if (secondaryAccession.startsWith("GSE")) {
					gseNumber = secondaryAccession;
					Document gse = db.getCollection("series")
							.find(Filters.eq("_id", secondaryAccession)).first();
					isGseFound = gse!=null;

				}
			}

			int nbImportedSamples = 0;

			if (!isGseFound) {

				// ===== Create Mongo series =====

				Document docSeries = mongoService.createSeries(accession, series.getTitle(), 
						null, series.getSubmissionDate(), series.getSubmissionDate());

				if (series.getListAccessions()!=null && !series.getListAccessions().isEmpty()) {
					docSeries.put("secondary_accessions", series.getListAccessions());
				}

				if (commit) {
					UpdateResult updateResult = collectionSeries.updateOne(Filters.eq("_id", accession), new Document("$set", docSeries));
					if (updateResult.getMatchedCount()==0) {
						collectionSeries.insertOne(docSeries);
					}
				}

				System.out.println(docSeries);

				// ===== Import clinical data =====

				String url = "https://www.ebi.ac.uk/arrayexpress/files/" + accession + "/" + series.getSdrf();
				System.out.println(url);
				String clindata = webService.loadUrl(url);

				String [] clinparts = clindata.split(lineSeparator);
				List<String> data = new ArrayList<String>(Arrays.asList(clinparts));

				// ===== Recognize samples =====

				List<String> header = this.createHeader(data.get(0), pattern);
				System.out.println(header);

				for (int i=1; i<data.size(); i++) {

					Integer nbSamples = data.size()-1;

					Map<String, Object> mapParameters = this.createMapParameters(data.get(i), header);
					String idSample = this.createIdSample(mapParameters);

					if (idSample==null) {
						System.err.println("ERROR: idSample is not recongnized for " + accession);
						System.out.println("Line " + i);
						System.out.println(mapParameters);
						mongoClient.close();
						System.exit(0);
					}
					else {
						if (formatIdSample) {
							idSample = accession + "-" + idSample;
							idSample = idSample.trim().replaceAll(" ", "-");
						}
					}
					idSample = idSample.split(" ")[0].trim();

					// === Organism ===
					String organism = (String) mapParameters.get("organism");
					if (organism==null || organism.isEmpty()) {
						organism = defaultOrganism;
					}

					// === Platform ===
					String platform = (String) mapParameters.get("LIBRARY_STRATEGY");
					if (platform!=null && !platform.isEmpty()) {
						platform = platform.toLowerCase().trim();
					}
					else {
						platform = defaultPlatform;
					}


					Document docSampleExist = collectionSamples.find(Filters.eq("_id", idSample)).first();
					boolean docAlreadyExist = docSampleExist!=null;

					
					boolean analysed = false;

					if (docAlreadyExist) {
						analysed = (Boolean) docSampleExist.get("analyzed");	
					}

					// ===== Sample Document =====

					Document docSample = mongoService.createSample(idSample, (String) docSeries.get("_id"), 
							accessionAsList, organism, (Date) docSeries.get("submission_date"), 
							(Date) docSeries.get("last_update"), analysed);


					Document expGroup = null;
					Document parameters = null;

					// System.out.println("------------------------------------------------------------------");

					if (docAlreadyExist) {
						// === ID sample alredy exists ===
						System.out.println(i + "/" + nbSamples + "\t " + docSeries.get("_id") + "\t " + idSample + ":  already exists in the database, analyzed=" + analysed);
						expGroup = docSampleExist.get("exp_group", Document.class);
						parameters = mongoService.updateParameters(docSampleExist, mapParameters);
					}
					else {
						// === New sample ===
						System.out.println(i + "/" + nbSamples + "\t " + docSeries.get("_id") + "\t " + idSample);
						expGroup = mongoService.createExpGroup(docSample, platform, null, null, organism);
						parameters = mongoService.createParameters(docSample, mapParameters);
						nbImportedSamples ++;
					}

					// === Update sample_title, sample_source, layout ===
					expGroup.put("sample_title", parameters.getString("organism part"));
					expGroup.put("sample_source", parameters.getString("Source Name"));
					expGroup.put("layout", parameters.getString("LIBRARY_LAYOUT"));


					docSample.append("exp_group", expGroup);
					docSample.append("parameters", parameters);



					if (commit) {

						// === Update old if already exist ===
						if (docAlreadyExist) {
							// collectionSamples.deleteOne(eq("_id", idSample));
							collectionSamples.updateOne(Filters.eq("_id", idSample), new Document("$set", docSample));
						}
						else {
							// ===== Insert data =====
							collectionSamples.insertOne(docSample);
						}

						// ===== Update series for platforms =====
						List<String> listPlatforms =  collectionSamples.distinct("exp_group.id_platform", Filters.in("series", accession), String.class)
								.into(new ArrayList<String>());
						docSeries.append("platforms", listPlatforms);
						collectionSeries.updateOne(Filters.eq("_id", accession), new Document("$set", docSeries));
					}

				}

			}
			else {
				System.out.println("GEO accession " +  gseNumber + " corresponding to  " + accession + " exists already. Skip import.");
			}

			System.out.println("Number of imported samples: " + nbImportedSamples);

		}

		mongoClient.close();

	}

	/** =============================================================== */

	public String createIdSample (Map<String, Object> mapParameters) {

		String [] listKeySample = {"Source Name", "BioSD_Sample", "ENA_RUN", "RUN_NAME", "Scan Name"};

		// run_name
		int j=0;
		boolean isFound = false;
		String idSample = null;
		while (!isFound && j<listKeySample.length) {
			idSample = (String) mapParameters.get(listKeySample[j]);
			isFound = idSample!=null;
			j++;
		}

		return idSample;

	}


	/** =============================================================== */

	public Map<String, Object> createMapParameters(String dataline, List<String> header) {

		Map<String, Object> mapParameters = new HashMap<String, Object>();

		String [] parts = dataline.split(columnSeparator);

		for (int i=0; i<parts.length; i++) {
			String current = parts[i];
			String existing = (String) mapParameters.get(header.get(i));
			String merged = mongoService.mergeParameter(current, existing);
			mapParameters.put(header.get(i), merged);
		}

		return mapParameters;
	}


	/** =============================================================== */

	public List<String> createHeader(String headerText, Pattern pattern) {

		List<String> list = new ArrayList<String>();
		String [] parts = headerText.split(columnSeparator);

		for (String part : parts) {
			Matcher matcher = pattern.matcher(part);
			boolean isPatternFound = matcher.find();
			if (isPatternFound) {
				String value =  matcher.group();
				list.add(value.replaceAll("[\\]\\[]", "").trim());
			}
			else {
				list.add(part.trim());
			}
		}

		return list;

	}

	/** =============================================================== */

	public static void main(String[] args) {
		new ImportArrayExpress();
	}

	/** ============================================================== */

}
