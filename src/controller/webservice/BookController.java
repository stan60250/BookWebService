package controller.webservice;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;

import javax.validation.Valid;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.criterion.Restrictions;
import org.hibernate.service.ServiceRegistry;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;

import restfulWS.auth.exceptions.NotLoginException;
import restfulWS.auth.model.MemberModel;
import restfulWS.book.exceptions.BookAlreadyExistException;
import restfulWS.book.exceptions.DataErrorException;
import restfulWS.book.exceptions.NotEnoughPointException;
import restfulWS.book.exceptions.NullBookException;
import restfulWS.book.exceptions.NullBookListException;
import restfulWS.book.exceptions.NullOptionException;
import restfulWS.book.model.BookModel;
import restfulWS.book.model.OwnerModel;
import restfulWS.book.model.UserModel;
import restfulWS.book.viewmodel.BookListModel;
import restfulWS.book.viewmodel.BookQueryModel;
import restfulWS.book.viewmodel.InfoModel;
import restfulWS.book.viewmodel.MenuItemConstant;
import restfulWS.book.viewmodel.MenuModel;
import restfulWS.book.viewmodel.MessageModel;

@Controller("controller.webservice.BookController")
@RequestMapping("/webservice")
public class BookController {

	private Session hibernateSession;
	private SessionFactory hibernateSessionFactory;
	private Criteria hibernateCriteria;
	private ServiceRegistry serviceRegistry;
	
	public BookController() throws Exception {
		String configXML = "restfulWS/book/bookhibernate-config.xml";
		try {
			Configuration hibernateConfig = new Configuration().configure(configXML);
			serviceRegistry = new StandardServiceRegistryBuilder().applySettings(hibernateConfig.getProperties()).build();
			hibernateSessionFactory = hibernateConfig.buildSessionFactory(serviceRegistry);	
		} catch (Exception e) {
			throw e;
		}
	}
	
	ApplicationContext context = new ClassPathXmlApplicationContext("controller/webservice/spring.xml");
	RestTemplate restTemplate = new RestTemplate();
	ResourceBundle resource = ResourceBundle.getBundle("resources.MessageDictionary");
	String ERROR = (String) context.getBean("ERROR");
	
	//============================================公共書架(首頁)(未登入)
	@RequestMapping(value = "/doListPublic", method = RequestMethod.GET)
	public ModelAndView doListPublic() {
		return doListPublicCommon(null);
	}
	
	//============================================公共書架(首頁)(已登入)
	@RequestMapping(value = "/doListPublicToken", method = RequestMethod.GET)
	public ModelAndView doListPublicToken(@RequestParam(value="token") String token) throws Exception {
		return doListPublicCommon(token);
	}
	
	//============================================公共書架(首頁)(公共)
	public ModelAndView doListPublicCommon(String token) {
		System.out.println("[book]doListPublic");
		if(token!=null) System.out.println("[book]token = " + token);
		
		List<FieldError> bookErrors = new ArrayList<FieldError>();
		List<BookModel> bookList;
		List<MenuModel> menuList;
		
		String book_service =(String)context.getBean("book_service");
		
		try {
			//取得首頁選單
			menuList = getMenuList(token);
			
			//取得書架
			bookList = restTemplate.getForObject(book_service, List.class);
			
			//空列表就顯示空的!! 實際DEMO我也不會讓空列表發生
			if (bookList.size() == 0) {
				//throw new NullListException();
				System.out.println("[book]Public List is null");
			}
		/*} catch (NullListException e) {
			bookErrors.add(new FieldError("ListController", e.getMessage(), resource.getString(e.getMessage())));
			return new ModelAndView(ERROR, "ErrorModel", bookErrors);*/
		} catch (RestClientException e) {
			bookErrors.add(new FieldError("BookController", "error.http", resource.getString("error.http")+"<br>"+e.getMessage()));
			return new ModelAndView(ERROR, "ErrorModel", bookErrors);
		} catch(Exception e){
			bookErrors.add(new FieldError("BookController", "error.database", resource.getString("error.database")+"<br>"+e.getMessage()));
			return new ModelAndView(ERROR, "ErrorModel", bookErrors);
		}
		
		BookListModel bookListModel = (BookListModel) context.getBean("bookListModel");
		bookListModel.setToken((token==null?"null":token));
		bookListModel.setBookListModel(bookList);
		bookListModel.setMenuListModel(menuList);
		
		String LIST = (String) context.getBean("listPublicSUCCESS");
		return new ModelAndView(LIST, "BookListModel", bookListModel);
	}

	//============================================個人書架(必須登入)
	@RequestMapping(value = "/doListPrivate", method = RequestMethod.GET)
	public ModelAndView doListPrivateToken(@RequestParam(value="token") String token) throws Exception {
		System.out.println("[book]doListPrivate");
		if(token!=null) System.out.println("[book]token = " + token);
		
		List<FieldError> bookErrors = new ArrayList<FieldError>();
		List<BookModel> bookList;
		List<MenuModel> menuList;
		
		String book_service =(String)context.getBean("book_service");
		
		try {
			//取得首頁選單
			menuList = getMenuList(token);
			
			//取得書架
			MemberModel userModel = getUserModel(token);
			if(userModel==null) throw new NotLoginException();
			
			bookList = getPrivateBookList(userModel.getUserID());
			if(bookList==null) throw new NullBookListException();
			
			//空列表
			if (bookList.size() == 0) {
				System.out.println("[book]Private List is null");
				throw new NullBookListException();
			}
		} catch (NotLoginException e) {
			bookErrors.add(new FieldError("BookController", e.getMessage(), resource.getString(e.getMessage())));
			return new ModelAndView(ERROR, "ErrorModel", bookErrors);
		} catch (NullBookListException e) {
			bookErrors.add(new FieldError("BookController", e.getMessage(), resource.getString(e.getMessage())));
			return new ModelAndView(ERROR, "ErrorModel", bookErrors);
		} catch (RestClientException e) {
			bookErrors.add(new FieldError("BookController", "error.http", resource.getString("error.http")+"<br>"+e.getMessage()));
			return new ModelAndView(ERROR, "ErrorModel", bookErrors);
		} catch(Exception e){
			bookErrors.add(new FieldError("BookController", "error.database", resource.getString("error.database")+"<br>"+e.getMessage()));
			return new ModelAndView(ERROR, "ErrorModel", bookErrors);
		}
		
		BookListModel bookListModel = (BookListModel) context.getBean("bookListModel");
		bookListModel.setToken(token);
		bookListModel.setBookListModel(bookList);
		bookListModel.setMenuListModel(menuList);
		
		String LIST = (String) context.getBean("listPrivateSUCCESS");
		return new ModelAndView(LIST, "BookListModel", bookListModel);
	}
	
	//============================================使用者資訊
	@RequestMapping(value = "/userInfo", method = RequestMethod.GET)
	public ModelAndView userInfo(@RequestParam(value="token") String token) throws Exception {
		System.out.println("[book]userInfo");
		System.out.println("[book]token = " + token);
		
		List<FieldError> authErrors = new ArrayList<FieldError>();
		MemberModel memModel;
		
		//從Auth取得使用者資訊
		try {
			//檢查token是否有效
			memModel = getUserModel(token);
			if(memModel==null) throw new NotLoginException();
			
		}catch (NotLoginException e) {
			authErrors.add(new FieldError("BookController", e.getMessage(), resource.getString(e.getMessage())));
			return new ModelAndView(ERROR, "ErrorModel", authErrors);
		}catch(Exception e){
			authErrors.add(new FieldError("BookController", "error.database", resource.getString("error.database")+"<br>"+e.getMessage()));
			return new ModelAndView(ERROR, "ErrorModel", authErrors);
		}
		
		//從BK_users取得使用者點數
		System.out.println("[auth]get user point " + memModel.getUserID());
		int userPoint = 100;
		try{
			List<UserModel> userModelList;
			hibernateSession = hibernateSessionFactory.openSession();
			hibernateCriteria = hibernateSession.createCriteria(UserModel.class);
			hibernateCriteria.add(Restrictions.eq("userID", memModel.getUserID()));
			userModelList = hibernateCriteria.list();
			Iterator iterator = userModelList.iterator();
			
			UserModel userModel = new UserModel();
			
			Transaction tx= hibernateSession.beginTransaction();
			//找到就輸出
			if(iterator.hasNext()){
				userPoint = ((UserModel)iterator.next()).getPoint();
			//找不到就新增
			}else{
				userModel.setUserID(userModel.getUserID());
				userModel.setPoint(userPoint);
				hibernateSession.save(userModel);
			}
			tx.commit();
			hibernateSession.close();
		}catch(Exception e){
			System.out.println("[auth]INSERT BK_users error");
			System.out.println(e.getMessage());
		}
		
		InfoModel infoModel = (InfoModel)context.getBean("infoModel");
		infoModel.setUserID(memModel.getUserID());
		infoModel.setUserPWHash("");
		infoModel.setUserName(memModel.getUserName());
		infoModel.setUserEmail(memModel.getUserEmail());
		infoModel.setUserToken(token);
		infoModel.setUserPoint(userPoint + "");
		
		String userInfoPage = (String) context.getBean("userInfo");
		return new ModelAndView(userInfoPage, "InfoModel", infoModel);
	}
	
	//============================================書籍上架LINK
	@RequestMapping(value = "/bookCreate", method = RequestMethod.GET)
	public ModelAndView bookCreate(@RequestParam(value="token") String token) {
		List<FieldError> bookErrors = new ArrayList<FieldError>();
		//驗證是否登入
		try {
			if(getUserModel(token) == null){
				throw new NotLoginException();
			}
		}catch(NotLoginException e){
			bookErrors.add(new FieldError("BookController", e.getMessage(), resource.getString(e.getMessage())));
			return new ModelAndView(ERROR, "ErrorModel", bookErrors);
		}catch(Exception e){
			bookErrors.add(new FieldError("BookController", "error.database", resource.getString("error.database")+"<br>"+e.getMessage()));
			return new ModelAndView(ERROR, "ErrorModel", bookErrors);
		}

		InfoModel infoModel = (InfoModel)context.getBean("infoModel");
		infoModel.setUserToken(token);

		String bookCreatePage = (String) context.getBean("bookCreate");
		return new ModelAndView(bookCreatePage, "InfoModel", infoModel);
	}
	
	//============================================書籍上架動作
	@RequestMapping(value = "/doBookCreate", method = RequestMethod.POST)
	public ModelAndView doBookCreate(@RequestParam(value="token") String token, @Valid BookModel bookModel, BindingResult bindingResult){
		
		List<FieldError> bookErrors = new ArrayList<FieldError>();
		if (bindingResult.hasErrors()) {
			return new ModelAndView(ERROR, "ErrorModel", bindingResult.getFieldErrors());
		}
		
		List<BookModel> bookModelList;

		try {
			
			hibernateSession = hibernateSessionFactory.openSession();
			hibernateCriteria = hibernateSession.createCriteria(BookModel.class);
			hibernateCriteria.add(Restrictions.eq("bookName", bookModel.getBookName()));
			bookModelList = hibernateCriteria.list();
			Iterator iterator = bookModelList.iterator();
			
			if(bookModel.getBookName()==null || bookModel.getBookName().equals("") ||
					bookModel.getBookAbstract()==null || bookModel.getBookAbstract().equals("") ||
					bookModel.getBookCover()==null || bookModel.getBookCover().equals("") ||
					bookModel.getBookContent()==null || bookModel.getBookContent().equals("")){
				throw new DataErrorException();
			}
			
			MemberModel memModel = getUserModel(token);
			if(memModel == null){
				throw new NotLoginException();
			}
			
			Transaction tx= hibernateSession.beginTransaction();
			if(iterator.hasNext()){
				throw new BookAlreadyExistException();
			}else{
				bookModel.setBookCreator(memModel.getUserID());
				hibernateSession.save(bookModel);
			}
			tx.commit();
			hibernateSession.close();
			
			//把該書籍加入該user的list
			addBookToUser(getBookID(bookModel.getBookName()), memModel.getUserID());
			//上架一本書+10點
			addPointToUser(10, memModel.getUserID());
		
		}catch(BookAlreadyExistException e){
			bookErrors.add(new FieldError("BookController", e.getMessage(), resource.getString(e.getMessage())));
			return new ModelAndView(ERROR, "ErrorModel", bookErrors);
		}catch(NotLoginException e){
			bookErrors.add(new FieldError("BookController", e.getMessage(), resource.getString(e.getMessage())));
			return new ModelAndView(ERROR, "ErrorModel", bookErrors);
		}catch(DataErrorException e){
			bookErrors.add(new FieldError("BookController", e.getMessage(), resource.getString(e.getMessage())));
			return new ModelAndView(ERROR, "ErrorModel", bookErrors);
		}catch(Exception e){
			bookErrors.add(new FieldError("BookController", "error.database", resource.getString("error.database")+"<br>"+e.getMessage()));
			return new ModelAndView(ERROR, "ErrorModel", bookErrors);
		}
		
		MessageModel messageModel = (MessageModel)context.getBean("messageModel");
		messageModel.setMessage("書籍上架成功!<br>5 秒後將自動回到首頁!");
		messageModel.setUrl("doListPrivate?token=" + token);
		messageModel.setSec("5");
		
		String SUCCESS = (String) context.getBean("bookCreateSUCCESS");
		return new ModelAndView(SUCCESS, "MessageModel", messageModel);
		
	}
	
	//============================================書籍下架LINK
	@RequestMapping(value = "/bookDelete", method = RequestMethod.GET)
	public ModelAndView bookDelete(@RequestParam(value="token") String token) {
		System.out.println("[book]bookDelete");
		List<FieldError> bookErrors = new ArrayList<FieldError>();
		List<BookModel> bookModelList;
		
		try {
			MemberModel userModel = getUserModel(token);
			if(userModel==null) throw new NotLoginException();
			
			bookModelList = getPrivateBookList(userModel.getUserID());
			if(bookModelList==null) throw new NullBookListException();
			
		}catch(NullBookListException e){
			bookErrors.add(new FieldError("BookController", e.getMessage(), resource.getString(e.getMessage())));
			return new ModelAndView(ERROR, "ErrorModel", bookErrors);
		}catch(NotLoginException e){
			bookErrors.add(new FieldError("BookController", e.getMessage(), resource.getString(e.getMessage())));
			return new ModelAndView(ERROR, "ErrorModel", bookErrors);
		}catch(Exception e){
			bookErrors.add(new FieldError("BookController", "error.database", resource.getString("error.database")+"<br>"+e.getMessage()));
			return new ModelAndView(ERROR, "ErrorModel", bookErrors);
		}
		
		BookListModel bookListModel = (BookListModel)context.getBean("bookListModel");
		bookListModel.setBookListModel(bookModelList);
		bookListModel.setToken(token);
		
		String bookDeletePage = (String) context.getBean("bookDelete");
		return new ModelAndView(bookDeletePage, "BookListModel", bookListModel);
	}
	
	//============================================書籍下架動作
	@RequestMapping(value = "/doBookDelete", method = RequestMethod.POST)
	public ModelAndView doBookDelete(@RequestParam(value="token") String token, @Valid BookModel bookModel, BindingResult bindingResult){
		List<FieldError> bookErrors = new ArrayList<FieldError>();
		if (bindingResult.hasErrors()) {
			return new ModelAndView(ERROR, "ErrorModel", bindingResult.getFieldErrors());
		}
		
		boolean flag = false;
		
		try {
			
			MemberModel memModel = getUserModel(token);
			if(memModel == null)throw new NotLoginException();

			//判定id=0的例外
			if(bookModel.getId()==0)throw new NullOptionException();
			
			removeBookFromUser(bookModel.getId(), memModel.getUserID());
			
			flag = (getPrivateBookList(memModel.getUserID())!=null);
		
		}catch(NullOptionException e){
			bookErrors.add(new FieldError("BookController", e.getMessage(), resource.getString(e.getMessage())));
			return new ModelAndView(ERROR, "ErrorModel", bookErrors);
		}catch(NotLoginException e){
			bookErrors.add(new FieldError("BookController", e.getMessage(), resource.getString(e.getMessage())));
			return new ModelAndView(ERROR, "ErrorModel", bookErrors);
		}catch(Exception e){
			bookErrors.add(new FieldError("BookController", "error.database", resource.getString("error.database")+"<br>"+e.getMessage()));
			return new ModelAndView(ERROR, "ErrorModel", bookErrors);
		}
		
		MessageModel messageModel = (MessageModel)context.getBean("messageModel");
		messageModel.setMessage("書籍下架成功!<br>5 秒後將自動回到首頁!");
		messageModel.setUrl((flag?"doListPrivate":"doListPublicToken") + "?token=" + token);
		messageModel.setSec("5");
		
		String SUCCESS = (String) context.getBean("bookDeleteSUCCESS");
		return new ModelAndView(SUCCESS, "MessageModel", messageModel);
		
	}
	
	//============================================書籍搜尋LINK
	@RequestMapping(value = "/bookQuery", method = RequestMethod.GET)
	public ModelAndView bookQuery(@RequestParam(value="token") String token) {
		System.out.println("[book]bookQuery");
		List<FieldError> bookErrors = new ArrayList<FieldError>();
		
		try {
			MemberModel userModel = getUserModel(token);
			if(userModel==null) throw new NotLoginException();
			
		}catch(NotLoginException e){
			bookErrors.add(new FieldError("BookController", e.getMessage(), resource.getString(e.getMessage())));
			return new ModelAndView(ERROR, "ErrorModel", bookErrors);
		}catch(Exception e){
			bookErrors.add(new FieldError("BookController", "error.database", resource.getString("error.database")+"<br>"+e.getMessage()));
			return new ModelAndView(ERROR, "ErrorModel", bookErrors);
		}
			
		BookQueryModel bookQueryModel = (BookQueryModel)context.getBean("bookQueryModel");
		bookQueryModel.setToken(token);
		
		String bookQueryPage = (String) context.getBean("bookQuery");
		return new ModelAndView(bookQueryPage, "BookQueryModel", bookQueryModel);
	}
	
	//============================================書籍搜尋動作
	@RequestMapping(value = "/doBookQuery", method = RequestMethod.POST)
	public ModelAndView doBookQuery(@RequestParam(value="token") String token, @Valid BookQueryModel bookQuery, BindingResult bindingResult){
		List<FieldError> bookErrors = new ArrayList<FieldError>();
		if (bindingResult.hasErrors()) {
			return new ModelAndView(ERROR, "ErrorModel", bindingResult.getFieldErrors());
		}
		
		List<BookModel> allBookList;
		List<BookModel> foundBookList = new ArrayList<BookModel>();
		
		try {
			
			MemberModel memModel = getUserModel(token);
			if(memModel == null)throw new NotLoginException();
			
			hibernateSession = hibernateSessionFactory.openSession();
			hibernateCriteria = hibernateSession.createCriteria(BookModel.class);
			allBookList = hibernateCriteria.list();
			Iterator iterator = allBookList.iterator();
			hibernateSession.close();
			if(iterator.hasNext()){
				for(BookModel book : allBookList) {
					if(
						(bookQuery.getOptBookName()==0&&(!bookQuery.getBookName().equals(""))&&book.getBookName().indexOf(bookQuery.getBookName())>-1)||
						(bookQuery.getOptBookName()==1&&(!bookQuery.getBookName().equals(""))&&book.getBookName().equals(bookQuery.getBookName()))||
						(bookQuery.getOptBookAbstract()==0&&(!bookQuery.getBookAbstract().equals(""))&&book.getBookAbstract().indexOf(bookQuery.getBookAbstract())>-1)||
						(bookQuery.getOptBookAbstract()==1&&(!bookQuery.getBookAbstract().equals(""))&&book.getBookAbstract().equals(bookQuery.getBookAbstract()))||
						(bookQuery.getOptBookCreator()==0&&(!bookQuery.getBookCreator().equals(""))&&book.getBookCreator().indexOf(bookQuery.getBookCreator())>-1)||
						(bookQuery.getOptBookCreator()==1&&(!bookQuery.getBookCreator().equals(""))&&book.getBookCreator().equals(bookQuery.getBookCreator()))
					){
						foundBookList.add(book);
					}	
				}
				if(foundBookList.size()<=0) throw new NullBookListException();
			}else{
				throw new NullBookListException();
			}
			
		}catch(NotLoginException e){
			bookErrors.add(new FieldError("BookController", e.getMessage(), resource.getString(e.getMessage())));
			return new ModelAndView(ERROR, "ErrorModel", bookErrors);
		}catch(NullBookListException e){
			
			MessageModel messageModel = (MessageModel)context.getBean("messageModel");
			messageModel.setMessage("沒有找到符合資格的書籍!<br>5 秒後將自動回到首頁!");
			messageModel.setUrl("doListPrivate?token=" + token);
			messageModel.setSec("5");
			
			String SUCCESS = (String) context.getBean("MSG");
			return new ModelAndView(SUCCESS, "MessageModel", messageModel);
			
		}catch(Exception e){
			bookErrors.add(new FieldError("BookController", "error.database", resource.getString("error.database")+"<br>"+e.getMessage()));
			return new ModelAndView(ERROR, "ErrorModel", bookErrors);
		}
		
		BookListModel bookListModel = (BookListModel) context.getBean("bookListModel");
		bookListModel.setToken(token);
		bookListModel.setBookListModel(foundBookList);
		bookListModel.setMenuListModel(getMenuList(token));
		
		String LIST = (String) context.getBean("listPublicSUCCESS");
		return new ModelAndView(LIST, "BookListModel", bookListModel);
		
	}
	
	//============================================書籍閱讀LINK
	@RequestMapping(value = "/bookView", method = RequestMethod.GET)
	public ModelAndView bookView(@RequestParam(value="token") String token, @RequestParam(value="id") String id) {
		return bookViewOrBuy(token,id);
	}
	
	//============================================書籍訂閱LINK
	@RequestMapping(value = "/bookPay", method = RequestMethod.GET)
	public ModelAndView bookPay(@RequestParam(value="token") String token, @RequestParam(value="id") String id) {
		return bookViewOrBuy(token,id);
	}
	
	public ModelAndView bookViewOrBuy(String token, String id){
		List<FieldError> bookErrors = new ArrayList<FieldError>();
		BookModel bookModel;
		boolean paid = false;
		
		//驗證是否登入
		try {
			//檢查登入有效
			MemberModel userModel = getUserModel(token);
			if(userModel==null) throw new NotLoginException();
			
			//檢查有這本書
			bookModel = getBookModel(Integer.parseInt(id));
			if(bookModel==null) throw new NullBookException();
			
			//檢查使用者有無付費
			paid = hasPaid(Integer.parseInt(id),userModel.getUserID());
		
		}catch(NotLoginException e){
			//提示需要登入
			/*bookErrors.add(new FieldError("BookController", e.getMessage(), resource.getString(e.getMessage())));
			return new ModelAndView(ERROR, "ErrorModel", bookErrors);*/
			
			//直接引導去登入
			MessageModel messageModel = (MessageModel)context.getBean("messageModel");
			messageModel.setMessage("必須是會員才能訂閱書籍!<br>5 秒後將引導至登入頁面!");
			messageModel.setUrl("userLogin");
			messageModel.setSec("5");
			String showMsgPage = (String) context.getBean("MSG");
			return new ModelAndView(showMsgPage, "MessageModel", messageModel);
		}catch(NullBookException e){
			bookErrors.add(new FieldError("BookController", e.getMessage(), resource.getString(e.getMessage())));
			return new ModelAndView(ERROR, "ErrorModel", bookErrors);
		}catch(Exception e){
			bookErrors.add(new FieldError("BookController", "error.database", resource.getString("error.database")+"<br>"+e.getMessage()));
			return new ModelAndView(ERROR, "ErrorModel", bookErrors);
		}
		
		BookModel viewBookModel = (BookModel)context.getBean("bookModel");
		viewBookModel.setId(bookModel.getId());
		viewBookModel.setBookName(bookModel.getBookName());
		viewBookModel.setBookCost(bookModel.getBookCost());
		viewBookModel.setBookCover(bookModel.getBookCover());
		viewBookModel.setBookCreator(bookModel.getBookCreator());
		viewBookModel.setBookAbstract(bookModel.getBookAbstract());
		
		if(paid){
			//有付費, 直接瀏覽
			viewBookModel.setBookContent(bookModel.getBookContent());
			String bookViewPage = (String) context.getBean("bookView");
			return new ModelAndView(bookViewPage, "BookModel", viewBookModel);
		}else{
			//沒付費, 引導去付費
			//借用bookModel傳送token
			viewBookModel.setBookContent(token);
			String bookPayPage = ((String)context.getBean("bookPay"));
			return new ModelAndView(bookPayPage, "BookModel", viewBookModel);
		}
	}
	
	//============================================書籍訂閱動作
	@RequestMapping(value = "/doBookPay", method = RequestMethod.POST)
	public ModelAndView doBookPay(@RequestParam(value="token") String token, @Valid BookModel bookModel, BindingResult bindingResult) {
		
		List<FieldError> bookErrors = new ArrayList<FieldError>();
		//BookModel bookModel;
		
		//驗證是否登入
		try {
			//檢查登入有效
			MemberModel userModel = getUserModel(token);
			if(userModel==null) throw new NotLoginException();
			
			//檢查有這本書
			bookModel = getBookModel(bookModel.getId());
			if(bookModel==null) throw new NullBookException();
			
			//檢查使用者點數足夠
			if(!isEnoughPoint(bookModel.getBookCost()*-1, userModel.getUserID())) throw new NotEnoughPointException();
			
			//開始交易
			//上傳者得到點數
			addPointToUser(bookModel.getBookCost(), bookModel.getBookCreator());
			//訂閱者扣除點數
			addPointToUser(bookModel.getBookCost()*-1, userModel.getUserID());
			//訂閱者得到書籍
			addBookToUser(bookModel.getId(), userModel.getUserID());
		
		}catch(NotEnoughPointException e){
			bookErrors.add(new FieldError("BookController", e.getMessage(), resource.getString(e.getMessage())));
			return new ModelAndView(ERROR, "ErrorModel", bookErrors);
		}catch(NotLoginException e){
			//提示需要登入
			/*bookErrors.add(new FieldError("BookController", e.getMessage(), resource.getString(e.getMessage())));
			return new ModelAndView(ERROR, "ErrorModel", bookErrors);*/
			//直接引導去登入
			String userLoginPage = (String) context.getBean("userLogin");
			return new ModelAndView(userLoginPage);
		}catch(NullBookException e){
			bookErrors.add(new FieldError("BookController", e.getMessage(), resource.getString(e.getMessage())));
			return new ModelAndView(ERROR, "ErrorModel", bookErrors);
		}catch(Exception e){
			bookErrors.add(new FieldError("BookController", "error.database", resource.getString("error.database")+"<br>"+e.getMessage()));
			return new ModelAndView(ERROR, "ErrorModel", bookErrors);
		}
				
		MessageModel messageModel = (MessageModel)context.getBean("messageModel");
		messageModel.setMessage("書籍訂閱成功!<br>5 秒後將自動回到首頁!");
		messageModel.setUrl("doListPrivate?token=" + token);
		messageModel.setSec("5");
		
		String SUCCESS = (String) context.getBean("bookPaySUCCESS");
		return new ModelAndView(SUCCESS, "MessageModel", messageModel);
	}
	
	public int getBookID(String bookName){
		System.out.println("[book]getBookID bookName:" + bookName);
		List<BookModel> bookModelList;
		
		try{
			hibernateSession = hibernateSessionFactory.openSession();
			hibernateCriteria = hibernateSession.createCriteria(BookModel.class);
			hibernateCriteria.add(Restrictions.eq("bookName", bookName));
			bookModelList = hibernateCriteria.list();
			Iterator iterator = bookModelList.iterator();
			hibernateSession.close();
			if(iterator.hasNext()){
				System.out.println("[book]OK");
				return ((BookModel)iterator.next()).getId();
			}else{
				System.out.println("[book]Book: " + bookName  + "not found");
				return 0;
			}
		}catch(Exception e){
			System.out.println("[book]error " + e.getMessage());
			return 0;
		}
	}
	
	public BookModel getBookModel(int id){
		System.out.println("[book]getBookModel id:" + id);
		List<BookModel> bookModelList;
		
		try{
			hibernateSession = hibernateSessionFactory.openSession();
			hibernateCriteria = hibernateSession.createCriteria(BookModel.class);
			hibernateCriteria.add(Restrictions.eq("id", id));
			bookModelList = hibernateCriteria.list();
			Iterator iterator = bookModelList.iterator();
			hibernateSession.close();
			if(iterator.hasNext()){
				System.out.println("[book]OK");
				return ((BookModel)iterator.next());
			}else{
				System.out.println("[book]Book: " + id  + "not found");
				return null;
			}
		}catch(Exception e){
			System.out.println("[book]error " + e.getMessage());
			return null;
		}
	}
	
	public void addPointToUser(int point, String userID){
		System.out.println("[book]addPointToUser point= " + point + " TO " + userID);
		List<UserModel> userModelList;
		
		try{
			hibernateSession = hibernateSessionFactory.openSession();
			hibernateCriteria = hibernateSession.createCriteria(UserModel.class);
			hibernateCriteria.add(Restrictions.eq("userID", userID));
			userModelList = hibernateCriteria.list();
			Iterator iterator = userModelList.iterator();
			
			Transaction tx= hibernateSession.beginTransaction();
			if(iterator.hasNext()){
				System.out.println("[book]OK");
				UserModel userModel = (UserModel)iterator.next();
				userModel.setPoint(userModel.getPoint() + point);
				hibernateSession.update(userModel);
				tx.commit();
			}else{
				System.out.println("[book]User: " + userID  + "not found");
			}
			hibernateSession.close();
		}catch(Exception e){
			System.out.println("[book]error " + e.getMessage());
			throw e;
		}
	}
	
	public boolean isEnoughPoint(int point, String userID){
		System.out.println("[book]isEnoughtPoint point= " + point + " TO " + userID);
		List<UserModel> userModelList;
		
		try{
			hibernateSession = hibernateSessionFactory.openSession();
			hibernateCriteria = hibernateSession.createCriteria(UserModel.class);
			hibernateCriteria.add(Restrictions.eq("userID", userID));
			userModelList = hibernateCriteria.list();
			Iterator iterator = userModelList.iterator();
			hibernateSession.close();
			if(iterator.hasNext()){
				System.out.println("[book]OK");
				return ((((UserModel)iterator.next()).getPoint()+point)>=0);
			}else{
				System.out.println("[book]User: " + userID  + "not found");
				return false;
			}
		}catch(Exception e){
			System.out.println("[book]error " + e.getMessage());
			throw e;
		}
	}
	
	public List<BookModel> getPrivateBookList(String userID){
		System.out.println("[book]getPrivateBookList user:" + userID);
		
		List<BookModel> bookModelList = new ArrayList<BookModel>();
		List<OwnerModel> ownerModelList;
		
		try{
			hibernateSession = hibernateSessionFactory.openSession();
			hibernateCriteria = hibernateSession.createCriteria(OwnerModel.class);
			hibernateCriteria.add(Restrictions.eq("userID", userID));
			ownerModelList = hibernateCriteria.list();
			Iterator iterator = ownerModelList.iterator();
			hibernateSession.close();
			if(iterator.hasNext()){
				for(OwnerModel ownerModel : ownerModelList) {
					bookModelList.add(getBookModel(ownerModel.getBookID()));
				}
				return bookModelList;
			}else{
				return null;
			}
		}catch(Exception e){
			System.out.println("[book]error " + e.getMessage());
			throw e;
		}
	}
	
	
	public void addBookToUser(int bookID, String userID){
		System.out.println("[book]addBookToUser bookID= " + bookID + " TO " + userID);
		List<OwnerModel> ownModelList;
		
		try{
			hibernateSession = hibernateSessionFactory.openSession();
			hibernateCriteria = hibernateSession.createCriteria(OwnerModel.class);
			hibernateCriteria.add(Restrictions.eq("bookID", bookID));
			hibernateCriteria.add(Restrictions.eq("userID", userID));
			ownModelList = hibernateCriteria.list();
			Iterator iterator = ownModelList.iterator();
			
			OwnerModel ownerModel = new OwnerModel();
			ownerModel.setBookID(bookID);
			ownerModel.setUserID(userID);
			
			Transaction tx= hibernateSession.beginTransaction();
			if(iterator.hasNext()){
				System.out.println("[book]User: " + userID  + "already had bookID= " + bookID);
			}else{
				System.out.println("[book]OK");
				hibernateSession.save(ownerModel);
				tx.commit();
			}
			hibernateSession.close();
		}catch(Exception e){
			System.out.println("[book]error " + e.getMessage());
			throw e;
		}
	}
	
	public void removeBookFromUser(int bookID, String userID){
		System.out.println("[book]removeBookFromUser bookID= " + bookID + " FROM " + userID);
		List<OwnerModel> ownModelList;
		
		try{
			hibernateSession = hibernateSessionFactory.openSession();
			hibernateCriteria = hibernateSession.createCriteria(OwnerModel.class);
			hibernateCriteria.add(Restrictions.eq("bookID", bookID));
			hibernateCriteria.add(Restrictions.eq("userID", userID));
			ownModelList = hibernateCriteria.list();
			Iterator iterator = ownModelList.iterator();
			
			Transaction tx= hibernateSession.beginTransaction();
			if(iterator.hasNext()){
				System.out.println("[book]OK");
				hibernateSession.delete(iterator.next());
				tx.commit();
			}else{
				System.out.println("[book]User: " + userID  + "don't own bookID= " + bookID);
			}
			hibernateSession.close();
		}catch(Exception e){
			System.out.println("[book]error " + e.getMessage());
			throw e;
		}
	}
	
	public boolean hasPaid(int bookID, String userID){
		System.out.println("[book]hasPaid bookID= " + bookID + " TO " + userID);
		List<OwnerModel> ownModelList;
		try{
			hibernateSession = hibernateSessionFactory.openSession();
			hibernateCriteria = hibernateSession.createCriteria(OwnerModel.class);
			hibernateCriteria.add(Restrictions.eq("bookID", bookID));
			hibernateCriteria.add(Restrictions.eq("userID", userID));
			ownModelList = hibernateCriteria.list();
			Iterator iterator = ownModelList.iterator();
			hibernateSession.close();
			if(iterator.hasNext()){
				System.out.println("[book]yes");
				return true;
			}else{
				System.out.println("[book]no");
				return false;
			}
		}catch(Exception e){
			System.out.println("[book]error " + e.getMessage());
			throw e;
		}
	}
	
	//依據token建立左側選單
	public List<MenuModel> getMenuList(String token){
		System.out.println("[book]getMenuList: " + token);
		List<MenuModel> userMenuList = new ArrayList<MenuModel>();

		//已登入的瀏覽者
		if((token != null)&&(getUserModel(token) != null)){
			userMenuList.add(new MenuModel(MenuItemConstant.TEXT_LIST_PUBLIC,MenuItemConstant.URL_LIST_PUBLIC_TOKEN + token));
			userMenuList.add(new MenuModel(MenuItemConstant.TEXT_LIST_PRIVATE,MenuItemConstant.URL_LIST_PRIVATE_TOKEN + token));
			userMenuList.add(new MenuModel(MenuItemConstant.TEXT_BOOK_CREATE,MenuItemConstant.URL_BOOK_CREATE_TOKEN + token));
			userMenuList.add(new MenuModel(MenuItemConstant.TEXT_BOOK_DELETE,MenuItemConstant.URL_BOOK_DELETE_TOKEN + token));
			userMenuList.add(new MenuModel(MenuItemConstant.TEXT_BOOK_QUERY,MenuItemConstant.URL_BOOK_QUERY_TOKEN + token));
			userMenuList.add(new MenuModel(MenuItemConstant.TEXT_USER_QUERY,MenuItemConstant.URL_USER_QUERY_TOKEN + token));
			userMenuList.add(new MenuModel(MenuItemConstant.TEXT_USER_MODIFY,MenuItemConstant.URL_USER_MODIFY_TOKEN + token));
			userMenuList.add(new MenuModel(MenuItemConstant.TEXT_USER_LOGOUT,MenuItemConstant.URL_USER_LOGOUT_TOKEN + token));
		//未登入的瀏覽者
		}else{
			userMenuList.add(new MenuModel(MenuItemConstant.TEXT_LIST_PUBLIC,MenuItemConstant.URL_LIST_PUBLIC));
			userMenuList.add(new MenuModel(MenuItemConstant.TEXT_USER_LOGIN,MenuItemConstant.URL_USER_LOGIN));
			userMenuList.add(new MenuModel(MenuItemConstant.TEXT_USER_REGIST,MenuItemConstant.URL_USER_REGIST));
		}
		
		return userMenuList;
	}
	
	//依照token取得USER資料(驗證token是否正確)
	public MemberModel getUserModel(String token){
		System.out.println("[book]getUserModel token = " + token);
		String auth_service =(String)context.getBean("auth_service");
		try {
			return restTemplate.getForObject(auth_service + "/" + token, MemberModel.class);
		} catch(Exception e){
			System.out.println("[book]error: " + e.getMessage());
			return null;
		}
	}
}
