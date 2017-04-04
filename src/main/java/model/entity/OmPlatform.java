package model.entity;
// Generated 14 nov. 2016 16:04:36 by Hibernate Tools 4.3.1.Final

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * OmPlatform generated by hbm2java
 */
@Entity
@Table(name = "om_platform", schema = "hs")
public class OmPlatform implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String idPlatform;
	private String title;
	private String manufacturer;
	private boolean enabled;

	public OmPlatform() {
	}

	public OmPlatform(String idPlatform, String title, String manufacturer, boolean enabled) {
		this.idPlatform = idPlatform;
		this.title = title;
		this.manufacturer = manufacturer;
		this.enabled = enabled;
	}

	@Id

	@Column(name = "id_platform", unique = true, nullable = false, length = 50)
	public String getIdPlatform() {
		return this.idPlatform;
	}

	public void setIdPlatform(String idPlatform) {
		this.idPlatform = idPlatform;
	}

	@Column(name = "title", nullable = false)
	public String getTitle() {
		return this.title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	@Column(name = "manufacturer", nullable = false)
	public String getManufacturer() {
		return this.manufacturer;
	}

	public void setManufacturer(String manufacturer) {
		this.manufacturer = manufacturer;
	}

	@Column(name = "enabled", nullable = false)
	public boolean isEnabled() {
		return this.enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((idPlatform == null) ? 0 : idPlatform.hashCode());
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
		OmPlatform other = (OmPlatform) obj;
		if (idPlatform == null) {
			if (other.idPlatform != null)
				return false;
		} else if (!idPlatform.equals(other.idPlatform))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "OmPlatform [idPlatform=" + idPlatform + ", title=" + title + ", manufacturer=" + manufacturer
				+ ", enabled=" + enabled + "]";
	}
	
	

}