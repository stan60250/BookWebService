package restfulWS.fee;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.criterion.Restrictions;
import org.hibernate.service.ServiceRegistry;

import restfulWS.fee.exceptions.NullAccountException;
import restfulWS.fee.model.FeeModel;

import java.util.Iterator;
import java.util.List;

public class HibernateFee implements Fee {
	
	private Session hibernateSession;
	private SessionFactory hibernateSessionFactory;
	private Criteria hibernateCriteria;
	private ServiceRegistry serviceRegistry;

	public HibernateFee() throws Exception {

		String configXML = "restfulWS/fee/feehibernate-config.xml";
		try {
			Configuration hibernateConfig = new Configuration().configure(configXML);
			serviceRegistry =   new StandardServiceRegistryBuilder().
					applySettings(hibernateConfig.getProperties()).build();
			hibernateSessionFactory = hibernateConfig.buildSessionFactory(serviceRegistry);	
		} 
		
		catch (Exception e) {
			throw e;
		}
	}
	
	public void create(FeeModel feeModel) throws Exception {
		hibernateSession = hibernateSessionFactory.openSession();
		Transaction tx= hibernateSession.beginTransaction();
		hibernateSession.save(feeModel);
		tx.commit();	
		hibernateSession.close();
	}
	
	public void update(int id, FeeModel feeModel) throws Exception {
		hibernateSession = hibernateSessionFactory.openSession();
		Transaction tx= hibernateSession.beginTransaction();
		hibernateSession.update(feeModel);
		tx.commit();
		hibernateSession.close();	
	}
	
	public void delete(int id) throws Exception {	
		hibernateSession = hibernateSessionFactory.openSession();
		Transaction tx = hibernateSession.beginTransaction();
		hibernateSession.delete(hibernateSession.load(FeeModel.class, id));
		tx.commit();
		hibernateSession.close();
	}
	
	public FeeModel find(int id) throws Exception {	
		hibernateSession = hibernateSessionFactory.openSession();
		
		FeeModel feeModel = (FeeModel) hibernateSession.get(FeeModel.class, id);
		
		if (feeModel == null) {
			throw new NullAccountException();
		}
		return feeModel;
	}
	
	public List<FeeModel> list() throws Exception {
		List<FeeModel> feeModelList;
		
		hibernateSession = hibernateSessionFactory.openSession();
		hibernateCriteria = hibernateSession.createCriteria(FeeModel.class);
		feeModelList = hibernateCriteria.list();
		
		return feeModelList;	
	}
	
	public FeeModel findByName(String name) throws Exception {	
		List<FeeModel> feeModelList;

		hibernateSession = hibernateSessionFactory.openSession();
		hibernateCriteria = hibernateSession.createCriteria(FeeModel.class);
		hibernateCriteria.add(Restrictions.eq("name", name));
		feeModelList = hibernateCriteria.list();
		Iterator iterator = feeModelList.iterator();
		
		FeeModel feeModel;
		
		if (iterator.hasNext()) {
			feeModel = (FeeModel) iterator.next();
		} 
		else {
			throw new NullAccountException();
		}
		return feeModel;
	}
}
