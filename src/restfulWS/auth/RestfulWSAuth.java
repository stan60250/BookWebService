package restfulWS.auth;

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

import restfulWS.auth.exceptions.AccountAlreadyExistException;
import restfulWS.auth.exceptions.EmailToLoginException;
import restfulWS.auth.exceptions.ErrorPasswordException;
import restfulWS.auth.exceptions.ErrorUserEmailException;
import restfulWS.auth.exceptions.ErrorUserIDException;
import restfulWS.auth.exceptions.ErrorUserNameException;
import restfulWS.auth.exceptions.NeedConfirmEmailException;
import restfulWS.auth.exceptions.NotLoginException;
import restfulWS.auth.exceptions.NullAccountException;
import restfulWS.auth.exceptions.NullPasswordException;
import restfulWS.auth.model.AuthModel;
import restfulWS.auth.model.EmailModel;
import restfulWS.auth.model.MemberModel;
import restfulWS.mail.GoogleMail;

import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

@RestController("restfulWS.RestfulWSAuth")
@RequestMapping("/webservice/auth")
public class RestfulWSAuth implements Auth {
	
	private Session hibernateSession;
	private SessionFactory hibernateSessionFactory;
	private Criteria hibernateCriteria;
	private ServiceRegistry serviceRegistry;

	public RestfulWSAuth() throws Exception {
		String configXML = "restfulWS/auth/authhibernate-config.xml";
		try {
			Configuration hibernateConfig = new Configuration().configure(configXML);
			serviceRegistry = new StandardServiceRegistryBuilder().applySettings(hibernateConfig.getProperties()).build();
			hibernateSessionFactory = hibernateConfig.buildSessionFactory(serviceRegistry);	
		} catch (Exception e) {
			throw e;
		}
	}

	// 註冊帳號
	// http://localhost:8080/13_MemberWS/spring/webservice/auth/create
	// http://ilab.csie.ntut.edu.tw:8080/13_MemberWS/spring/webservice/auth/create
	// 登入帳號
	// http://localhost:8080/13_MemberWS/spring/webservice/auth/login
	// http://ilab.csie.ntut.edu.tw:8080/13_MemberWS/spring/webservice/auth/login
	@RequestMapping(method = RequestMethod.POST, value = "/{action}")
	public @ResponseBody void create(@PathVariable("action") String action, @RequestBody MemberModel memModel) throws Exception {
		//建立帳號
		if(action.equals("create")){
			System.out.println("[auth]ADD User:" + memModel.getUserID());
			
			//檢查資料是否齊全
			if(memModel.getUserID() == null || memModel.getUserID().equals("") || memModel.getUserID().length() > 16){
				System.out.println("[auth]ErrorUserIDException");
				throw new ErrorUserIDException();
			}
			if(memModel.getUserPWHash() == null || memModel.getUserPWHash().equals("")){
				System.out.println("[auth]NullPasswordException");
				throw new NullPasswordException();
			}
			if(memModel.getUserName() == null || memModel.getUserName().equals("") || memModel.getUserName().length() > 50){
				System.out.println("[auth]ErrorUserNameException");
				throw new ErrorUserNameException();
			}
			Pattern EMAIL_PATTERN = Pattern.compile("^\\w+\\.*\\w+@(\\w+\\.){1,5}[a-zA-Z]{2,3}$");
			if(memModel.getUserEmail() == null || memModel.getUserEmail().equals("") || (!EMAIL_PATTERN.matcher(memModel.getUserEmail()).matches())){
				System.out.println("[auth]ErrorUserEmailException");
				throw new ErrorUserEmailException();
			} 
			
			//檢查帳號是否已存在
			if(findUserId(memModel.getUserID())){
				System.out.println("[auth]AccountAlreadyExistException");
				throw new AccountAlreadyExistException();
			}
			
			memModel.setUserPWHash(getToken(memModel.getUserPWHash())); //加密密碼
			memModel.setEnable(0); //設定為停用(等待email啟用)
			
			ApplicationContext context = new ClassPathXmlApplicationContext("controller/webservice/spring.xml");
			
			//寄出驗證email
			GoogleMail GmailAgent = new GoogleMail();
			String result = GmailAgent.sendAuthMail(memModel.getUserEmail(),
					"JAVA 13 期末專案 - 會員Email驗證信",
					"親愛的會員 " + memModel.getUserName() + "(" + memModel.getUserID() + ") 您好:\n\n"
					+ "這是會員帳號驗證信\n"
					+ "請點擊下方的網址進行驗證\n"
					//+ "http://localhost:8080/13_MemberWS/spring/webservice/auth/"
					//+ "http://ilab.csie.ntut.edu.tw:8080/13_MemberWS/spring/webservice/auth/"
					+ ((String)context.getBean("emailConfirmAddress"))
					+ getToken(memModel.getUserID()));
			System.out.println("[auth]Auth Email:" + result);
			
			//資料庫操作
			hibernateSession = hibernateSessionFactory.openSession();
			Transaction tx= hibernateSession.beginTransaction();
			//將帳號登記在WS_member內
			hibernateSession.save(memModel);
			tx.commit();
			hibernateSession.close();
			
			//將驗證email登記在WS_email內
			EmailModel emlModel = new EmailModel();
			emlModel.setId(memModel.getId());
			emlModel.setUserID(memModel.getUserID());
			emlModel.setRequestTime(new Date().toString());
			emlModel.setAccessToken(getToken(emlModel.getUserID()));
			regMailToken(emlModel);
		
		//登入帳號
		}else if(action.equals("login")){
			System.out.println("[auth]LOGIN User:" + memModel.getUserID());
			
			//檢查帳號狀態
			List<MemberModel> memModelList;
			hibernateSession = hibernateSessionFactory.openSession();
			hibernateCriteria = hibernateSession.createCriteria(MemberModel.class);
			hibernateCriteria.add(Restrictions.eq("userID", memModel.getUserID()));
			memModelList = hibernateCriteria.list();
			hibernateSession.close();
			Iterator iterator = memModelList.iterator();
			if(iterator.hasNext()){
				MemberModel findMem = (MemberModel)iterator.next();
				if(findMem.getEnable()==0){
					//帳號沒啟用
					System.out.println("[auth]NEED TO Confirm Email First");
					throw new NeedConfirmEmailException();
				}
			}else{
				//找不到帳號(userID)
				System.out.println("[auth]Account Not Found");
				throw new NullAccountException();
			}
			
			if(loginUser(memModel)){
				System.out.println("[auth]LOGIN SUCCESS");
				regAccessToken(memModel);
			}else{
				System.out.println("[auth]LOGIN FAIL");
				throw new ErrorPasswordException();
			}
		}else{
			System.out.println("[auth]Unknow Action");
		}
	}
	
	//更新帳號資料
	// http://localhost:8080/13_MemberWS/spring/webservice/auth/{token}
	// http://ilab.csie.ntut.edu.tw:8080/13_MemberWS/spring/webservice/auth/{token}
	@RequestMapping(method = RequestMethod.PUT, value = "/{token}")
	public @ResponseBody void update(@PathVariable("token") String token, @RequestBody MemberModel memModel) throws Exception {
		//如果該連線已登入
		if(hasLogin(token)){
			
			MemberModel memModelToken = getMemberModel(token);
			if(memModelToken == null){
				//找不到
				System.out.println("[auth]not found");
				throw new NullAccountException();
			}else{
				System.out.println("[auth]UPDATE User:" + memModelToken.getUserID());
				
				//檢查資料是否齊全
				if(memModel.getUserPWHash() == null || memModel.getUserPWHash().equals("")){
					System.out.println("[auth]NullPasswordException");
					throw new NullPasswordException();
				}
				if(memModel.getUserName() == null || memModel.getUserName().equals("") || memModel.getUserName().length() > 50){
					System.out.println("[auth]ErrorUserNameException");
					throw new ErrorUserNameException();
				}
				Pattern EMAIL_PATTERN = Pattern.compile("^\\w+\\.*\\w+@(\\w+\\.){1,5}[a-zA-Z]{2,3}$");
				if(memModel.getUserEmail() == null || memModel.getUserEmail().equals("") || (!EMAIL_PATTERN.matcher(memModel.getUserEmail()).matches())){
					System.out.println("[auth]ErrorUserEmailException");
					throw new ErrorUserEmailException();
				}
				
				memModel.setId(memModelToken.getId());
				memModel.setUserID(memModelToken.getUserID());
				memModel.setUserPWHash(getToken(memModel.getUserPWHash()));
				memModel.setEnable(0);
				
				hibernateSession = hibernateSessionFactory.openSession();
				Transaction tx= hibernateSession.beginTransaction();
				hibernateSession.update(memModel);
				tx.commit();
				hibernateSession.close();
				
				ApplicationContext context = new ClassPathXmlApplicationContext("controller/webservice/spring.xml");
				
				//寄出驗證email
				GoogleMail GmailAgent = new GoogleMail();
				String result = GmailAgent.sendAuthMail(memModel.getUserEmail(),
						"JAVA 13 期末專案 - 會員Email驗證信",
						"親愛的會員 " + memModel.getUserName() + "(" + memModel.getUserID() + ") 您好:\n\n"
						+ "這是會員帳號驗證信\n"
						+ "請點擊下方的網址進行驗證\n"
						//+ "http://localhost:8080/13_MemberWS/spring/webservice/auth/"
						//+ "http://ilab.csie.ntut.edu.tw:8080/13_MemberWS/spring/webservice/auth/"
						+ ((String)context.getBean("emailConfirmAddress"))
						+ getToken(memModel.getUserID()));
				System.out.println("[auth]Auth Email:" + result);
				
				unregAccessToken(getToken(memModel.getUserID()));
				System.out.println("[auth]Clear Auth:" + memModel.getUserID());
				
				//將驗證email登記在WS_email內
				EmailModel emlModel = new EmailModel();
				emlModel.setId(memModel.getId());
				emlModel.setUserID(memModel.getUserID());
				emlModel.setRequestTime(new Date().toString());
				emlModel.setAccessToken(getToken(emlModel.getUserID()));
				regMailToken(emlModel);
			}
		}else{
			System.out.println("[auth]need to login first!");
			throw new NotLoginException();
		}
	}
	
	//登出帳號
	// http://localhost:8080/13_MemberWS/spring/webservice/auth/{token}
	// http://ilab.csie.ntut.edu.tw:8080/13_MemberWS/spring/webservice/auth/{token}
	@RequestMapping(method = RequestMethod.DELETE, value = "/{token}")
	public @ResponseBody void delete(@PathVariable("token") String token) throws Exception {
		
		//如果該連線已登入
		if(hasLogin(token)){
			MemberModel memModel = getMemberModel(token);
			if(memModel == null){
				//找不到
				System.out.println("[auth]not found");
				throw new NullAccountException();
			}else{
				System.out.println("[auth]DELETE Auth:" + memModel.getUserID());
				unregAccessToken(token);
			}
		}else{
			//要先登入才能登出!
			System.out.println("[auth]you can't logout when you didn't login");
			throw new NotLoginException();
		}
	}
	
	//驗證帳號 or 取得帳號資訊
	// http://localhost:8080/13_MemberWS/spring/webservice/auth/{token}
	// http://ilab.csie.ntut.edu.tw:8080/13_MemberWS/spring/webservice/auth/{token}
	@RequestMapping(method = RequestMethod.GET, value = "/{token}", produces = "application/json")
	public @ResponseBody MemberModel find(@PathVariable("token") String token) throws Exception {
		//如果該連線已登入
		if(hasLogin(token)){
			MemberModel memModel = getMemberModel(token);
			if(memModel == null){
				//找不到
				throw new NullAccountException();
			}else{
				return memModel;
			}
		
		//如果該連線未登入, 可能是驗證email
		}else{
			if(unregMailToken(token)){
				//成功驗證email, 跳轉至登入
				System.out.println("[auth]unregMailToken Complete" + token);
				//TODO 輸出至前端說"帳號已驗證"?
				return getMemberModel(token);
				//throw new EmailToLoginException();
			}else{
				//沒登入, 也不是驗證email, 要求登入
				System.out.println("[auth]you need to login first!");
				throw new NotLoginException();
			}
		}
	}
	
	//在WS_member中尋找userID
	public boolean findUserId(String userID){
		List<MemberModel> memModelList;
		hibernateSession = hibernateSessionFactory.openSession();
		hibernateCriteria = hibernateSession.createCriteria(MemberModel.class);
		hibernateCriteria.add(Restrictions.eq("userID", userID));
		memModelList = hibernateCriteria.list();
		hibernateSession.close();
		Iterator iterator = memModelList.iterator();
		return iterator.hasNext();
	}
	
	//在WS_member中檢查登入ID PW
	public boolean loginUser(MemberModel memModel){
		List<MemberModel> memModelList;
		hibernateSession = hibernateSessionFactory.openSession();
		hibernateCriteria = hibernateSession.createCriteria(MemberModel.class);
		hibernateCriteria.add(Restrictions.eq("userID", memModel.getUserID()));
		hibernateCriteria.add(Restrictions.eq("userPWHash", getToken(memModel.getUserPWHash())));
		memModelList = hibernateCriteria.list();
		hibernateSession.close();
		Iterator iterator = memModelList.iterator();
		return iterator.hasNext();
	}
	
	public void regAccessToken(MemberModel memModel){
		System.out.println("[auth]regAccessToken " + memModel.getUserID());
		List<AuthModel> authModelList;
		hibernateSession = hibernateSessionFactory.openSession();
		hibernateCriteria = hibernateSession.createCriteria(AuthModel.class);
		hibernateCriteria.add(Restrictions.eq("userID", memModel.getUserID()));
		authModelList = hibernateCriteria.list();
		Iterator iterator = authModelList.iterator();
		
		//在WS_auth內登記
		AuthModel authModel;
		
		Transaction tx= hibernateSession.beginTransaction();
		if(iterator.hasNext()){
			authModel = (AuthModel)iterator.next();
			authModel.setRequestTime(new Date().toString());
			hibernateSession.update(authModel);
		}else{
			authModel = new AuthModel();
			authModel.setUserID(memModel.getUserID());
			authModel.setAccessToken(getToken(memModel.getUserID()));
			authModel.setRequestTime(new Date().toString());
			hibernateSession.save(authModel);
		}
		tx.commit();
		hibernateSession.close();
	}
	
	public void unregAccessToken(String token){
		System.out.println("[auth]regAccessToken " + token);
		List<AuthModel> authModelList;
		hibernateSession = hibernateSessionFactory.openSession();
		hibernateCriteria = hibernateSession.createCriteria(AuthModel.class);
		hibernateCriteria.add(Restrictions.eq("accessToken", token));
		authModelList = hibernateCriteria.list();
		Iterator iterator = authModelList.iterator();

		Transaction tx= hibernateSession.beginTransaction();
		AuthModel authModel;
		if (iterator.hasNext()) {
			authModel = (AuthModel) iterator.next();
			hibernateSession.delete(authModel);
		}
		tx.commit();
		hibernateSession.close();
	}
	
	public void regMailToken(EmailModel emlModel){
		System.out.println("[auth]regMailToken " + emlModel.getAccessToken());
		List<EmailModel> emlModelList;
		hibernateSession = hibernateSessionFactory.openSession();
		hibernateCriteria = hibernateSession.createCriteria(EmailModel.class);
		hibernateCriteria.add(Restrictions.eq("userID", emlModel.getUserID()));
		emlModelList = hibernateCriteria.list();
		Iterator iterator = emlModelList.iterator();
		
		//在WS_email內登記
		EmailModel new_emlModel = new EmailModel();
		
		new_emlModel.setUserID(emlModel.getUserID());
		new_emlModel.setAccessToken(getToken(emlModel.getUserID()));
		new_emlModel.setRequestTime(new Date().toString());
		
		Transaction tx= hibernateSession.beginTransaction();
		if(iterator.hasNext()){
			hibernateSession.update(new_emlModel);
		}else{
			hibernateSession.save(new_emlModel);
		}
		tx.commit();
		hibernateSession.close();
	}
	
	public boolean unregMailToken(String token){
		System.out.println("[auth]unregMailToken " + token);
		List<EmailModel> emlModelList;
		hibernateSession = hibernateSessionFactory.openSession();
		hibernateCriteria = hibernateSession.createCriteria(EmailModel.class);
		hibernateCriteria.add(Restrictions.eq("accessToken", token));
		emlModelList = hibernateCriteria.list();
		Iterator iterator = emlModelList.iterator();

		Transaction tx= hibernateSession.beginTransaction();
		EmailModel emlModel;
		if (iterator.hasNext()) {
			emlModel = (EmailModel) iterator.next();
			hibernateSession.delete(emlModel);
			tx.commit();
			hibernateSession.close();
		}else{
			hibernateSession.close();
			System.out.println("[auth]email token not found");
			return false;
		}
		
		List<MemberModel> memModelList;
		hibernateSession = hibernateSessionFactory.openSession();
		hibernateCriteria = hibernateSession.createCriteria(MemberModel.class);
		hibernateCriteria.add(Restrictions.eq("userID", emlModel.getUserID()));
		memModelList = hibernateCriteria.list();
		iterator = memModelList.iterator();
		if(iterator.hasNext()){
			MemberModel memModel = (MemberModel)iterator.next();
			memModel.setEnable(1);
			tx= hibernateSession.beginTransaction();
			hibernateSession.update(memModel);
			System.out.println("[auth]ok");
			tx.commit();
			hibernateSession.close();
			return true;
		}else{
			System.out.println("[auth]null");
			hibernateSession.close();
			return false;
		}
	}
	
	public boolean hasLogin(String token){
		System.out.println("[auth]hasLogin " + token);
		List<AuthModel> authModelList;
		hibernateSession = hibernateSessionFactory.openSession();
		hibernateCriteria = hibernateSession.createCriteria(AuthModel.class);
		hibernateCriteria.add(Restrictions.eq("accessToken", token));
		authModelList = hibernateCriteria.list();
		hibernateSession.close();
		Iterator iterator = authModelList.iterator();
		return iterator.hasNext();
	}
	
	public AuthModel hasLoginGetModel(String token){
		System.out.println("[auth]hasLoginGetModel " + token);
		List<AuthModel> authModelList;
		hibernateSession = hibernateSessionFactory.openSession();
		hibernateCriteria = hibernateSession.createCriteria(AuthModel.class);
		hibernateCriteria.add(Restrictions.eq("accessToken", token));
		authModelList = hibernateCriteria.list();
		hibernateSession.close();
		Iterator iterator = authModelList.iterator();
		if(iterator.hasNext()){
			System.out.println("[auth]ok");
			return (AuthModel)iterator.next();
		}else{
			System.out.println("[auth]null");
			return null;
		}
	}
	
	public MemberModel getMemberModel(String token){
		System.out.println("[auth]getMemberModel " + token);
		AuthModel authModel = hasLoginGetModel(token);
		if(authModel==null){
			return null;
		}else{
			List<MemberModel> memModelList;
			hibernateSession = hibernateSessionFactory.openSession();
			hibernateCriteria = hibernateSession.createCriteria(MemberModel.class);
			hibernateCriteria.add(Restrictions.eq("userID", authModel.getUserID()));
			memModelList = hibernateCriteria.list();
			hibernateSession.close();
			Iterator iterator = memModelList.iterator();
			if(iterator.hasNext()){
				System.out.println("[auth]ok");
				return (MemberModel)iterator.next();
			}else{
				System.out.println("[auth]null");
				return null;
			}
		}
	}
	
	public String getToken(String text){
		return GlobalFunction.encrypt(text).toLowerCase();
	}
	
	@ExceptionHandler(NullAccountException.class)
	@ResponseBody
	@ResponseStatus(value = HttpStatus.NOT_FOUND)
	public void handleNullAccountException(NullAccountException e) {}
	
	@ExceptionHandler(ErrorPasswordException.class)
	@ResponseBody
	@ResponseStatus(value = HttpStatus.FORBIDDEN)
	public void handleErrorPasswordException(ErrorPasswordException e) {}
	
	@ExceptionHandler(ErrorUserIDException.class)
	@ResponseBody
	@ResponseStatus(value = HttpStatus.BAD_REQUEST)
	public void handleErrorUserIDException(ErrorUserIDException e) {}
	
	@ExceptionHandler(ErrorUserNameException.class)
	@ResponseBody
	@ResponseStatus(value = HttpStatus.BAD_GATEWAY)
	public void handleErrorUserNameException(ErrorUserNameException e) {}
	
	@ExceptionHandler(ErrorUserEmailException.class)
	@ResponseBody
	@ResponseStatus(value = HttpStatus.SERVICE_UNAVAILABLE)
	public void handleErrorUserEmailException(ErrorUserEmailException e) {}
	
	@ExceptionHandler(NullPasswordException.class)
	@ResponseBody
	@ResponseStatus(value = HttpStatus.NOT_IMPLEMENTED)
	public void handleNullPasswordException(NullPasswordException e) {}
	
	@ExceptionHandler(AccountAlreadyExistException.class)
	@ResponseBody
	@ResponseStatus(value = HttpStatus.GATEWAY_TIMEOUT)
	public void handleAccountAlreadyExistException(AccountAlreadyExistException e) {}
	
	@ExceptionHandler(NotLoginException.class)
	@ResponseBody
	@ResponseStatus(value = HttpStatus.HTTP_VERSION_NOT_SUPPORTED)
	public void handleAccountAlreadyExistException(NotLoginException e) {}
	
	@ExceptionHandler(NeedConfirmEmailException.class)
	@ResponseBody
	@ResponseStatus(value = HttpStatus.METHOD_NOT_ALLOWED)
	public void handleAccountAlreadyExistException(NeedConfirmEmailException e) {}
	
	@ExceptionHandler(Exception.class)
	@ResponseBody
	@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
	public void handleException(Exception e) {}
}
