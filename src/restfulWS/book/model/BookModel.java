package restfulWS.book.model;

public class BookModel{
	
	private int id;
	private String bookName;
	private String bookAbstract;
	private String bookCover;
	private String bookContent;
	private int bookCost;
	private String bookCreator;
	
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
	
}