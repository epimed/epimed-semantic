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
package module.script;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bson.Document;

import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Accumulators;
import com.mongodb.client.model.Projections;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;
import com.mongodb.Block;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import config.MongoUtil;
import module.BaseModule;
import service.FormatService;

public class QueryAvailableData extends BaseModule {


	public QueryAvailableData () {

		// ===== Service =====
		FormatService formatService = new FormatService ();


		// ===== Session Mongo =====

		MongoClient mongoClient = MongoUtil.buildMongoClient();
		MongoDatabase db = mongoClient.getDatabase("epimed_experiments");

		MongoCollection<Document> collectionSeries = db.getCollection("series");
		MongoCollection<Document> collectionSamples = db.getCollection("samples");


		// ===== Print block =====
		Block<Document> printBlock = new Block<Document>() {
			public void apply(final Document document) {
				System.out.println(document.toJson());
			}
		};

		// ===== Group by topology =====
		// db.getCollection('samples').aggregate({ $group: { "_id" : "$exp_group.topology", "total" : {$sum : 1} }}, {$sort : {total : -1}} )
		/*
		List<Document> listDocuments = collectionSamples.aggregate(
				Arrays.asList(
						Aggregates.group("$exp_group.topology", Accumulators.sum("total", 1)),
						Aggregates.sort(Sorts.orderBy(Sorts.descending("total")))
						))
				.into(new ArrayList<Document>());
		 */

		// ===== Group by sample =====
		/*
		List<Document> listSeries = collectionSeries
				.find()
				.projection(Projections.fields(Projections.include("title")))
				.sort(Sorts.ascending("_id"))
				.into(new ArrayList<Document>());

		for (Document doc : listSeries) {
		
			String idSeries = doc.getString("_id");
			Long nbSamples = collectionSamples.count((Filters.eq("series", idSeries)));
			doc.append("nbSamples", nbSamples);
		} 
		display(listSeries);
		*/
		
		
		// === Export Geo for a list of idSeries ===
		
		// String[] listIdSeries = {"GSE11092","GSE13309", "GSE13159"};
		
		/*
		List<Document> docExpGroup = collectionSamples
				.find(Filters.in("series", listIdSeries))
				.projection(Projections.fields(Projections.include("exp_group"), Projections.excludeId()))
				.into(new ArrayList<Document>());
		// display(docExpGroup);
		
		List<String> header = formatService.extractHeader(docExpGroup, "exp_group");
		List<Object> data = formatService.extractData(docExpGroup, header, "exp_group");
		System.out.println(header);
		displayMatrix(data);
		
		*/
		// List<Object> listObjects = formatService.convertHeterogeneousMongoDocuments(docExpGroup, "exp_group");
		// displayMatrix(listObjects);
		
		// List<Object> listObjects = formatService.convertHomogeneousMongoDocuments(listDocuments);

		
		// === Find series ===
		
		String[] listIdSamples = {"GSM80908", "GSM274639","GSM274638", "GSM280213"};
		List<Document> listDocuments = collectionSamples.aggregate(
			      Arrays.asList(
			              Aggregates.match(Filters.in("_id", listIdSamples)),
			              Aggregates.group("$main_gse_number"),
			              Aggregates.sort(Sorts.orderBy(Sorts.ascending("main_gse_numbe")))
			      )
				)
			.into(new ArrayList<Document>());
		List<Object> listObjects = formatService.convertHomogeneousMongoDocuments(listDocuments);
		displayMatrix(listObjects);
		
		mongoClient.close();	
	}

	/** =============================================================== */

	public static void main(String[] args) {
		new QueryAvailableData();
	}

	/** ============================================================== */

	public void display(List<Document> list) {

		for (Document document : list) {
			System.out.println(document);
		}
	}


	/** ============================================================== */

	public void displayMatrix(List<Object> list) {
		for (Object item : list) {
			Object[] line = (Object[]) item;
			System.out.println(Arrays.toString(line));
		}
	}
	
}
