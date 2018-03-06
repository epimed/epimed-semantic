package module.script.epimed_ontology;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import config.HibernateUtil;
import dao.ClEpimedGroupDao;
import dao.ClTopologyDao;
import model.entity.ClEpimedGroup;
import model.entity.ClTopology;
import service.FileService;

public class ImportEpimedOntology {

	private String workingDir = System.getProperty("user.dir");
	private FileService fileService = new FileService();

	public ImportEpimedOntology() {	

		// ===== Session PostgreSQL =====
		SessionFactory sessionFactory = HibernateUtil.buildSessionFactory("config/epimed_semantic.hibernate.cfg.xml");
		Session session = sessionFactory.openSession();
		session.beginTransaction();

		ClTopologyDao topologyDao = new ClTopologyDao(session);
		ClEpimedGroupDao epimedGroupDao = new ClEpimedGroupDao(session);


		// ====== Load file ======

		String inputfile = "180131_EpiMed_tissue_ontology.csv";
		String filename =  workingDir + File.separator + "data" + File.separator + "in" + File.separator + inputfile;
		System.out.println("LOADING \t " + filename);
		List<String[]> csv = fileService.loadCsv(filename, ';');
		fileService.printHeader(csv);

		for (int i =1; i<csv.size(); i++) {
		// for (int i =1; i<2; i++) {
			String [] line = csv.get(i);
			System.out.println(Arrays.toString(line));

			// === Topology ===
			String idTopology = line[0];
			ClTopology topology = topologyDao.find(idTopology);
			System.out.println(topology);

			// === Group level 1 ===
			ClEpimedGroup epimedGroup1 = epimedGroupDao.findByNameAndLevel(line[4], 1);
			ClEpimedGroup epimedGroup2 = epimedGroupDao.findByNameAndLevel(line[5], 2);
			ClEpimedGroup epimedGroup3 = epimedGroupDao.findByNameAndLevel(line[6], 3);
			System.out.println(epimedGroup1);
			System.out.println(epimedGroup2);
			System.out.println(epimedGroup3);
		
			epimedGroup2.setParent(epimedGroup3);
			epimedGroup1.setParent(epimedGroup2);
			topology.setClEpimedGroup(epimedGroup1);
			
			session.update(epimedGroup2);
			session.update(epimedGroup1);
			session.update(topology);

		}




		// === Commit transaction ===
		session.getTransaction().commit();
		// session.getTransaction().rollback();


		if (session.isOpen()) {session.close();}
		sessionFactory.close();

	}

	/** =============================================================== */

	public static void main(String[] args) {
		new ImportEpimedOntology();
	}

	/** ============================================================== */

}
