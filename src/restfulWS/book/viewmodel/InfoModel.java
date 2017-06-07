package restfulWS.book.viewmodel;

public class InfoModel {
	private String userID;
	private String userPWHash;
	private String userName;
	private String userEmail;
	private String userToken;
	private String userPoint;
	
	
	/*public InfoModel(String userID, String userPWHash, String userName, String userEmail, String userToken, String userPoint){
		this.userID = userID;
		this.userPWHash = userPWHash;
		this.userName = userName;
		this.userEmail = userEmail;
		this.userToken = userToken;
		this.userPoint = userPoint;
	}*/

	public String getUserID() {
		return userID;
	}
	public void setUserID(String userID) {
		this.userID = userID;
	}
	public String getUserPWHash() {
		return userPWHash;
	}
	public void setUserPWHash(String userPWHash) {
		this.userPWHash = userPWHash;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getUserEmail() {
		return userEmail;
	}
	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}
	public String getUserToken() {
		return userToken;
	}
	public void setUserToken(String userToken) {
		this.userToken = userToken;
	}
	public String getUserPoint() {
		return userPoint;
	}
	public void setUserPoint(String userPoint) {
		this.userPoint = userPoint;
	}
	
}
