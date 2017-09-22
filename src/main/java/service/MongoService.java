package service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bson.Document;

public class MongoService {

	private Date today = new Date();

	public MongoService() {

	}



	/** ============================================================================================ */

	public String mergeParameter(String current, String existing) {

		// System.out.println("\t ------------------------");
		// System.out.println("\t current: " + current);
		// System.out.println("\t existing: " + existing);

		String merged = null;
		Set<String> setMerged = new HashSet<String>();
		String line = existing + ", " + current;
		String [] array = line.split(", ");

		// System.out.println("\t line: " + line + "\t" + " array: " + Arrays.toString(array));

		for (String part : array) {
			part = part.trim();
			if (!part.isEmpty() && !part.equals("null")) {
				setMerged.add(part);
			}
		}

		if (!setMerged.isEmpty()) {
			merged = setMerged.toString().replaceAll("[\\[\\]\"]", "");
		}

		// System.out.println("\t merged: " + merged);

		return merged;
	}


	/** ============================================================================================ */

	public Document updateParameters(Document docSample, Map<String, Object> mapParameters) {
		Document parameters = new Document();
		parameters.append("id_sample", docSample.getString("_id"));

		for (Map.Entry<String, Object> entry : mapParameters.entrySet()) {
			String key = entry.getKey();
			Object value = entry.getValue();

			String existingValue = docSample.get("parameters", Document.class).getString(key);
			String mergedValue = this.mergeParameter((String) value, existingValue);

			parameters.append(key, mergedValue);
		}

		return parameters;
	}


	/** ============================================================================================ */

	public Document createParameters(Document docSample, Map<String, Object> mapParameters) {
		Document parameters = new Document();
		parameters.append("id_sample", docSample.getString("_id"));

		for (Map.Entry<String, Object> entry : mapParameters.entrySet()) {
			String key = entry.getKey();
			Object value = entry.getValue();
			parameters.append(key, value);
		}

		return parameters;
	}

	/** ============================================================================================ */

	public Document createExpGroup(Document docSample, String idPlatform, String title, String source) {

		Document expGroup = new Document();

		expGroup
		.append("id_sample", docSample.getString("_id"))
		.append("main_gse_number", docSample.getString("main_gse_number"))
		.append("id_platform", idPlatform)
		.append("sample_title", title)
		.append("sample_source", source)
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

	/** ============================================================================================ */

	public Document createSample (String idSample, String mainIdSeries, List<String> listIdSeries, String organism,
			Date submissionDate, Date lastUpdate, boolean analyzed) {

		Document docSample = new Document();

		docSample
		.append("_id", idSample)
		.append("main_gse_number", mainIdSeries)
		.append("series", listIdSeries)
		.append("organism", organism)
		.append("submission_date", submissionDate)
		.append("last_update", lastUpdate)
		.append("import_date", today)
		.append("analyzed", analyzed)
		;
		return docSample;
	}

	/** ============================================================================================ */

	public Document createProbeset(String idPlatform, String idProbeset) {
		Document docPlatform = new Document();
		docPlatform
		.append("_id", idProbeset)
		.append("platforms", Arrays.asList(idPlatform))
		.append("genes", null)
		.append("unigenes", null)
		.append("transcripts", null)
		;
		return docPlatform;
	}

	/** ============================================================================================ */

	public Document createPlatform(String idPlatform, String title, 
			String taxid, String organism, String manufacturer, 
			Date submissionDate, Date lastUpdate, String technology) {

		String type = this.recognizeTypePlatform(title, technology);

		Document docPlatform = new Document();

		docPlatform
		.append("_id", idPlatform)
		.append("title", title)
		.append("id_organism", taxid)
		.append("organism", organism)
		.append("manufacturer", manufacturer)
		.append("submission_date", submissionDate)
		.append("last_update", lastUpdate)
		.append("import_date", today)
		.append("technology", technology)
		.append("type", type);
		;

		return docPlatform;
	}

	/** ============================================================================================ */

	private String recognizeTypePlatform(String title, String technology) {

		if (title!=null) {
			String lowtitle = title.toLowerCase();

			if (lowtitle.contains("methyl")) {
				return "methylome";
			}
			if (title.contains("SNP")) {
				return "snp";
			}

			if ((technology==null || technology.contains("oligonucleotide"))
					&& (
							lowtitle.contains("expression") 
							|| lowtitle.contains("genome") 
							|| lowtitle.contains("exon") 
							|| lowtitle.contains("transcript")
							)
					){
				return "expression array";
			}

			// Default rna-seq
			if (technology!=null && technology.contains("sequencing")) {
				return "rna-seq";
			}

			// Default oligonucleotide
			if (technology!=null && technology.contains("oligonucleotide")) {
				return "expression array";
			}

			// Default proteomics
			if (technology!=null && technology.contains("proteomics")) {
				return "proteomics";
			}


		}

		return null;
	}


	/** ============================================================================================ */

	public Document createSeries(String idSeries, String title, List<String> listPlatforms, Date submissionDate, Date lastUpdate) {

		Document docSeries = new Document();

		docSeries
		.append("_id", idSeries)
		.append("title", title)
		.append("platforms",listPlatforms)
		.append("submission_date", submissionDate)
		.append("last_update", lastUpdate)
		.append("import_date", today)
		.append("nb_samples", 0)
		;

		return docSeries;

	}

	/** ============================================================================================ */


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

	/** ============================================================================================ */

}
