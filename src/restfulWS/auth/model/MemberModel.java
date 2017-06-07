package restfulWS.auth.model;

public class MemberModel{
	private int id;
	private String userID;
	private String userPWHash;
	private String userName;
	private String userEmail;
	private int enable;

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
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
	
	public int getEnable() {
		return enable;
	}
	public void setEnable(int enable) {
		this.enable = enable;
	}
	
}