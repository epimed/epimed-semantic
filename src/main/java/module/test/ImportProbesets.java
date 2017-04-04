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

import java.util.List;

import org.bson.Document;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import config.HibernateUtil;
import config.MongoUtil;
import module.BaseModule;
import service.FileService;
import service.FormatService;
import service.MongoService;

public class ImportProbesets extends BaseModule {

	private MongoService mongoService = new MongoService();
	private FileService fileService = new FileService();
	private FormatService formatService = new FormatService();

	public ImportProbesets () {

		// === Display ===
		System.out.println("\n================ BEGIN Module " + this.getClass().getName() + "================");

		// ===== Connection =====

		MongoClient mongoClient = MongoUtil.buildMongoClient();
		MongoDatabase db = mongoClient.getDatabase("epimed_experiments");
		MongoCollection<Document> collectionProbesets = db.getCollection("probesets");

		// ===== Session PostgreSQL =====
		SessionFactory sessionFactory = HibernateUtil.buildSessionFactory("config/epimed_semantic.hibernate.cfg.xml");
		Session session = sessionFactory.openSession();

		String idPlatform = "GPL570";
		String gpl = idPlatform.toLowerCase();

		// ===== Load file =====

		String inputfile = this.getInputDirectory() + this.getDirSeparator() + "HG-U133_Plus_2.na36.annot.csv";

		System.out.println("ID Platform " + gpl);
		System.out.println("LOADING \t " + inputfile);
		System.out.println("Please wait... ");
		List<String> listRows = fileService.loadTextFile(inputfile);
		System.out.println("File sucessfully LOADED");


		// ===== Recognize header =====

		List<String> header = fileService.readHeader(listRows,"\",\"");
		System.out.println("Header " + header);
		List<List<String>> data = fileService.readData(listRows,"\",\"");
		System.out.println("The data are sucessfully loaded: rows " + data.size() + ", columns " + data.get(0).size());
		
		Integer indProbeset = fileService.findIndex(header, "Probe Set ID");
		Integer indGenes = fileService.findIndex(header, "Entrez Gene");
		Integer indUnigenes = fileService.findIndex(header, "UniGene ID");
		Integer indTranscripts = fileService.findIndex(header, "RefSeq Transcript ID");
		Integer indGb = fileService.findIndex(header, "Representative Public ID");
		
		for (int i=0; i<5; i++) {
			List<String> dataline = data.get(i);
			
			String probeset = dataline.get(indProbeset);
			String genes = dataline.get(indGenes);
			String unigenes = dataline.get(indUnigenes);
			String transcripts = dataline.get(indTranscripts);
			String gb = dataline.get(indGb);
			
			System.out.println(probeset + "\t" + genes + "\t" + formatService.splitInArray(unigenes, "///") + "\t" + gb + "\t" + transcripts);
			
			Document docProbeset = mongoService.createProbeset(idPlatform, probeset);
			docProbeset.put("genes", formatService.splitInArray(genes, "///"));
			docProbeset.put("unigenes", formatService.splitInArray(unigenes, "///"));
			
			List<String> listTranscripts = formatService.splitInArray(transcripts, "///");
			listTranscripts.addAll(formatService.splitInArray(gb, "///"));
			
			docProbeset.put("transcripts", listTranscripts);
		
			collectionProbesets.insertOne(docProbeset);
			
			/*
			for (int j=0; j<dataline.size(); j++) {
				String key = header.get(j);
				String value = dataline.get(j);
				System.out.println(key + ": " + value);
			}
			*/
		}

		/*
		String tableProbe = "hs.om_probe_" + gpl;
		String tableGP = "hs.om_gp_" + gpl;
		List<Object []> listProbesets = session.createNativeQuery("select * from " + tableProbe + " order by id_probe").getResultList();
		for (int i=0; i<10; i++) {

			Object[] line = listProbesets.get(i);

			System.out.println(Arrays.toString(line));
			// Document docProbeset = mongoService.createProbeset(idPlatform, "1007_s_at");
			// collectionProbesets.insertOne(docProbeset);
		}
		 */

		// === Close connections ===

		if (session.isOpen()) {session.close();}
		sessionFactory.close();
		mongoClient.close();	

		// === Display ===
		System.out.println("================ END Module " + this.getClass().getName() + "================");

	}

	/** =============================================================== */

	public static void main(String[] args) {
		new ImportProbesets();
	}


	/** =============================================================== */

}
