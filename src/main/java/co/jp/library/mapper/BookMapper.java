package co.jp.library.mapper;

import co.jp.library.entity.Book;

import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface BookMapper {
	
    List<Book> getAllBooks();
    
    List<Book> getBooks();
    
    List<Book> getEBooks();
           
    List<Book> getBooksByCategory(String categoryName);
   
    void addBook(Book book);

    // New method to retrieve book by registrationId with PDF data
   // Book getBookWithPdfDataByRegistrationId(String registrationId);

    void updateBook(Book updatedBook);
    
    /**
     * @param registrationId
     * @return
     */
    Book getBookByRegistrationId(String registrationId);
    byte[] getPdfDataByRegistrationId(String registrationId);

    List<Book> searchBooks(String bookCategory, String author, String registrationNumber, String bookName);

    void updateBookStatusByRegistrationId(String registrationId);

    void updateEbookStatusByRegistrationId(String registrationId);
	
}
