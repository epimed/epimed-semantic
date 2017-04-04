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
import java.util.List;

import org.bson.Document;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;

import config.HibernateUtil;
import config.MongoUtil;
import module.BaseModule;
import service.ExcelService;

public class ImportSupplementaryGSE20711 extends BaseModule {

	public ImportSupplementaryGSE20711 () {


		// ===== Session PostgreSQL =====
		SessionFactory sessionFactory = HibernateUtil.buildSessionFactory("config/epimed_semantic.hibernate.cfg.xml");
		Session session = sessionFactory.openSession();

		// ===== Session Mongo =====

		MongoClient mongoClient = MongoUtil.buildMongoClient();
		MongoDatabase db = mongoClient.getDatabase("epimed_experiments");

		MongoCollection<Document> collection = db.getCollection("samples");

		// ===== Excel data loader =====

		String inputfile = this.getInputDirectory() + this.getDirSeparator() + "GSE20711_emmm0003-0726-SD2.xlsx";
		System.out.println("LOADING \t " + inputfile);
		ExcelService excelService = new ExcelService();
		excelService.load(inputfile);

		String gseNumber = "GSE20711";
		
		for (int i=0; i<excelService.getData().size(); i++) {
			List<Object> dataLine = excelService.getData().get(i);
			
			
			String bcString = (String) dataLine.get(0);
			bcString = bcString.replaceAll("BC", "");
			
			Integer bcNumber = Integer.parseInt(bcString);
			
			Document docSample = collection
					.find(Filters.and(
							Filters.in("series", gseNumber),
							Filters.eq("exp_group.sample_title", "Breast tumor from patient P_" + bcNumber + " (expression data)")
							)
							).first();
			
			System.out.println("-------------------------------------------");
			System.out.println(dataLine);
			System.out.println(docSample);

		}

		if (session.isOpen()) {session.close();}
		sessionFactory.close();

		mongoClient.close();	
	}

	/** =============================================================== */

	public static void main(String[] args) {
		new ImportSupplementaryGSE20711();
	}

	/** ============================================================== */

}
