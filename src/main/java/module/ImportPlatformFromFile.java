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

import java.util.Arrays;
import java.util.Date;
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
import dao.OmGeneDao;
import exception.ImportDataException;
import model.bind.NcbiGeoGpl;
import model.entity.OmGenbankUnigene;
import model.entity.OmGene;
import model.entity.OmPlatform;
import service.FileService;
import service.WebService;

@SuppressWarnings("unchecked")
public class ImportPlatformFromFile extends BaseModule {

	private FileService fileService = new FileService();
	private WebService webService = new WebService();
	private Date today = new Date();

	public ImportPlatformFromFile() {

		// === Display ===
		System.out.println("\n================ BEGIN Module " + this.getClass().getName() + "================");

		// === INPUT ===
		String idPlatform = "GPL97";
		String inputfile = this.getInputDirectory() + this.getDirSeparator() + "GPL97-17394.txt";
		String gpl = idPlatform.toLowerCase().trim();

		// ===== Session PostgreSQL =====
		SessionFactory sessionFactory = HibernateUtil.buildSessionFactory("config/epimed_semantic.hibernate.cfg.xml");
		Session session = sessionFactory.openSession();

		// ===== DAO =====
		OmGeneDao geneDao = new OmGeneDao(session);

		// ===== Session Mongo =====
		MongoClient mongoClient = MongoUtil.buildMongoClient();
		MongoDatabase db = mongoClient.getDatabase("epimed_experiments");

		try {
			// === Begin transaction ===
			session.beginTransaction(); 

			// ===== Load file =====
			System.out.println("ID Platform " + gpl);
			System.out.println("LOADING \t " + inputfile);
			System.out.println("Please wait... ");
			List<String> listRows = fileService.loadTextFile(inputfile);
			// List<String> listRows = webService.loadGeoData(idPlatform);
			System.out.println("File sucessfully LOADED");




			// ===== Recognize header =====

			List<String> header = fileService.readHeader(listRows,"\t");

			if (header==null || header.isEmpty()) {
				throw new ImportDataException("The header is empty");
			}
			else  {
				System.out.println("Header " + header);
			}

			Integer indId = fileService.findIndex(header, "ID");
			Integer indGbacc = fileService.findIndex(header, "GB_ACC");
			Integer indEntrez = fileService.findIndex(header, "ENTREZ");

			if (indId==null || indGbacc==null || indEntrez==null) {
				throw new ImportDataException("Header not recognized: " 
						+ "ID index=" + indId + ", GB_ACC index=" + indGbacc +", ENTREZ index=" + indEntrez);
			}
			else {
				System.out.println("The following header items are recognized:");
				System.out.println("\t ID index=" + indId + ": " + header.get(indId));
				System.out.println("\t GB_ACC index=" + indGbacc + ": " + header.get(indGbacc));
				System.out.println("\t ENTREZ index=" + indEntrez + ": " + header.get(indEntrez));
			}


			// ===== Recognize data =====

			List<List<String>> data = fileService.readData(listRows, "\t");

			if (data==null || data.isEmpty()) {
				throw new ImportDataException("The data are empty"); 
			}
			else {
				System.out.println("The data are sucessfully loaded: rows " + data.size() + ", columns " + data.get(0).size());
			}


			// ===== Create specific tables =====

			String sqlCheckTableProbe ="select * from information_schema.tables WHERE table_schema = 'hs' and table_name='om_probe_" + gpl +"'";

			List<Object> result = session.createNativeQuery(sqlCheckTableProbe).getResultList();

			String tableProbe = "hs.om_probe_" + gpl;
			String tableGP = "hs.om_gp_" + gpl;

			if (result==null || result.isEmpty()) {
				// Table probe
				String sqlCreateTableProbe = "create table " + tableProbe
						+  "(id_probe             VARCHAR(50)          not null," 
						+  " genbank_acc          VARCHAR(50)          null,"
						+  " constraint pk_om_probe_" + gpl + " primary key (id_probe))";
				session.createNativeQuery(sqlCreateTableProbe).executeUpdate();

				// Table gp
				String sqlCreateTableGP = "create table " + tableGP
						+ "(id_probe             VARCHAR(50)          not null,"
						+ " id_gene              INT4                 not null,"
						+ " constraint pk_om_gp_" + gpl + " primary key (id_probe, id_gene))";	
				session.createNativeQuery(sqlCreateTableGP).executeUpdate();


				// Foregn keys

				String sqlAlterTableProbe = "alter table " + tableGP + " add constraint fk_gp_probe_" + gpl + " foreign key (id_probe)"
						+ "  references " + tableProbe +  " (id_probe) on delete restrict on update restrict";
				session.createNativeQuery(sqlAlterTableProbe).executeUpdate();

				String sqlAlterTableGene = "alter table " + tableGP + " add constraint fk_gp_gene_" + gpl + " foreign key (id_gene)"
						+ "  references hs.om_gene (id_gene) on delete restrict on update restrict";
				session.createNativeQuery(sqlAlterTableGene).executeUpdate();
			}


			// ===== Import data =====

			for (int i=0; i<data.size(); i++) {
				// for (int i=0; i<10; i++) {

				List<String> dataline = data.get(i);

				String idProbe = dataline.get(indId).trim();
				String genbankAcc = dataline.get(indGbacc).trim();

				String sqlInsertProbe = "insert into " + tableProbe + " values('" + idProbe + "',  null)";
				if (genbankAcc!=null && !genbankAcc.isEmpty()) {
					sqlInsertProbe = "insert into " + tableProbe + " values('" + idProbe + "', '" + genbankAcc + "')";
				}
				session.createNativeQuery(sqlInsertProbe).executeUpdate();

				OmGenbankUnigene gu = session.get(OmGenbankUnigene.class, genbankAcc);
				if (gu==null && genbankAcc!=null && !genbankAcc.isEmpty()) {
					gu = new OmGenbankUnigene();
					gu.setGenbankAcc(genbankAcc);
					session.save(gu);
				}

				String listEntrez = null;
				String [] parts = null;
				if (indEntrez<dataline.size()) {
					listEntrez = dataline.get(indEntrez).trim();
					parts = listEntrez.split("[///\\p{Space}]");

					for (String entrezString : parts) {

						Integer entrez = null;

						try {
							entrez = Integer.parseInt(entrezString);
						}
						catch (NumberFormatException e) {
							// nothing to do
						}

						if (entrez!=null) {

							OmGene gene  = geneDao.find(entrez);
							if (gene==null) {
								gene = geneDao.createGene(entrez, null);
							}

							String sqlInsertGP = "insert into " + tableGP + " values('" + idProbe + "', " + entrez + ")";
							session.createNativeQuery(sqlInsertGP).executeUpdate();

						}
					}
				}

				if (i%1000==0) {
					System.out.println(i + "\t" + idProbe + "\t" + genbankAcc + "\t" + listEntrez + "\t" + Arrays.toString(parts));
				}

				if (i%20==0) {
					session.flush();
				}
			}


			// ===== Subscribe platform =====

			OmPlatform platform = session.get(OmPlatform.class, idPlatform);
			if (platform!=null) {
				platform.setEnabled(true);
				session.update(platform);
			}
			else {
				MongoCollection<Document> collection = db.getCollection("platforms");
				Document docPlatform = collection.find(Filters.eq("_id", idPlatform)).first();
				String title = docPlatform.getString("title");
				String manufacturer = docPlatform.getString("manufacturer");
				platform = new OmPlatform();
				platform.setIdPlatform(idPlatform);
				platform.setTitle(title);
				platform.setManufacturer(manufacturer);
				platform.setEnabled(true);
				session.save(platform);
			}

			// ===== Rights =====
			String sqlRights;
			String [] users = {"epimed_prod", "epimed_web", "epimed_script"};
			for (String user : users) {
				sqlRights = "GRANT SELECT ON ALL TABLES IN SCHEMA hs TO " + user;
				session.createNativeQuery(sqlRights).executeUpdate();
			}
			sqlRights = "GRANT ALL ON ALL TABLES IN SCHEMA hs TO epimed_admin";
			session.createNativeQuery(sqlRights).executeUpdate();

			// === Commit transaction ===
			session.getTransaction().commit();
			// session.getTransaction().rollback();


		}
		catch (Exception e) {
			session.getTransaction().rollback();
			System.out.println("ROLLBACK in module " + this.getClass().getName());
			e.printStackTrace();
		}
		finally {
			if (session.isOpen()) {session.close();}
			sessionFactory.close();
			mongoClient.close();
		}

		// === Display ===
		System.out.println("================ END Module " + this.getClass().getName() + "================");


	}



	/** =============================================================== */

	public static void main(String[] args) {
		new ImportPlatformFromFile();
	}

	/** ============================================================== */

}
