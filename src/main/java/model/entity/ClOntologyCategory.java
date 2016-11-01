package model.entity;
// Generated 1 nov. 2016 10:44:10 by Hibernate Tools 4.3.1.Final

import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * ClOntologyCategory generated by hbm2java
 */
@Entity
@Table(name = "cl_ontology_category", schema = "epimed_semantic")
public class ClOntologyCategory implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String idCategory;
	private List<ClOntologyDictionary> clOntologyDictionaries = new ArrayList<ClOntologyDictionary>(0);
	private List<ClOntologyKeyword> clOntologyKeywords = new ArrayList<ClOntologyKeyword>(0);

	public ClOntologyCategory() {
	}

	public ClOntologyCategory(String idCategory) {
		this.idCategory = idCategory;
	}

	public ClOntologyCategory(String idCategory, List<ClOntologyDictionary> clOntologyDictionaries,
			List<ClOntologyKeyword> clOntologyKeywords) {
		this.idCategory = idCategory;
		this.clOntologyDictionaries = clOntologyDictionaries;
		this.clOntologyKeywords = clOntologyKeywords;
	}

	@Id

	@Column(name = "id_category", unique = true, nullable = false, length = 100)
	public String getIdCategory() {
		return this.idCategory;
	}

	public void setIdCategory(String idCategory) {
		this.idCategory = idCategory;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "clOntologyCategory")
	public List<ClOntologyDictionary> getClOntologyDictionaries() {
		return this.clOntologyDictionaries;
	}

	public void setClOntologyDictionaries(List<ClOntologyDictionary> clOntologyDictionaries) {
		this.clOntologyDictionaries = clOntologyDictionaries;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "clOntologyCategory")
	public List<ClOntologyKeyword> getClOntologyKeywords() {
		return this.clOntologyKeywords;
	}

	public void setClOntologyKeywords(List<ClOntologyKeyword> clOntologyKeywords) {
		this.clOntologyKeywords = clOntologyKeywords;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((idCategory == null) ? 0 : idCategory.hashCode());
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
		ClOntologyCategory other = (ClOntologyCategory) obj;
		if (idCategory == null) {
			if (other.idCategory != null)
				return false;
		} else if (!idCategory.equals(other.idCategory))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "ClOntologyCategory [idCategory=" + idCategory + "]";
	}

	

}
