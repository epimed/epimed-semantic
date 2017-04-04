package service;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.bson.Document;

public class MongoService {

	Date today = new Date();

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
			Date submissionDate, Date lastUpdate) {
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
		;
		return docPlatform;
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
		;

		return docSeries;

	}

	/** ============================================================================================ */

}
