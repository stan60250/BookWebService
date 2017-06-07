package controller.webservice;

import java.util.ArrayList;
import java.util.Date;
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

import restfulWS.auth.GlobalFunction;
import restfulWS.auth.exceptions.AccountAlreadyExistException;
import restfulWS.auth.exceptions.ErrorPasswordException;
import restfulWS.auth.exceptions.ErrorUserEmailException;
import restfulWS.auth.exceptions.ErrorUserIDException;
import restfulWS.auth.exceptions.ErrorUserNameException;
import restfulWS.auth.exceptions.NeedConfirmEmailException;
import restfulWS.auth.exceptions.NotLoginException;
import restfulWS.auth.exceptions.NullAccountException;
import restfulWS.auth.exceptions.NullPasswordException;
import restfulWS.auth.model.AuthModel;
import restfulWS.auth.model.MemberModel;
import restfulWS.book.model.UserModel;
import restfulWS.book.viewmodel.MessageModel;
import restfulWS.book.viewmodel.UserLoginModel;

@Controller("controller.webservice.AuthController")
@RequestMapping("/webservice")
public class AuthController {
	
	private Session hibernateSession;
	private SessionFactory hibernateSessionFactory;
	private Criteria hibernateCriteria;
	private ServiceRegistry serviceRegistry;
	
	public AuthController() throws Exception {
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
	String auth_service =(String)context.getBean("auth_service");
	
	//============================================帳號建立LINK
	@RequestMapping(value = "/userCreate", method = RequestMethod.GET)
	public ModelAndView userCreate() {
		String userCreatePage = (String) context.getBean("userCreate");
		return new ModelAndView(userCreatePage);
	}
	
	//============================================帳號建立動作
	@RequestMapping(value = "/doUserCreate", method = RequestMethod.POST)
	public ModelAndView doUserCreate(@Valid MemberModel userModel, BindingResult bindingResult){		
		
		List<FieldError> authErrors = new ArrayList<FieldError>();
		
		if (bindingResult.hasErrors())  {
			return new ModelAndView(ERROR, "ErrorModel", bindingResult.getFieldErrors());
		}
		
		userModel.setEnable(0);
		
		try {
			try{
				restTemplate.postForObject(auth_service + "/create", userModel, MemberModel.class);
				
				//在BK_users內登記
				try{
					List<UserModel> userModelList;
					hibernateSession = hibernateSessionFactory.openSession();
					hibernateCriteria = hibernateSession.createCriteria(AuthModel.class);
					hibernateCriteria.add(Restrictions.eq("userID", userModel.getUserID()));
					userModelList = hibernateCriteria.list();
					Iterator iterator = userModelList.iterator();
					
					UserModel bookUserModel = new UserModel();
					
					Transaction tx= hibernateSession.beginTransaction();
					if(!iterator.hasNext()){
						bookUserModel.setUserID(userModel.getUserID());
						bookUserModel.setPoint(100);
						hibernateSession.save(bookUserModel);
					}
					tx.commit();
					hibernateSession.close();
				}catch(Exception e){
					System.out.println("[auth]INSERT BK_users error");
					System.out.println(e.getMessage());
				}
			}catch (RestClientException restClientException){
				String statusCode = restClientException.getMessage();
				
				if(statusCode.equals("400 Bad Request")){
					throw new ErrorUserIDException();
				}else if(statusCode.equals("502 Bad Gateway")){
					throw new ErrorUserNameException();
				}else if(statusCode.equals("503 Service Unavailable")){
					throw new ErrorUserEmailException();
				}else if(statusCode.equals("501 Not Implemented")){
					throw new NullPasswordException();
				}else if(statusCode.equals("504 Gateway Timeout")){
					throw new AccountAlreadyExistException();
				}else{
					throw restClientException;
				}
			}
		}catch (ErrorUserIDException e) {
			authErrors.add(new FieldError("AuthController", e.getMessage(), resource.getString(e.getMessage())));
			return new ModelAndView(ERROR, "ErrorModel", authErrors);
		}catch (ErrorUserNameException e) {
			authErrors.add(new FieldError("AuthController", e.getMessage(), resource.getString(e.getMessage())));
			return new ModelAndView(ERROR, "ErrorModel", authErrors);
		}catch (ErrorUserEmailException e) {
			authErrors.add(new FieldError("AuthController", e.getMessage(), resource.getString(e.getMessage())));
			return new ModelAndView(ERROR, "ErrorModel", authErrors);
		}catch (NullPasswordException e) {
			authErrors.add(new FieldError("AuthController", e.getMessage(), resource.getString(e.getMessage())));
			return new ModelAndView(ERROR, "ErrorModel", authErrors);
		}catch (AccountAlreadyExistException e) {
			authErrors.add(new FieldError("AuthController", e.getMessage(), resource.getString(e.getMessage())));
			return new ModelAndView(ERROR, "ErrorModel", authErrors);
		}catch (RestClientException e) {
			authErrors.add(new FieldError("AuthController", "error.http", resource.getString("error.http")+"<br>"+e.getMessage()));
			return new ModelAndView(ERROR, "ErrorModel", authErrors);
		}catch(Exception e){
			authErrors.add(new FieldError("AuthController", "error.database", resource.getString("error.database")+"<br>"+e.getMessage()));
			return new ModelAndView(ERROR, "ErrorModel", authErrors);
		}
		
		MessageModel messageModel = (MessageModel)context.getBean("messageModel");
		messageModel.setMessage("帳號建立成功!&nbsp;請記得去驗證E-mail以啟用帳號!<br>5 秒後將自動回到首頁!");
		messageModel.setUrl("doListPublic");
		messageModel.setSec("5");
		
		String SUCCESS = (String) context.getBean("userCreateSUCCESS");
		return new ModelAndView(SUCCESS, "MessageModel", messageModel);
	}
	
	//============================================帳號登入LINK
	@RequestMapping(value = "/userLogin", method = RequestMethod.GET)
	public ModelAndView userLogin() {
		String userLoginPage = (String) context.getBean("userLogin");
		return new ModelAndView(userLoginPage);
	}
	
	//============================================帳號登入動作
	@RequestMapping(value = "/doUserLogin", method = RequestMethod.POST)
	public ModelAndView doUserLogin(@Valid UserLoginModel userLoginModel, BindingResult bindingResult){

		List<FieldError> authErrors = new ArrayList<FieldError>();
		
		if (bindingResult.hasErrors())  {
			return new ModelAndView(ERROR, "ErrorModel", bindingResult.getFieldErrors());
		}
		
		MemberModel userModel = new MemberModel();
		userModel.setUserID(userLoginModel.getUserID());
		userModel.setUserPWHash(userLoginModel.getUserPW());
		
		try {
			try{
				restTemplate.postForObject(auth_service + "/login", userModel, MemberModel.class);
			}catch (RestClientException restClientException){
				String statusCode = restClientException.getMessage();
				
				if(statusCode.equals("404 Not Found")){
					throw new NullAccountException();
				}else if(statusCode.equals("403 Forbidden")){
					throw new ErrorPasswordException();
				}else if(statusCode.equals("405 Method Not Allowed")){ 
					throw new NeedConfirmEmailException();
				}else{
					throw restClientException;
				}
			}
		}catch (NullAccountException e) {
			authErrors.add(new FieldError("AuthController", e.getMessage(), resource.getString(e.getMessage())));
			return new ModelAndView(ERROR, "ErrorModel", authErrors);
		}catch (ErrorPasswordException e) {
			authErrors.add(new FieldError("AuthController", e.getMessage(), resource.getString(e.getMessage())));
			return new ModelAndView(ERROR, "ErrorModel", authErrors);
		}catch (NeedConfirmEmailException e) {
			authErrors.add(new FieldError("AuthController", e.getMessage(), resource.getString(e.getMessage())));
			return new ModelAndView(ERROR, "ErrorModel", authErrors);
		}catch (RestClientException e) {
			authErrors.add(new FieldError("AuthController", "error.http", resource.getString("error.http")+"<br>"+e.getMessage()));
			return new ModelAndView(ERROR, "ErrorModel", authErrors);
		}catch(Exception e){
			authErrors.add(new FieldError("AuthController", "error.database", resource.getString("error.database")+"<br>"+e.getMessage()));
			return new ModelAndView(ERROR, "ErrorModel", authErrors);
		}
		
		MessageModel messageModel = (MessageModel)context.getBean("messageModel");
		messageModel.setMessage("帳號登入成功!<br>5 秒後將自動回到首頁!");
		messageModel.setUrl("doListPublicToken?token=" + getToken(userModel.getUserID()));
		messageModel.setSec("5");
		
		String SUCCESS = (String) context.getBean("userLoginSUCCESS");
		return new ModelAndView(SUCCESS, "MessageModel", messageModel);
	}
	
	//============================================帳號資料修改前置作業
	@RequestMapping(value = "/userModify", method = RequestMethod.GET)
	public ModelAndView userModify(@RequestParam(value="token") String token) throws Exception  {
		System.out.println("[book]userModify");
		
		List<FieldError> authErrors = new ArrayList<FieldError>();
		MemberModel memModel;
		
		try {
			//檢查token是否有效
			memModel = getUserModel(token);
			if(memModel==null) throw new NotLoginException();
			
		}catch (NotLoginException e) {
			authErrors.add(new FieldError("AuthController", e.getMessage(), resource.getString(e.getMessage())));
			return new ModelAndView(ERROR, "ErrorModel", authErrors);
		}catch(Exception e){
			authErrors.add(new FieldError("AuthController", "error.database", resource.getString("error.database")+"<br>"+e.getMessage()));
			return new ModelAndView(ERROR, "ErrorModel", authErrors);
		}
		
		MemberModel viewMemModel = (MemberModel)context.getBean("memberModel");
		viewMemModel.setUserID(memModel.getUserID());
		viewMemModel.setUserName(memModel.getUserName());
		viewMemModel.setUserEmail(memModel.getUserEmail());
		//借用UserPWHash傳遞token, 暫時先這樣
		viewMemModel.setUserPWHash(token);
		
		String userModifyPage = (String) context.getBean("userModify");
		return new ModelAndView(userModifyPage, "MemberModel", viewMemModel);
		
	}
		
	//============================================帳號資料修改動作
	@RequestMapping(value = "/doUserModify", method = RequestMethod.POST)
	public ModelAndView doUserModify(@RequestParam(value="token") String token, @Valid MemberModel userModel, BindingResult bindingResult){		
		
		List<FieldError> authErrors = new ArrayList<FieldError>();
		
		if (bindingResult.hasErrors())  {
			return new ModelAndView(ERROR, "ErrorModel", bindingResult.getFieldErrors());
		}
		
		try {
			try{
				restTemplate.put(auth_service + "/" + token, userModel, MemberModel.class);

			}catch (RestClientException restClientException){
				String statusCode = restClientException.getMessage();
				
				if(statusCode.equals("400 Bad Request")){
					throw new ErrorUserIDException();
				}else if(statusCode.equals("502 Bad Gateway")){
					throw new ErrorUserNameException();
				}else if(statusCode.equals("503 Service Unavailable")){
					throw new ErrorUserEmailException();
				}else if(statusCode.equals("501 Not Implemented")){
					throw new NullPasswordException();
				}else if(statusCode.equals("504 Gateway Timeout")){
					throw new AccountAlreadyExistException();
				}else{
					throw restClientException;
				}
			}
		}catch (ErrorUserIDException e) {
			authErrors.add(new FieldError("AuthController", e.getMessage(), resource.getString(e.getMessage())));
			return new ModelAndView(ERROR, "ErrorModel", authErrors);
		}catch (ErrorUserNameException e) {
			authErrors.add(new FieldError("AuthController", e.getMessage(), resource.getString(e.getMessage())));
			return new ModelAndView(ERROR, "ErrorModel", authErrors);
		}catch (ErrorUserEmailException e) {
			authErrors.add(new FieldError("AuthController", e.getMessage(), resource.getString(e.getMessage())));
			return new ModelAndView(ERROR, "ErrorModel", authErrors);
		}catch (NullPasswordException e) {
			authErrors.add(new FieldError("AuthController", e.getMessage(), resource.getString(e.getMessage())));
			return new ModelAndView(ERROR, "ErrorModel", authErrors);
		}catch (AccountAlreadyExistException e) {
			authErrors.add(new FieldError("AuthController", e.getMessage(), resource.getString(e.getMessage())));
			return new ModelAndView(ERROR, "ErrorModel", authErrors);
		}catch (RestClientException e) {
			authErrors.add(new FieldError("AuthController", "error.http", resource.getString("error.http")+"<br>"+e.getMessage()));
			return new ModelAndView(ERROR, "ErrorModel", authErrors);
		}catch(Exception e){
			authErrors.add(new FieldError("AuthController", "error.database", resource.getString("error.database")+"<br>"+e.getMessage()));
			return new ModelAndView(ERROR, "ErrorModel", authErrors);
		}
		
		MessageModel messageModel = (MessageModel)context.getBean("messageModel");
		messageModel.setMessage("帳號資料修改成功!&nbsp;請記得去驗證E-mail以啟用帳號!<br>5 秒後將自動回到首頁!");
		messageModel.setUrl("doListPublic");
		messageModel.setSec("5");
		
		String SUCCESS = (String) context.getBean("userModifySUCCESS");
		return new ModelAndView(SUCCESS, "MessageModel", messageModel);
	}
	
	//============================================帳號登出動作
	@RequestMapping(value = "/userLogout", method = RequestMethod.GET)
	public ModelAndView doUserLogout(@RequestParam(value="token") String token) throws Exception {
		
		List<FieldError> authErrors = new ArrayList<FieldError>();
		
		try {
			try{
				restTemplate.delete(auth_service + "/" + token);
			}catch (RestClientException restClientException){
				String statusCode = restClientException.getMessage();
				
				if(statusCode.equals("404 Not Found")){
					throw new NullAccountException();
				}else if(statusCode.equals("505 HTTP Version Not Supported")){
					throw new NotLoginException();
				}else{
					throw restClientException;
				}
			}
		}catch (NullAccountException e) {
			authErrors.add(new FieldError("AuthController", e.getMessage(), resource.getString(e.getMessage())));
			return new ModelAndView(ERROR, "ErrorModel", authErrors);
		}catch (NotLoginException e) {
			authErrors.add(new FieldError("AuthController", e.getMessage(), resource.getString(e.getMessage())));
			return new ModelAndView(ERROR, "ErrorModel", authErrors);
		}catch (RestClientException e) {
			authErrors.add(new FieldError("AuthController", "error.http", resource.getString("error.http")+"<br>"+e.getMessage()));
			return new ModelAndView(ERROR, "ErrorModel", authErrors);
		}catch(Exception e){
			authErrors.add(new FieldError("AuthController", "error.database", resource.getString("error.database")+"<br>"+e.getMessage()));
			return new ModelAndView(ERROR, "ErrorModel", authErrors);
		}
		
		MessageModel messageModel = (MessageModel)context.getBean("messageModel");
		messageModel.setMessage("帳號登出成功!<br>5 秒後將自動回到首頁!");
		messageModel.setUrl("doListPublic");
		messageModel.setSec("5");
		
		String SUCCESS = (String) context.getBean("userLogoutSUCCESS");
		return new ModelAndView(SUCCESS, "MessageModel", messageModel);
	}
	
	//============================================帳號驗證Email
	@RequestMapping(value = "/emailConfirm", method = RequestMethod.GET)
	public ModelAndView emailConfirm(@RequestParam(value="token") String token) throws Exception {
		
		List<FieldError> authErrors = new ArrayList<FieldError>();
		
		try {
			try{
				MemberModel userModel = restTemplate.getForObject(auth_service + "/" + token, MemberModel.class) ;
				//if(userModel==null)throw new NullAccountException();
			}catch (RestClientException restClientException){
				String statusCode = restClientException.getMessage();
				
				if(statusCode.equals("404 Not Found")){
					throw new NullAccountException();
				}else if(statusCode.equals("505 HTTP Version Not Supported")){ 
					throw new NotLoginException();
				}else{
					throw restClientException;
				}
			}
		}catch (NullAccountException e) {
			authErrors.add(new FieldError("AuthController", e.getMessage(), resource.getString(e.getMessage())));
			return new ModelAndView(ERROR, "ErrorModel", authErrors);
		}catch (NotLoginException e) {
			authErrors.add(new FieldError("AuthController", e.getMessage(), resource.getString(e.getMessage())));
			return new ModelAndView(ERROR, "ErrorModel", authErrors);
		}catch (RestClientException e) {
			authErrors.add(new FieldError("AuthController", "error.http", resource.getString("error.http")+"<br>"+e.getMessage()));
			return new ModelAndView(ERROR, "ErrorModel", authErrors);
		}catch(Exception e){
			authErrors.add(new FieldError("AuthController", "error.database", resource.getString("error.database")+"<br>"+e.getMessage()));
			return new ModelAndView(ERROR, "ErrorModel", authErrors);
		}
		
		MessageModel messageModel = (MessageModel)context.getBean("messageModel");
		messageModel.setMessage("郵件認證成功!<br>5 秒後將自動回到首頁!");
		messageModel.setUrl("doListPublic");
		messageModel.setSec("5");
		
		String SUCCESS = (String) context.getBean("emailConfirmSUCCESS");
		return new ModelAndView(SUCCESS, "MessageModel", messageModel);
	}
	
	public String getToken(String text){
		return GlobalFunction.encrypt(text).toLowerCase();
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
