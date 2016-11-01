package dao;

import org.hibernate.Session;

public class BaseDao {
	protected Session session;
	
	public BaseDao(Session session) {
		setSession(session);
	}
		
	public Session getSession() {
		return session;
	}

	public void setSession(Session session) {
		this.session = session;
	}

}
