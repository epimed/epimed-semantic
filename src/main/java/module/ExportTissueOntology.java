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
import java.util.Date;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import config.HibernateUtil;
import dao.ClTopologyDao;
import model.entity.ClTopology;
import service.FileService;

public class ExportTissueOntology extends BaseModule {

	private FileService fileService = new FileService();
	private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd");


	public ExportTissueOntology () {

		// === Output matrix ===
		List<Object> data = new ArrayList<Object>();
		List<String> header = new ArrayList<String>();
		header.add("id_topology");
		header.add("topology");
		header.add("id_topology_group");
		header.add("topology_group");
		

		// ===== Session PostgreSQL =====
		SessionFactory sessionFactory = HibernateUtil.buildSessionFactory("config/epimed_semantic.hibernate.cfg.xml");
		Session session = sessionFactory.openSession();

		// ===== Begin transaction =====
		session.beginTransaction();
		
		ClTopologyDao topologyDao = new ClTopologyDao(session);
		List<ClTopology> listTopologies = topologyDao.findAll();
		
		for (ClTopology t: listTopologies) {
			System.out.println(t);
			
			Object [] dataline = new Object [header.size()];
			
			int j=0;
			dataline[j] = t.getIdTopology();
			dataline[++j] = t.getName();
			dataline[++j] = t.getClTopologyGroup().getIdGroup();
			dataline[++j] = t.getClTopologyGroup().getName();
			
			data.add(dataline);
			
			System.out.println(Arrays.toString(dataline));
			
		}
		
		
		

		// === Output  file ===
		
		String fileName =  this.getOutputDirectory() + this.getDirSeparator() 
			+ "EpiMed_tissue_ontology" + "_" + dateFormat.format(new Date()) +  ".xlsx";
		fileService.writeExcelFile(fileName, header, data);
		System.out.println(fileName);
		

		// === Commit transaction ===
		// session.getTransaction().commit();
		session.getTransaction().rollback();


		if (session.isOpen()) {session.close();}
		sessionFactory.close();

	}



	/** =============================================================== */

	public static void main(String[] args) {
		new ExportTissueOntology();
	}

	/** ============================================================== */

}
