package restfulWS.auth.model;

public class LogModel {
	private int id;
	private String userID;
	private String actionTime;
	private String userAgent;
	private String userAction;
	
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
	public String getActionTime() {
		return actionTime;
	}
	public void setActionTime(String actionTime) {
		this.actionTime = actionTime;
	}
	public String getUserAgent() {
		return userAgent;
	}
	public void setUserAgent(String userAgent) {
		this.userAgent = userAgent;
	}
	public String getUserAction() {
		return userAction;
	}
	public void setUserAction(String userAction) {
		this.userAction = userAction;
	}
	
}
