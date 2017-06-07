package restfulWS.book.viewmodel;

import java.util.List;

import restfulWS.book.model.BookModel;

public class BookListModel {
	private String token;
	
	private List<MenuModel> menuListModel;
	private List<BookModel> bookListModel;
	
	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}
	
	public List<BookModel> getBookListModel() {
		return bookListModel;
	}

	public void setBookListModel(List<BookModel> bookListModel) {
		this.bookListModel = bookListModel;
	}
	
	public List<MenuModel> getMenuListModel() {
		return menuListModel;
	}

	public void setMenuListModel(List<MenuModel> menuListModel) {
		this.menuListModel = menuListModel;
	}
}
