package model.entity;
// Generated 1 nov. 2016 10:44:10 by Hibernate Tools 4.3.1.Final

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * ClCollectionMethod generated by hbm2java
 */
@Entity
@Table(name = "cl_collection_method", schema = "epimed_semantic")
public class ClCollectionMethod implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String collectionMethod;

	public ClCollectionMethod() {
	}

	public ClCollectionMethod(String collectionMethod) {
		this.collectionMethod = collectionMethod;
	}

	@Id

	@Column(name = "collection_method", unique = true, nullable = false, length = 100)
	public String getCollectionMethod() {
		return this.collectionMethod;
	}

	public void setCollectionMethod(String collectionMethod) {
		this.collectionMethod = collectionMethod;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((collectionMethod == null) ? 0 : collectionMethod.hashCode());
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
		ClCollectionMethod other = (ClCollectionMethod) obj;
		if (collectionMethod == null) {
			if (other.collectionMethod != null)
				return false;
		} else if (!collectionMethod.equals(other.collectionMethod))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "ClCollectionMethod [collectionMethod=" + collectionMethod + "]";
	}
	
	

}
