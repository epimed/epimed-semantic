package model.entity;

// default package
// Generated 1 nov. 2016 10:43:17 by Hibernate Tools 4.3.1.Final

import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * ClCellLine generated by hbm2java
 */
@Entity
@Table(name = "cl_cell_line", schema = "epimed_semantic")
public class ClCellLine implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String idCellLine;
	private ClMorphology clMorphology;
	private ClTopology clTopology;
	private String atcc;
	private Double age;
	private String sex;
	private String histologyType;
	private List<ClCellLineAlias> clCellLineAliases = new ArrayList<ClCellLineAlias>(0);

	public ClCellLine() {
	}

	public ClCellLine(String idCellLine) {
		this.idCellLine = idCellLine;
	}

	public ClCellLine(String idCellLine, ClMorphology clMorphology, ClTopology clTopology, String atcc, Double age,
			String sex, String histologyType, List<ClCellLineAlias> clCellLineAliases) {
		this.idCellLine = idCellLine;
		this.clMorphology = clMorphology;
		this.clTopology = clTopology;
		this.atcc = atcc;
		this.age = age;
		this.sex = sex;
		this.histologyType = histologyType;
		this.clCellLineAliases = clCellLineAliases;
	}

	@Id

	@Column(name = "id_cell_line", unique = true, nullable = false, length = 20)
	public String getIdCellLine() {
		return this.idCellLine;
	}

	public void setIdCellLine(String idCellLine) {
		this.idCellLine = idCellLine;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_morphology")
	public ClMorphology getClMorphology() {
		return this.clMorphology;
	}

	public void setClMorphology(ClMorphology clMorphology) {
		this.clMorphology = clMorphology;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_topology")
	public ClTopology getClTopology() {
		return this.clTopology;
	}

	public void setClTopology(ClTopology clTopology) {
		this.clTopology = clTopology;
	}

	@Column(name = "atcc", length = 50)
	public String getAtcc() {
		return this.atcc;
	}

	public void setAtcc(String atcc) {
		this.atcc = atcc;
	}

	@Column(name = "age", precision = 17, scale = 17)
	public Double getAge() {
		return this.age;
	}

	public void setAge(Double age) {
		this.age = age;
	}

	@Column(name = "sex", length = 1)
	public String getSex() {
		return this.sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	@Column(name = "histology_type")
	public String getHistologyType() {
		return this.histologyType;
	}

	public void setHistologyType(String histologyType) {
		this.histologyType = histologyType;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "clCellLine")
	public List<ClCellLineAlias> getClCellLineAliases() {
		return this.clCellLineAliases;
	}

	public void setClCellLineAliases(List<ClCellLineAlias> clCellLineAliases) {
		this.clCellLineAliases = clCellLineAliases;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((idCellLine == null) ? 0 : idCellLine.hashCode());
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
		ClCellLine other = (ClCellLine) obj;
		if (idCellLine == null) {
			if (other.idCellLine != null)
				return false;
		} else if (!idCellLine.equals(other.idCellLine))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "ClCellLine [idCellLine=" + idCellLine + ", clMorphology=" + clMorphology + ", clTopology=" + clTopology
				+ ", atcc=" + atcc + ", age=" + age + ", sex=" + sex + ", histologyType=" + histologyType + "]";
	}
	
	

}
