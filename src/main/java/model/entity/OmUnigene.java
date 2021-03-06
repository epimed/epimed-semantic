package model.entity;
// Generated 28 nov. 2016 14:42:01 by Hibernate Tools 4.3.1.Final

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * OmUnigene generated by hbm2java
 */
@Entity
@Table(name = "om_unigene", schema = "hs")
public class OmUnigene implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String idUnigene;
	private OmGene omGene;

	public OmUnigene() {
	}

	public OmUnigene(String idUnigene, OmGene omGene) {
		this.idUnigene = idUnigene;
		this.omGene = omGene;
	}

	@Id

	@Column(name = "id_unigene", unique = true, nullable = false, length = 20)
	public String getIdUnigene() {
		return this.idUnigene;
	}

	public void setIdUnigene(String idUnigene) {
		this.idUnigene = idUnigene;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_gene")
	public OmGene getOmGene() {
		return this.omGene;
	}

	public void setOmGene(OmGene omGene) {
		this.omGene = omGene;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((idUnigene == null) ? 0 : idUnigene.hashCode());
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
		OmUnigene other = (OmUnigene) obj;
		if (idUnigene == null) {
			if (other.idUnigene != null)
				return false;
		} else if (!idUnigene.equals(other.idUnigene))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "OmUnigene [idUnigene=" + idUnigene + ", omGene=" + omGene + "]";
	}

	
}
