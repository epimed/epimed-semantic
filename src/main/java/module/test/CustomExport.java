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
package module.test;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.bson.Document;
import org.bson.conversions.Bson;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;

import config.MongoUtil;
import module.BaseModule;
import service.FileService;

@SuppressWarnings("unchecked")
public class CustomExport extends BaseModule {

	private FileService fileService = new FileService();
	private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd");


	public CustomExport () {



		// ===== Connection =====

		MongoClient mongoClient = MongoUtil.buildMongoClient();
		MongoDatabase db = mongoClient.getDatabase("epimed_experiments");

		MongoCollection<Document> collection = db.getCollection("samples");

		// ===== Find exp_group in the database =====


		// === Query 1 ===
		/*
		String queryName = "breast_cancer_GPL570";
		List<Bson> filters = new ArrayList<Bson>();
		filters.add(Filters.eq("exp_group.id_platform", "GPL570"));
		filters.add(Filters.eq("exp_group.id_topology_group", "C50"));
		filters.add(Filters.eq("exp_group.id_tissue_status", 3)); // tumoral
		 */

		// === Query 2 ===
		/*
		String queryName = "breast_normal_GPL570";
		List<Bson> filters = new ArrayList<Bson>();
		filters.add(Filters.eq("exp_group.id_platform", "GPL570"));
		filters.add(Filters.eq("exp_group.id_topology_group", "C50"));
		filters.add(Filters.eq("exp_group.id_tissue_status", 1)); // normal
		*/

		// === Query 3 ===
		String queryName = "breast_cancer_with_survival_GPL570";
		List<Bson> filters = new ArrayList<Bson>();
		filters.add(Filters.eq("exp_group.id_platform", "GPL570"));
		filters.add(Filters.eq("exp_group.id_topology_group", "C50"));
		filters.add(Filters.eq("exp_group.id_tissue_status", 3)); // tumoral
		filters.add(Filters.or(
				Filters.ne("exp_group.os_months", null),
				Filters.ne("exp_group.dfss_months", null),
				Filters.ne("exp_group.relapsed", null),
				Filters.ne("exp_group.dead", null)
				));
		
		Bson filter = Filters.and(filters);
		Long nbSamples = collection.count(filter);
		List<String> listSeries = collection.distinct("exp_group.main_gse_number", filter, String.class)
				.into(new ArrayList<String>());
		queryName = queryName + "_" + nbSamples + "_samples_" + listSeries.size() + "_series" ; 

		List<Document> docExpGroup = collection
				.find(filter)
				.projection(Projections.fields(Projections.include("exp_group"), Projections.excludeId()))
				.into(new ArrayList<Document>());

		List<Document> docParam = collection
				.find(filter)
				.projection(Projections.fields(Projections.include("parameters"), Projections.excludeId()))
				.into(new ArrayList<Document>());

		mongoClient.close();	


		// ===== Load Exp Group into a matrix =====

		List<String> headerExpGroup = new ArrayList<String>();
		List<Object> dataExpGroup = new ArrayList<Object>();

		for (int i=0; i<docExpGroup.size(); i++) {
			Map<String, String> expGroup = (Map<String, String>) docExpGroup.get(i).get("exp_group");
			if (i==0) {
				headerExpGroup.addAll(expGroup.keySet());
			}

			Object [] dataLine = new Object [headerExpGroup.size()];
			for (int j=0; j<headerExpGroup.size(); j++) {
				dataLine[j] = expGroup.get(headerExpGroup.get(j));
			}
			dataExpGroup.add(dataLine);
		}

		// ===== Load Params into a matrix =====

		Set<String> headerParamSet = new HashSet<String>();
		List<String> headerParam = new ArrayList<String>();
		List<Object> dataParam = new ArrayList<Object>();

		for (int i=0; i<docParam.size(); i++) {
			Map<String, String> param = (Map<String, String>) docParam.get(i).get("parameters");
			headerParamSet.addAll(param.keySet());
		}
		headerParam.addAll(headerParamSet);
		Collections.sort(headerParam);

		for (int i=0; i<docParam.size(); i++) {
			Map<String, String> param = (Map<String, String>) docParam.get(i).get("parameters");
			Object [] dataLine = new Object [headerParam.size()];
			for (int j=0; j<headerParam.size(); j++) {
				dataLine[j] =  param.get(headerParam.get(j));
			}
			// System.out.println(Arrays.toString(dataLine));
			dataParam.add(dataLine);

		}

		// === Output ===

		String fileName =  this.getOutputDirectory() + this.getDirSeparator() + "EpiMed_database_" + queryName + "_" + dateFormat.format(new Date()) +  ".xlsx";
		System.out.println(fileName);
		XSSFWorkbook workbook = fileService.createWorkbook();
		fileService.addSheet(workbook, "exp_group_" + dateFormat.format(new Date()), headerExpGroup, dataExpGroup);
		fileService.addSheet(workbook, "parameters_" + dateFormat.format(new Date()), headerParam, dataParam);
		fileService.writeWorkbook(workbook, fileName);

	}



	/** =============================================================== */

	public static void main(String[] args) {
		new CustomExport();
	}

	/** ============================================================== */

}
