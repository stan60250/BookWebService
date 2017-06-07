package restfulWS.fee;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.criterion.Restrictions;
import org.hibernate.service.ServiceRegistry;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import restfulWS.fee.exceptions.NullAccountException;
import restfulWS.fee.model.FeeModel;

import java.util.Iterator;
import java.util.List;

@RestController("restfulWS.RestfulWSFee")
@RequestMapping("/webservice/fee")
public class RestfulWSFee implements Fee {
	
	private Session hibernateSession;
	private SessionFactory hibernateSessionFactory;
	private Criteria hibernateCriteria;
	private ServiceRegistry serviceRegistry;

	public RestfulWSFee() throws Exception {

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

	// http://localhost:8080/00_hw10_WebService/spring/webservice/fee
	// http://ilab.csie.ntut.edu.tw:8080/00_hw10_WebService/spring/webservice/fee
	@RequestMapping(method = RequestMethod.POST)
	public @ResponseBody void create(@RequestBody FeeModel feeModel) throws Exception {
		System.out.println(feeModel.getName());
		
		hibernateSession = hibernateSessionFactory.openSession();
		Transaction tx= hibernateSession.beginTransaction();
		hibernateSession.save(feeModel);
		tx.commit();	
		hibernateSession.close();
	}
	
	// http://localhost:8080/00_hw10_WebService/spring/webservice/fee/{id}
	// http://ilab.csie.ntut.edu.tw:8080/00_hw10_WebService/spring/webservice/fee/{id}
	@RequestMapping(method = RequestMethod.PUT, value = "/{id}")
	public @ResponseBody void update(@PathVariable("id") int id, @RequestBody FeeModel feeModel) throws Exception {
		hibernateSession = hibernateSessionFactory.openSession();
		Transaction tx= hibernateSession.beginTransaction();
		hibernateSession.update(feeModel);
		tx.commit();
		hibernateSession.close();	
	}
	
	// http://localhost:8080/00_hw10_WebService/spring/webservice/fee/{id}
	// http://ilab.csie.ntut.edu.tw:8080/00_hw10_WebService/spring/webservice/fee/{id}
	@RequestMapping(method = RequestMethod.DELETE, value = "/{id}")
	public @ResponseBody void delete(@PathVariable("id") int id) throws Exception {	
		hibernateSession = hibernateSessionFactory.openSession();
		Transaction tx = hibernateSession.beginTransaction();
		
		FeeModel feeModel = new FeeModel();
		feeModel.setId(id);
		hibernateSession.delete(feeModel);
		
		tx.commit();
		hibernateSession.close();	
	}
	
	// http://localhost:8080/00_hw10_WebService/spring/webservice/fee/{id}
	// http://ilab.csie.ntut.edu.tw:8080/00_hw10_WebService/spring/webservice/fee/{id}
	@RequestMapping(method = RequestMethod.GET, value = "/{id}", produces = "application/json")
	public @ResponseBody FeeModel find(@PathVariable("id") int id) throws Exception {	
		hibernateSession = hibernateSessionFactory.openSession();
		
		FeeModel feeModel = (FeeModel) hibernateSession.get(FeeModel.class, id);
		
		if (feeModel == null) {
			throw new NullAccountException();
		}
		return feeModel;
	}
	
	// http://localhost:8080/00_hw10_WebService/spring/webservice/fee
	// http://ilab.csie.ntut.edu.tw:8080/00_hw10_WebService/spring/webservice/fee
	@RequestMapping(method = RequestMethod.GET, produces = "application/json")
	public @ResponseBody List<FeeModel> list() throws Exception {
		List<FeeModel> feeModelList;
		
		hibernateSession = hibernateSessionFactory.openSession();
		hibernateCriteria = hibernateSession.createCriteria(FeeModel.class);
		feeModelList = hibernateCriteria.list();
		
		return feeModelList;	
	}
	
	// http://localhost:8080/00_hw10_WebService/spring/webservice/fee/findByName
	// http://ilab.csie.ntut.edu.tw:8080/00_hw10_WebService/spring/webservice/fee/findByName
	@RequestMapping(value = "/findByName", method = RequestMethod.GET, produces = "application/json")
	public @ResponseBody FeeModel findByName(@RequestParam(value="name") String name) throws Exception {	
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
	
	@ExceptionHandler(NullAccountException.class)
	@ResponseBody
	@ResponseStatus(value = HttpStatus.NOT_FOUND)
	public void handleNullAccountException(NullAccountException e) {}
	
	@ExceptionHandler(Exception.class)
	@ResponseBody
	@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
	public void handleException(Exception e) {}
}
