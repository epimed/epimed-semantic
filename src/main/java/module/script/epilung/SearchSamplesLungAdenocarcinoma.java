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
package module.script.epilung;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bson.Document;
import org.bson.conversions.Bson;

import com.mongodb.Block;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;

import config.MongoUtil;
import module.BaseModule;

public class SearchSamplesLungAdenocarcinoma extends BaseModule {

	public SearchSamplesLungAdenocarcinoma () {


		// ===== Connection =====

		MongoClient mongoClient = MongoUtil.buildMongoClient();
		MongoDatabase db = mongoClient.getDatabase("epimed_experiments");
		MongoCollection<Document> collectionSamples = db.getCollection("samples");
		MongoCollection<Document> collectionPlatforms = db.getCollection("platforms");

		System.out.println("\n================== SUMMARY ==================");

		Bson []  pFilters = {Filters.eq("_id","GPL570"), Filters.eq("type","rna-seq")};

		for (Bson pFilter : pFilters) {

			// === Platforms ===
			List<String> platforms = new ArrayList<String>();
			List<String> platformstext = new ArrayList<String>();
			List<Document> list = collectionPlatforms
					.find(pFilter)
					.into(new ArrayList<Document>());
			for (Document doc : list) {
				platforms.add(doc.getString("_id"));
				platformstext.add(doc.getString("_id") + " " + doc.getString("type"));
			}


			String lungAdenoFilterName = "Lung adenocarcinoma samples with survival";
			Bson lungAdenoFilter = Filters.and(
					Filters.in("exp_group.id_platform", platforms),
					Filters.eq("exp_group.id_tissue_status", 3),
					Filters.eq("exp_group.id_topology_group", "C34"),
					Filters.regex("exp_group.morphology", ".*denocarcinoma.*"),
					Filters.or(
							Filters.ne("exp_group.os_months", null),
							Filters.ne("exp_group.dfs_months", null)
							)
					);

			String ntlFilterName = "Normal lung samples";
			Bson ntlFilter = Filters.and(
					Filters.in("exp_group.id_platform", platforms),
					Filters.eq("exp_group.id_tissue_status", 1),
					Filters.eq("exp_group.id_topology_group", "C34")
					);

			String [] filterNames = {lungAdenoFilterName, ntlFilterName};
			Bson [] sFilters = {lungAdenoFilter, ntlFilter};


			for (int i=0; i<sFilters.length; i++) {

				Bson filter = sFilters[i];
				String filterName = filterNames[i];

				List<Document> docs = collectionSamples
						.find(filter)
						.into(new ArrayList<Document>());

				Set<String> setGse = new HashSet<String>();
				for (Document doc : docs) {
					setGse.add(doc.getString("main_gse_number"));
					// System.out.println(doc);
				}

				System.out.println("-------------------------------------------");
				System.out.println("Query: " + filterName);
				System.out.println("Platforms: " + platformstext);
				System.out.println("Samples: " + docs.size());
				System.out.println("Series: " + setGse);

			}
		}
		/*		
 		List<Document> list = collectionSamples
				.aggregate(
						Arrays.asList(
								Aggregates.match(filters),
								Aggregates.group("$exp_group.topology", Accumulators.sum("total", 1)),
								Aggregates.sort(Sorts.orderBy(Sorts.descending("total")))
								))
				.into(new ArrayList<Document>());

		 */



		// collectionPlatforms.find(Filters.regex("title", ".*ethyl.*")).forEach(printBlock);

		mongoClient.close();	

	}

	/** =============================================================== */

	public static void main(String[] args) {
		new SearchSamplesLungAdenocarcinoma();
	}


	/** =============================================================== */

	Block<Document> printBlock = new Block<Document>() {
		@Override
		public void apply(final Document document) {
			System.out.println(document.toJson());
		}
	};

	/** =============================================================== */


}
