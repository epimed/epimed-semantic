package model.entity;
// Generated 1 nov. 2016 10:44:10 by Hibernate Tools 4.3.1.Final

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * ViewOntologyDictionaryId generated by hbm2java
 */
@Embeddable
public class ViewOntologyDictionaryId implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String term;
	private String idReference;
	private String idCategory;

	public ViewOntologyDictionaryId() {
	}

	public ViewOntologyDictionaryId(String term, String idReference, String idCategory) {
		this.term = term;
		this.idReference = idReference;
		this.idCategory = idCategory;
	}

	@Column(name = "term")
	public String getTerm() {
		return this.term;
	}

	public void setTerm(String term) {
		this.term = term;
	}

	@Column(name = "id_reference")
	public String getIdReference() {
		return this.idReference;
	}

	public void setIdReference(String idReference) {
		this.idReference = idReference;
	}

	@Column(name = "id_category")
	public String getIdCategory() {
		return this.idCategory;
	}

	public void setIdCategory(String idCategory) {
		this.idCategory = idCategory;
	}

	public boolean equals(Object other) {
		if ((this == other))
			return true;
		if ((other == null))
			return false;
		if (!(other instanceof ViewOntologyDictionaryId))
			return false;
		ViewOntologyDictionaryId castOther = (ViewOntologyDictionaryId) other;

		return ((this.getTerm() == castOther.getTerm()) || (this.getTerm() != null && castOther.getTerm() != null
				&& this.getTerm().equals(castOther.getTerm())))
				&& ((this.getIdReference() == castOther.getIdReference())
						|| (this.getIdReference() != null && castOther.getIdReference() != null
								&& this.getIdReference().equals(castOther.getIdReference())))
				&& ((this.getIdCategory() == castOther.getIdCategory())
						|| (this.getIdCategory() != null && castOther.getIdCategory() != null
								&& this.getIdCategory().equals(castOther.getIdCategory())));
	}

	public int hashCode() {
		int result = 17;

		result = 37 * result + (getTerm() == null ? 0 : this.getTerm().hashCode());
		result = 37 * result + (getIdReference() == null ? 0 : this.getIdReference().hashCode());
		result = 37 * result + (getIdCategory() == null ? 0 : this.getIdCategory().hashCode());
		return result;
	}

	@Override
	public String toString() {
		return "ViewOntologyDictionaryId [term=" + term + ", idReference=" + idReference + ", idCategory=" + idCategory
				+ "]";
	}
	
	

}
