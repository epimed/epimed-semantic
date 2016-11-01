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
 * ClTopologyGroup generated by hbm2java
 */
@Entity
@Table(name = "cl_topology_group", schema = "epimed_semantic")
public class ClTopologyGroup implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String idGroup;
	private String name;
	private List<ClTopology> clTopologies = new ArrayList<ClTopology>(0);

	public ClTopologyGroup() {
	}

	public ClTopologyGroup(String idGroup, String name) {
		this.idGroup = idGroup;
		this.name = name;
	}

	public ClTopologyGroup(String idGroup, String name, List<ClTopology> clTopologies) {
		this.idGroup = idGroup;
		this.name = name;
		this.clTopologies = clTopologies;
	}

	@Id

	@Column(name = "id_group", unique = true, nullable = false, length = 10)
	public String getIdGroup() {
		return this.idGroup;
	}

	public void setIdGroup(String idGroup) {
		this.idGroup = idGroup;
	}

	@Column(name = "name", nullable = false, length = 100)
	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "clTopologyGroup")
	public List<ClTopology> getClTopologies() {
		return this.clTopologies;
	}

	public void setClTopologies(List<ClTopology> clTopologies) {
		this.clTopologies = clTopologies;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((idGroup == null) ? 0 : idGroup.hashCode());
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
		ClTopologyGroup other = (ClTopologyGroup) obj;
		if (idGroup == null) {
			if (other.idGroup != null)
				return false;
		} else if (!idGroup.equals(other.idGroup))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "ClTopologyGroup [idGroup=" + idGroup + ", name=" + name + "]";
	}

	
}
