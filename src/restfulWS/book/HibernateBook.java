package restfulWS.book;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.criterion.Restrictions;
import org.hibernate.service.ServiceRegistry;

import restfulWS.book.exceptions.NullBookException;
import restfulWS.book.model.BookModel;

import java.util.Iterator;
import java.util.List;

public class HibernateBook implements Book {
	
	private Session hibernateSession;
	private SessionFactory hibernateSessionFactory;
	private Criteria hibernateCriteria;
	private ServiceRegistry serviceRegistry;

	public HibernateBook() throws Exception {
		String configXML = "restfulWS/book/bookhibernate-config.xml";
		try {
			Configuration hibernateConfig = new Configuration().configure(configXML);
			serviceRegistry = new StandardServiceRegistryBuilder().applySettings(hibernateConfig.getProperties()).build();
			hibernateSessionFactory = hibernateConfig.buildSessionFactory(serviceRegistry);	
		} catch (Exception e) {
			throw e;
		}
	}
	
	public void create(BookModel bookModel) throws Exception {
		hibernateSession = hibernateSessionFactory.openSession();
		Transaction tx= hibernateSession.beginTransaction();
		hibernateSession.save(bookModel);
		tx.commit();	
		hibernateSession.close();
	}
	
	public void update(int id, BookModel bookModel) throws Exception {
		hibernateSession = hibernateSessionFactory.openSession();
		Transaction tx= hibernateSession.beginTransaction();
		hibernateSession.update(bookModel);
		tx.commit();
		hibernateSession.close();	
	}
	
	public void delete(int id) throws Exception {	
		hibernateSession = hibernateSessionFactory.openSession();
		Transaction tx = hibernateSession.beginTransaction();
		hibernateSession.delete(hibernateSession.load(BookModel.class, id));
		tx.commit();
		hibernateSession.close();
	}
	
	public BookModel find(int id) throws Exception {	
		hibernateSession = hibernateSessionFactory.openSession();
		BookModel bookModel = (BookModel) hibernateSession.get(BookModel.class, id);
		if (bookModel == null) {
			throw new NullBookException();
		}
		return bookModel;
	}
	
	public List<BookModel> list() throws Exception {
		List<BookModel> bookModelList;
		hibernateSession = hibernateSessionFactory.openSession();
		hibernateCriteria = hibernateSession.createCriteria(BookModel.class);
		bookModelList = hibernateCriteria.list();
		return bookModelList;	
	}
	
	public BookModel findByName(String bookName) throws Exception {	
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
}
