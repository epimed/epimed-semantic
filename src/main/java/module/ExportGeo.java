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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;

import config.MongoUtil;
import service.FileService;

public class ExportGeo extends BaseModule {

	private FileService fileService = new FileService();
	private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd");

	public ExportGeo () {

		String gseNumber = "GSE2109";

		// ===== Connection =====

		MongoClient mongoClient = MongoUtil.buildMongoClient();
		MongoDatabase db = mongoClient.getDatabase("geo");
		MongoCollection<Document> collection = db.getCollection("samples");

		// ===== Find exp_group in the database =====

		List<Document> docExpGroup = collection
				.find(Filters.in("series", gseNumber))
				.projection(Projections.fields(Projections.include("exp_group"), Projections.excludeId()))
				.into(new ArrayList<Document>());

		List<Document> docParam = collection
				.find(Filters.in("series", gseNumber))
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

			String [] dataLine = new String [headerExpGroup.size()];
			for (int j=0; j<headerExpGroup.size(); j++) {
				dataLine[j] =  expGroup.get(headerExpGroup.get(j));
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
			String [] dataLine = new String [headerParam.size()];
			for (int j=0; j<headerParam.size(); j++) {
				dataLine[j] =  param.get(headerParam.get(j));
			}
			// System.out.println(Arrays.toString(dataLine));
			dataParam.add(dataLine);
			
		}

		// === Output ===

		String fileName =  this.getOutputDirectory() + this.getDirSeparator() + "Export_Mongo_" + gseNumber + "_" + dateFormat.format(new Date()) +  ".xlsx";
		System.out.println(fileName);
		XSSFWorkbook workbook = fileService.createWorkbook();
		fileService.addSheet(workbook, "exp_group" + dateFormat.format(new Date()), headerExpGroup, dataExpGroup);
		fileService.addSheet(workbook, "parameters_" + dateFormat.format(new Date()), headerParam, dataParam);
		fileService.writeWorkbook(workbook, fileName);

	}



	/** =============================================================== */

	public static void main(String[] args) {
		new ExportGeo();
	}

	/** ============================================================== */

}
