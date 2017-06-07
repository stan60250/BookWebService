package restfulWS.mail;

public interface MailAccount {
	// mail server
	public static final String host = "smtp.gmail.com";
	public static final String port = "465";

	public static final String account = "lulu04018@gmail.com";  //Gmail 帳號
	public static final String password = "ns%zSyfc$@sJ8ZrA"; //Gmail 密碼
	
	// mail
	public static final String sender = "lulu04018@gmail.com"; //Gmail 帳號(寄件者)
	public static final String reciver = "stan60250@gmail.com"; //Gmail 帳號(收件者)
	public static final String subject = "Hello Mail"; //訊息
}
