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

public class ImportPlatform {


	private WebService webService = new WebService();
	private Date today = new Date();

	public ImportPlatform () {

		NcbiGeoGpl gpl = new NcbiGeoGpl(webService.loadGeo("GPL96"));

		System.out.println(gpl);
		
		System.out.println("Data import, please wait...");
		List<String> data = webService.loadGeoData("GPL96");
		System.out.println("Loaded " + data.size() + " lines");
		
		for (int i=0; i<20; i++) {
			String line = data.get(i);
			System.out.println(line);
		}
		
		
		
	}


	/** =============================================================== */

	public static void main(String[] args) {
		new ImportPlatform();
	}

	/** ============================================================== */

}
