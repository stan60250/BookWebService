package restfulWS.book;

import java.util.List;

import restfulWS.book.model.BookModel;

public interface Book {
	void create(BookModel bookModel) throws Exception;
	void update(int id, BookModel bookModel) throws Exception;
	void delete(int id) throws Exception;
	BookModel find(int id) throws Exception;
	List<BookModel> list() throws Exception;
	BookModel findByName(String bookName) throws Exception;
}
