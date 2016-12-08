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
 * Author: Ekaterina Flin 
 *
 */
package module.interoperability.dispatcher;

import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.bson.Document;
import org.hibernate.Session;

import service.WebService;

public abstract class DispatcherAbstract {

	protected Session session;
	protected WebService webService;

	/** ================================================================================= */

	public DispatcherAbstract(Session session) {
		super();
		this.session = session;
	}


	/** ================================================================================= */
	
	public abstract void create(Document doc, Map <String, List<Object>> mapOntologyObjects) throws DispatcherException;

	
	/** ================================================================================= */
	
	@SuppressWarnings("resource")
	public Object makeChoice(Map <String, List<Object>> mapOntologyObjects, String category) {
		
		List<Object> list = mapOntologyObjects.get(category);
		
		if (list==null || list.isEmpty()) {
			return null;
		}
		else {
			
			if (list.size()==1) {
				return list.get(0);
			}
			
			if (list.size()>1) {
				
				Scanner keyboard = new Scanner(System.in);
				
				System.out.println("\n*********************************************************");
				System.out.println("WARNING. Several possibilities found for " + category + ": ");
				
				for (int i=0; i<list.size(); i++) {
					System.out.println("\t" + i + "\t->\t" + list.get(i));
				}
				
				System.out.println("\t" + "Please enter the index of selected value: \n");
				int index = keyboard.nextInt();
				
				
				if (index<list.size()) {
					System.out.println("\t" + "Selected value: " + list.get(index));
					System.out.println("\n*********************************************************");
					return list.get(index);
				}
				else {
					System.out.println("\t" + "ERROR. Index doesn't exist. Exit.");
					System.out.println("\n*********************************************************");
					System.exit(0);
				}
				
				keyboard.close();
				
			}
		}
		
		return null;
	}
	
	/** ================================================================================= */
	
	public void displayMessage (String message) {
		System.err.println(message + " [" + this.getClass().getName() + "]");
	}
	
	
	/** ================================================================================= */
}
