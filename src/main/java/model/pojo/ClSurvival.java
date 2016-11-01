package model.pojo;

public class ClSurvival {

	private Double dfsMonths;
	private Double osMonths;
	private Boolean relapsed;
	private Boolean dead;

	public ClSurvival() {
	}


	public ClSurvival(Double dfsMonths, Double osMonths, Boolean relapsed, Boolean dead) {
		super();
		this.dfsMonths = dfsMonths;
		this.osMonths = osMonths;
		this.relapsed = relapsed;
		this.dead = dead;
	}


	public Double getDfsMonths() {
		return dfsMonths;
	}


	public void setDfsMonths(Double dfsMonths) {
		this.dfsMonths = dfsMonths;
	}


	public Double getOsMonths() {
		return osMonths;
	}


	public void setOsMonths(Double osMonths) {
		this.osMonths = osMonths;
	}


	public Boolean getRelapsed() {
		return relapsed;
	}


	public void setRelapsed(Boolean relapsed) {
		this.relapsed = relapsed;
	}


	public Boolean getDead() {
		return dead;
	}


	public void setDead(Boolean dead) {
		this.dead = dead;
	}


	@Override
	public String toString() {
		return "ClSurvival [dfsMonths=" + dfsMonths + ", osMonths=" + osMonths + ", relapsed=" + relapsed + ", dead="
				+ dead + "]";
	}



	public boolean isEmptySurvival() {
		boolean result = this.getRelapsed()==null && this.getDead()==null && this.getDfsMonths()==null && this.getOsMonths()==null;
		return result;
	}


}
