package model.pojo;


public class ClTnm {

	private String t;
	private String n;
	private String m;
	private String stage;
	private String grade;

	public ClTnm() {	
	}

	/**
	 * @param t
	 * @param n
	 * @param m
	 * @param stage
	 * @param grade
	 */
	public ClTnm(String t, String n, String m, String stage, String grade) {
		super();
		this.t = t;
		this.n = n;
		this.m = m;
		this.stage = stage;
		this.grade = grade;
	}

	
	
	
	public String getT() {
		return t;
	}

	public void setT(String t) {
		this.t = t;
	}

	public String getN() {
		return n;
	}

	public void setN(String n) {
		this.n = n;
	}

	public String getM() {
		return m;
	}

	public void setM(String m) {
		this.m = m;
	}

	public String getStage() {
		return stage;
	}

	public void setStage(String stage) {
		this.stage = stage;
	}

	public String getGrade() {
		return grade;
	}

	public void setGrade(String grade) {
		this.grade = grade;
	}

	@Override
	public String toString() {
		return "ClTnm [t=" + t + ", n=" + n + ", m=" + m + ", stage=" + stage + ", grade=" + grade + "]";
	}

	
	
}
