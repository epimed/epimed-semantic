package model.entity;
// Generated 1 nov. 2016 10:44:10 by Hibernate Tools 4.3.1.Final

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * ClTissueStage generated by hbm2java
 */
@Entity
@Table(name = "cl_tissue_stage", schema = "epimed_semantic")
public class ClTissueStage implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Integer idTissueStage;
	private String stage;

	public ClTissueStage() {
	}

	public ClTissueStage(Integer idTissueStage, String stage) {
		this.idTissueStage = idTissueStage;
		this.stage = stage;
	}

	@Id

	@Column(name = "id_tissue_stage", unique = true, nullable = false)
	public Integer getIdTissueStage() {
		return this.idTissueStage;
	}

	public void setIdTissueStage(Integer idTissueStage) {
		this.idTissueStage = idTissueStage;
	}

	@Column(name = "stage", nullable = false, length = 50)
	public String getStage() {
		return this.stage;
	}

	public void setStage(String stage) {
		this.stage = stage;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((idTissueStage == null) ? 0 : idTissueStage.hashCode());
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
		ClTissueStage other = (ClTissueStage) obj;
		if (idTissueStage == null) {
			if (other.idTissueStage != null)
				return false;
		} else if (!idTissueStage.equals(other.idTissueStage))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "ClTissueStage [idTissueStage=" + idTissueStage + ", stage=" + stage + "]";
	}
	
	

}