package restfulWS.book.viewmodel;

public class BookQueryModel {

	private int id;
	private String bookName;
	private String bookAbstract;
	private String bookCover;
	private String bookContent;
	private int bookCost;
	private String bookCreator;
	
	private String token;
	private int optBookName;
	private int optBookAbstract;
	private int optBookCreator;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getBookName() {
		return bookName;
	}
	public void setBookName(String bookName) {
		this.bookName = bookName;
	}
	public String getBookAbstract() {
		return bookAbstract;
	}
	public void setBookAbstract(String bookAbstract) {
		this.bookAbstract = bookAbstract;
	}
	public String getBookCover() {
		return bookCover;
	}
	public void setBookCover(String bookCover) {
		this.bookCover = bookCover;
	}
	public String getBookContent() {
		return bookContent;
	}
	public void setBookContent(String bookContent) {
		this.bookContent = bookContent;
	}
	public int getBookCost() {
		return bookCost;
	}
	public void setBookCost(int bookCost) {
		this.bookCost = bookCost;
	}
	public String getBookCreator() {
		return bookCreator;
	}
	public void setBookCreator(String bookCreator) {
		this.bookCreator = bookCreator;
	}
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public int getOptBookName() {
		return optBookName;
	}
	public void setOptBookName(int optBookName) {
		this.optBookName = optBookName;
	}
	public int getOptBookAbstract() {
		return optBookAbstract;
	}
	public void setOptBookAbstract(int optBookAbstract) {
		this.optBookAbstract = optBookAbstract;
	}
	public int getOptBookCreator() {
		return optBookCreator;
	}
	public void setOptBookCreator(int optBookCreator) {
		this.optBookCreator = optBookCreator;
	}
}
