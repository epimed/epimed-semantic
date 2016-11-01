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

package model.bind;


import java.util.ArrayList;
import java.util.List;


public class Cellosaurus {

	private String id;
	private String sex;
	private String histologyType;
	private String atcc;
	private List<String> listAlias; 

	public Cellosaurus() {
		super();
	}


	public Cellosaurus(List<String> data) {
		this.bind(data);
	}


	private void bind(List<String> data) {
		for (int i=0; i<data.size(); i++) {

			String line = data.get(i);

			// ID
			if (line.startsWith("ID")) {
				this.id = line.split("ID")[1].trim().toUpperCase();
			}

			// Sex
			if (line.startsWith("SX")) {
				this.sex = line.split("SX")[1].trim().toUpperCase().substring(0, 1);
			}
			
			// Histology type
			if (line.startsWith("DI")) {
				String[] parts=line.split(";");
				this.histologyType = parts[parts.length-1].trim().toLowerCase();
			}
			
			// ATCC
			if (line.startsWith("DR") && line.contains(" ATCC;")) {
				String[] parts=line.split(";");
				this.atcc = parts[parts.length-1].trim().toUpperCase();
			}
			
			// Alias
			if (line.startsWith("SY")) {
				 listAlias = new ArrayList<String>();
				 line = line.split("SY")[1].trim();
				 String [] aliases = line.split("[;:]");
				 for (int j=0; j<aliases.length; j++) {
					listAlias.add(aliases[j].trim().toUpperCase()); 
				 }
			}

		}
	}


	public String getId() {
		return id;
	}


	public String getSex() {
		return sex;
	}


	public String getHistologyType() {
		return histologyType;
	}


	public String getAtcc() {
		return atcc;
	}


	public List<String> getListAlias() {
		return listAlias;
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Cellosaurus other = (Cellosaurus) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}


	@Override
	public String toString() {
		return "Cellosaurus [id=" + id + ", sex=" + sex + ", histologyType=" + histologyType + ", atcc=" + atcc
				+ ", listAlias=" + listAlias + "]";
	}

}
