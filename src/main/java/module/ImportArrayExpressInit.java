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

public class ImportArrayExpressInit {

	private static String columnSeparator = "\t";
	private static String lineSeparator = "\n";
	private static String [] listRunNameParameters = {"RUN_NAME", "ENA_RUN", "Scan Name"};

	private String [] listAccessions = {"E-MTAB-513"};

	private WebService webService = new WebService();
	private MongoService mongoService = new MongoService();

	public ImportArrayExpressInit () {

		// ===== Connection =====

		MongoClient mongoClient = MongoUtil.buildMongoClient();
		MongoDatabase db = mongoClient.getDatabase("epimed_experiments");
		MongoCollection<Document> collectionSeries = db.getCollection("series");
		MongoCollection<Document> collectionSamples = db.getCollection("samples");

		// ===== Pattern =====
		String patternText = "\\[[\\p{Print}\\p{Space}]+\\]";;
		Pattern pattern = Pattern.compile(patternText);



		// ===== Series =====

		for (String accession : listAccessions) {

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
					// System.out.println("GEO accession " +  gseNumber + " found: " + isGseFound);
				}
			}

			if (!isGseFound) {

				// ===== Create Mongo series =====

				List<String> listSeriesAcc = new ArrayList<String>();
				listSeriesAcc.add(accession);

				Document docSeries = mongoService.createSeries(accession, series.getTitle(), 
						null, series.getSubmissionDate(), series.getSubmissionDate());

				if (series.getListAccessions()!=null && !series.getListAccessions().isEmpty()) {
					listSeriesAcc.addAll(series.getListAccessions());
				}

				docSeries.put("accessions", listSeriesAcc);


				UpdateResult updateResult = collectionSeries.updateOne(Filters.eq("_id", accession), new Document("$set", docSeries));
				if (updateResult.getMatchedCount()==0) {
					collectionSeries.insertOne(docSeries);
				}



				System.out.println(docSeries);

				// ===== Import clinical data =====

				String url = "https://www.ebi.ac.uk/arrayexpress/files/" + accession + "/" + series.getSdrf();
				System.out.println(url);
				String clindata = webService.loadUrl(url);

				String [] clinparts = clindata.split(lineSeparator);
				List<String> data = new ArrayList<String>(Arrays.asList(clinparts));

				// ===== Samples =====

				List<String> header = this.createHeader(data.get(0), pattern);
				System.out.println(header);


				for (int i=1; i<data.size(); i++) {

					Integer nbSamples = data.size()-1;

					Map<String, Object> mapParameters = this.createParameters(data.get(i), header);
					String idSample = this.createIdSample(mapParameters);

					if (idSample==null) {
						System.err.println("idSample is not recongnized for " + mapParameters);
						mongoClient.close();
						System.exit(0);
					}


					String organism = (String) mapParameters.get("organism");
					if (organism==null || organism.isEmpty()) {
						organism = "Homo sapiens";
					}
					String platform = (String) mapParameters.get("LIBRARY_STRATEGY");
					if (platform!=null && !platform.isEmpty()) {
						platform = platform.toLowerCase().trim();
					}
					else {
						platform="rna-seq";
					}
					String layout = (String) mapParameters.get("LIBRARY_LAYOUT");
					if (layout!=null && !layout.isEmpty()) {
						layout = layout.toLowerCase().trim();
					}


					Document docSampleExist = collectionSamples.find(Filters.eq("_id", idSample)).first();
					boolean docAlreadyExist = docSampleExist!=null;

					boolean analysed = false;

					if (docAlreadyExist) {
						analysed = (Boolean) docSampleExist.get("analyzed");
						System.out.println(i + "/" + nbSamples + "\t " + docSeries.get("_id") + "\t " + idSample + ":  already exists in the database, analyzed=" + analysed);
					}
					else {
						System.out.println(i + "/" + nbSamples + "\t " + docSeries.get("_id") + "\t " + idSample);
					}

					// ===== Sample Document =====

					Document docSample = mongoService.createSample(idSample, (String) docSeries.get("_id"), 
							listSeriesAcc, organism, (Date) docSeries.get("submission_date"), 
							(Date) docSeries.get("last_update"), analysed);

					// ===== Mandatory parameters =====

					// Preserve "exp_group" if the document exists already

					Document expGroup = null;
					if (docAlreadyExist) {
						expGroup = (Document) docSampleExist.get("exp_group");
					}
					else {
						expGroup = mongoService.createExpGroup(docSample, platform, (String) mapParameters.get("organism part"), (String) mapParameters.get("Source Name"));
						if (layout!=null) {
							expGroup.append("layout", layout);

							
							// run_name
							int j=0;
							boolean isFound = false;
							String runName = null;
							while (!isFound && j<listRunNameParameters.length) {
								runName = (String) mapParameters.get(listRunNameParameters[j]);
								isFound = runName!=null;
								j++;
							}
							if (runName!=null) {
								expGroup.append("run_name", runName);
							}
							
						}
					}


					docSample.append("exp_group", expGroup);

					// ===== Supplementary parameters =====

					Document parameters = mongoService.createParameters(docSample, mapParameters);
					docSample.append("parameters", parameters);


					// === Delete if already exist ===
					collectionSamples.deleteOne(eq("_id", idSample));

					// ===== Insert data =====
					collectionSamples.insertOne(docSample);


					// ===== Update series for platforms =====
					List<String> listPlatforms =  collectionSamples.distinct("exp_group.id_platform", Filters.in("series", accession), String.class)
							.into(new ArrayList<String>());
					docSeries.append("platforms", listPlatforms);
					collectionSeries.updateOne(Filters.eq("_id", accession), new Document("$set", docSeries));

				}

			}
			else {
				System.out.println("GEO accession " +  gseNumber + " corresponding to  " + accession + " exists already. Skip import.");
			}
		}

		mongoClient.close();

	}

	/** =============================================================== */

	public String createIdSample (Map<String, Object> mapParameters) {

		String idSample = null;

		for (Map.Entry<String, Object> entry : mapParameters.entrySet()) {		    

			try {
				String value = (String) entry.getValue();
				if (value!=null && (value.contains(".fastq") || value.contains(".gz"))) {
					String [] parts = value.toString().split("[/\\\\]");
					String fileName = parts[parts.length-1];
					idSample = fileName.split(".fastq")[0].trim();

					// System.out.println(value + "\t --> \t " + Arrays.toString(parts) + " " + fileName + "\t --> \t " + idSample);

					return idSample;
				}
			}
			catch (Exception e) {
				// nothing to do
			}
		}
		return idSample;
	}


	/** =============================================================== */

	public Map<String, Object> createParameters(String dataline, List<String> header) {

		Map<String, Object> mapParameters = new HashMap<String, Object>();

		String [] parts = dataline.split(columnSeparator);

		for (int i=0; i<parts.length; i++) {
			String value = parts[i];
			if (value!=null) {
				value = value.trim();
				if (!value.isEmpty()) {
					mapParameters.put(header.get(i), parts[i]);
				}
			}
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
		new ImportArrayExpressInit();
	}

	/** ============================================================== */

}
