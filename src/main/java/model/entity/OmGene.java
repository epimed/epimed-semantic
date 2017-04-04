package model.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name = "om_gene", schema = "hs")
public class OmGene implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Integer idGene;
	private String geneSymbol;
	private String title;
	private String location;
	private String status;
	private String type;
	private Date dateModified;
	private Date lastUpdate;
	private String hgncId;
	private String locusGroup;
	private String source;
	private Integer replacedBy;
	private boolean removed;
	

	public OmGene() {
	}

	public OmGene(Integer idGene, String geneSymbol, String status, String type, Date dateModified, Date lastUpdate,
			String locusGroup, String source, boolean removed) {
		this.idGene = idGene;
		this.geneSymbol = geneSymbol;
		this.status = status;
		this.type = type;
		this.dateModified = dateModified;
		this.lastUpdate = lastUpdate;
		this.locusGroup = locusGroup;
		this.source = source;
		this.removed = removed;
	}

	

	public OmGene(Integer idGene, String geneSymbol, String title, String location, String status, String type,
			Date dateModified, Date lastUpdate, String hgncId, String locusGroup, String source, Integer replacedBy,
			boolean removed) {
		super();
		this.idGene = idGene;
		this.geneSymbol = geneSymbol;
		this.title = title;
		this.location = location;
		this.status = status;
		this.type = type;
		this.dateModified = dateModified;
		this.lastUpdate = lastUpdate;
		this.hgncId = hgncId;
		this.locusGroup = locusGroup;
		this.source = source;
		this.replacedBy = replacedBy;
		this.removed = removed;
	}

	@Id

	@Column(name = "id_gene", unique = true, nullable = false)
	public Integer getIdGene() {
		return this.idGene;
	}

	public void setIdGene(Integer idGene) {
		this.idGene = idGene;
	}

	@Column(name = "gene_symbol", nullable = false, length = 50)
	public String getGeneSymbol() {
		return this.geneSymbol;
	}

	public void setGeneSymbol(String geneSymbol) {
		this.geneSymbol = geneSymbol;
	}

	@Column(name = "title")
	public String getTitle() {
		return this.title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	@Column(name = "location", length = 100)
	public String getLocation() {
		return this.location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	@Column(name = "status", nullable = false, length = 100)
	public String getStatus() {
		return this.status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@Column(name = "type", nullable = false, length = 100)
	public String getType() {
		return this.type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "date_modified", nullable = false, length = 13)
	public Date getDateModified() {
		return this.dateModified;
	}

	public void setDateModified(Date dateModified) {
		this.dateModified = dateModified;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "last_update", nullable = false, length = 13)
	public Date getLastUpdate() {
		return this.lastUpdate;
	}

	public void setLastUpdate(Date lastUpdate) {
		this.lastUpdate = lastUpdate;
	}

	@Column(name = "hgnc_id", length = 50)
	public String getHgncId() {
		return this.hgncId;
	}

	public void setHgncId(String hgncId) {
		this.hgncId = hgncId;
	}

	@Column(name = "locus_group", nullable = false, length = 100)
	public String getLocusGroup() {
		return this.locusGroup;
	}

	public void setLocusGroup(String locusGroup) {
		this.locusGroup = locusGroup;
	}

	@Column(name = "source", nullable = false, length = 20)
	public String getSource() {
		return this.source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	@Column(name = "replaced_by")
	public Integer getReplacedBy() {
		return this.replacedBy;
	}

	public void setReplacedBy(Integer replacedBy) {
		this.replacedBy = replacedBy;
	}

	@Column(name = "removed", nullable = false)
	public boolean isRemoved() {
		return this.removed;
	}

	public void setRemoved(boolean removed) {
		this.removed = removed;
	}

	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((idGene == null) ? 0 : idGene.hashCode());
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
		OmGene other = (OmGene) obj;
		if (idGene == null) {
			if (other.idGene != null)
				return false;
		} else if (!idGene.equals(other.idGene))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "OmGene [idGene=" + idGene + ", geneSymbol=" + geneSymbol + ", title=" + title + ", location=" + location
				+ ", status=" + status + ", type=" + type + ", dateModified=" + dateModified + ", lastUpdate="
				+ lastUpdate + ", hgncId=" + hgncId + ", locusGroup=" + locusGroup + ", source=" + source
				+ ", replacedBy=" + replacedBy + ", removed=" + removed + "]";
	}
	
}
