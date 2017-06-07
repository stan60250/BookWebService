package restfulWS.auth;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.criterion.Restrictions;
import org.hibernate.service.ServiceRegistry;

import restfulWS.auth.exceptions.NullAccountException;
import restfulWS.auth.model.MemberModel;

import java.util.Iterator;
import java.util.List;

public class HibernateAuth implements Auth {
	
	private Session hibernateSession;
	private SessionFactory hibernateSessionFactory;
	private Criteria hibernateCriteria;
	private ServiceRegistry serviceRegistry;

	public HibernateAuth() throws Exception {

		String configXML = "restfulWS/auth/authhibernate-config.xml";
		try {
			Configuration hibernateConfig = new Configuration().configure(configXML);
			serviceRegistry = new StandardServiceRegistryBuilder().applySettings(hibernateConfig.getProperties()).build();
			hibernateSessionFactory = hibernateConfig.buildSessionFactory(serviceRegistry);	
		} catch (Exception e) {
			throw e;
		}
	}
	
	public void create(String action, MemberModel memModel) throws Exception {
		hibernateSession = hibernateSessionFactory.openSession();
		Transaction tx= hibernateSession.beginTransaction();
		hibernateSession.save(memModel);
		tx.commit();
		hibernateSession.close();
	}
	
	public void update(String token, MemberModel memModel) throws Exception {
		hibernateSession = hibernateSessionFactory.openSession();
		Transaction tx= hibernateSession.beginTransaction();
		hibernateSession.update(memModel);
		tx.commit();
		hibernateSession.close();	
	}
	
	public void delete(String token) throws Exception {	
		hibernateSession = hibernateSessionFactory.openSession();
		Transaction tx = hibernateSession.beginTransaction();
		hibernateSession.delete(hibernateSession.load(MemberModel.class, token));
		tx.commit();
		hibernateSession.close();
	}
	
	public MemberModel find(String token) throws Exception {	
		hibernateSession = hibernateSessionFactory.openSession();
		MemberModel memModel = (MemberModel) hibernateSession.get(MemberModel.class, token);
		if (memModel == null) {
			throw new NullAccountException();
		}
		return memModel;
	}
}
