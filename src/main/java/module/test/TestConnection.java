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

import static com.mongodb.client.model.Filters.eq;

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

public class TestConnection {


	private String [] listGseNumber = {"GSE74104"};

	private WebService webService = new WebService();
	private MongoService mongoService = new MongoService();

	public TestConnection () {

		for (int k=0; k<listGseNumber.length; k++) {

			String gseNumber = listGseNumber[k];

			System.out.println("------------------------------------------");
			System.out.println(k + " Import " + gseNumber);

			// ===== Load GSE =====

			NcbiGeoGse gse = new NcbiGeoGse(webService.loadGeo(gseNumber));	
			System.out.println(gse);
		}

	}


	/** =============================================================== */

	public Document generateParameters(NcbiGeoGsm gsm) {
		Document parameters = new Document();
		parameters.append("id_sample", gsm.getGsmNumber());
		mongoService.append(parameters, gsm.getDescription());
		mongoService.append(parameters, gsm.getListCharacteristics());
		// parameters.append("extract_protocol", gsm.getExtractProtocol())
		;
		return parameters;
	}

	/** =============================================================== */

	public static void main(String[] args) {
		new TestConnection();
	}

	/** ============================================================== */

}
