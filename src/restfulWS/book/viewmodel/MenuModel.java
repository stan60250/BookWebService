package restfulWS.book.viewmodel;

public class MenuModel {
	
	private String url;
	private String text;
	
	//Constructor!!!
	public MenuModel(String text, String url){
		this.text = text;
		this.url = url;
	}
	
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	
}
