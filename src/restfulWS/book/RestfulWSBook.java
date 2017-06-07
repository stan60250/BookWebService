package restfulWS.book;

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

import restfulWS.book.exceptions.NullBookException;
import restfulWS.book.model.BookModel;

import java.util.Iterator;
import java.util.List;

@RestController("restfulWS.RestfulWSBook")
@RequestMapping("/webservice/book")
public class RestfulWSBook implements Book {
	
	private Session hibernateSession;
	private SessionFactory hibernateSessionFactory;
	private Criteria hibernateCriteria;
	private ServiceRegistry serviceRegistry;

	public RestfulWSBook() throws Exception {
		String configXML = "restfulWS/book/bookhibernate-config.xml";
		try {
			Configuration hibernateConfig = new Configuration().configure(configXML);
			serviceRegistry =   new StandardServiceRegistryBuilder().
					applySettings(hibernateConfig.getProperties()).build();
			hibernateSessionFactory = hibernateConfig.buildSessionFactory(serviceRegistry);	
		} catch (Exception e) {
			throw e;
		}
	}

	// http://localhost:8080/13_MemberWS/spring/webservice/book
	// http://ilab.csie.ntut.edu.tw:8080/13_MemberWS/spring/webservice/book
	@RequestMapping(method = RequestMethod.POST)
	public @ResponseBody void create(@RequestBody BookModel bookModel) throws Exception {
		hibernateSession = hibernateSessionFactory.openSession();
		Transaction tx= hibernateSession.beginTransaction();
		hibernateSession.save(bookModel);
		tx.commit();
		hibernateSession.close();
	}
	
	// http://localhost:8080/13_MemberWS/spring/webservice/book/{id}
	// http://ilab.csie.ntut.edu.tw:8080/13_MemberWS/spring/webservice/book/{id}
	@RequestMapping(method = RequestMethod.PUT, value = "/{id}")
	public @ResponseBody void update(@PathVariable("id") int id, @RequestBody BookModel bookModel) throws Exception {
		hibernateSession = hibernateSessionFactory.openSession();
		Transaction tx= hibernateSession.beginTransaction();
		hibernateSession.update(bookModel);
		tx.commit();
		hibernateSession.close();	
	}
	
	// http://localhost:8080/13_MemberWS/spring/webservice/book/{id}
	// http://ilab.csie.ntut.edu.tw:8080/13_MemberWS/spring/webservice/book/{id}
	@RequestMapping(method = RequestMethod.DELETE, value = "/{id}")
	public @ResponseBody void delete(@PathVariable("id") int id) throws Exception {	
		hibernateSession = hibernateSessionFactory.openSession();
		Transaction tx = hibernateSession.beginTransaction();
		BookModel bookModel = new BookModel();
		bookModel.setId(id);
		hibernateSession.delete(bookModel);
		tx.commit();
		hibernateSession.close();	
	}
	
	// http://localhost:8080/13_MemberWS/spring/webservice/book/{id}
	// http://ilab.csie.ntut.edu.tw:8080/13_MemberWS/spring/webservice/book/{id}
	@RequestMapping(method = RequestMethod.GET, value = "/{id}", produces = "application/json")
	public @ResponseBody BookModel find(@PathVariable("id") int id) throws Exception {	
		hibernateSession = hibernateSessionFactory.openSession();
		BookModel bookModel = (BookModel) hibernateSession.get(BookModel.class, id);
		if (bookModel == null) {
			throw new NullBookException();
		}
		return bookModel;
	}
	
	// http://localhost:8080/13_MemberWS/spring/webservice/book
	// http://ilab.csie.ntut.edu.tw:8080/13_MemberWS/spring/webservice/book
	@RequestMapping(method = RequestMethod.GET, produces = "application/json")
	public @ResponseBody List<BookModel> list() throws Exception {
		List<BookModel> bookModelList;
		hibernateSession = hibernateSessionFactory.openSession();
		hibernateCriteria = hibernateSession.createCriteria(BookModel.class);
		bookModelList = hibernateCriteria.list();
		return bookModelList;	
	}
	
	// http://localhost:8080/13_MemberWS/spring/webservice/book/findByName
	// http://ilab.csie.ntut.edu.tw:8080/13_MemberWS/spring/webservice/book/findByName
	@RequestMapping(value = "/findByName", method = RequestMethod.GET, produces = "application/json")
	public @ResponseBody BookModel findByName(@RequestParam(value="bookName") String bookName) throws Exception {	
		List<BookModel> bookModelList;

		hibernateSession = hibernateSessionFactory.openSession();
		hibernateCriteria = hibernateSession.createCriteria(BookModel.class);
		hibernateCriteria.add(Restrictions.eq("bookName", bookName));
		bookModelList = hibernateCriteria.list();
		Iterator iterator = bookModelList.iterator();
		BookModel bookModel;
		if (iterator.hasNext()) {
			bookModel = (BookModel) iterator.next();
		} else {
			throw new NullBookException();
		}
		return bookModel;
	}
	
	@ExceptionHandler(NullBookException.class)
	@ResponseBody
	@ResponseStatus(value = HttpStatus.NOT_FOUND)
	public void handleNullAccountException(NullBookException e) {}
	
	@ExceptionHandler(Exception.class)
	@ResponseBody
	@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
	public void handleException(Exception e) {}
	
}
