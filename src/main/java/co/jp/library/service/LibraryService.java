package co.jp.library.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;


import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import co.jp.library.entity.Book;
import co.jp.library.entity.Category;
import co.jp.library.entity.User;
import co.jp.library.mapper.BookCategoryMapper;
import co.jp.library.mapper.BookMapper;

import co.jp.library.mapper.UserMapper;

@Service
public class LibraryService {

	private final BookCategoryMapper bookCategoryMapper;
	private final BookMapper bookMapper;
	private final UserMapper userMapper;
	

	public LibraryService(BookCategoryMapper bookCategoryMapper, BookMapper bookMapper, UserMapper userMapper) {
		this.bookCategoryMapper = bookCategoryMapper;
		this.bookMapper = bookMapper;
		this.userMapper = userMapper;
		
	}

	public void addCategory(Category category) {
		bookCategoryMapper.addCategory(category);
	}

	// public void setBookPdfData(Book book, byte[] pdfData) {
	// book.setPdfData(pdfData);
	// }

	public Category getCategoryById(Long categoryId) {
		return bookCategoryMapper.getCategoryById(categoryId);
	}

	public List<Category> getAllCategories() {
		return bookCategoryMapper.getAllCategories();
	}

	public void addBook(Book book, MultipartFile pdfFile) throws IOException {
		if (pdfFile != null && !pdfFile.isEmpty()) {
			byte[] pdfData = pdfFile.getBytes();
			book.setPdfData(pdfData);
			// Save the file to src/main/resources/static/pdf
String fileName = pdfFile.getOriginalFilename();
java.nio.file.Path filePath = Paths.get("src/main/resources/static/pdf/", fileName);
Files.write(filePath, pdfData);

// Set the file path in your Book entity
book.setFilePath("/pdf/" + fileName); // The "/pdf/" prefix is the URL path
		}

		boolean isEbook = "EBook".equalsIgnoreCase(book.getBookType());
		book.setIsEbook(isEbook);
		if ("Book".equalsIgnoreCase(book.getBookType())) {

			bookMapper.addBook(book);
			book.setIsEbook(false);
		} else if ("EBook".equalsIgnoreCase(book.getBookType())) {

			bookMapper.addBook(book);
			book.setIsEbook(true);
		} else {

			throw new IllegalArgumentException("Invalid book type: " + book.getBookType());
		}
	}

	public Book getBookByRegistrationId(String registrationId) {
		return bookMapper.getBookByRegistrationId(registrationId);
	}

	public List<Book> getAllBooks() {
		List<Book> books = bookMapper.getAllBooks(); // Retrieve all books from the database

		// Set isEbook for each book based on bookType
		for (Book book : books) {
			boolean isEbook = "EBook".equalsIgnoreCase(book.getBookType());
			book.setIsEbook(isEbook);
		}

		return books;
	}

	public List<Book> getBooks() {
		List<Book> books = bookMapper.getBooks();

		// Set isEbook for each book based on bookType
		for (Book book : books) {
			boolean isEbook = "EBook".equalsIgnoreCase(book.getBookType());
			book.setIsEbook(isEbook);
		}

		return books;
	}

	public List<Book> getEBooks() {
		List<Book> books = bookMapper.getEBooks();

		// Set isEbook for each book based on bookType
		for (Book book : books) {
			boolean isEbook = "EBook".equalsIgnoreCase(book.getBookType());
			book.setIsEbook(isEbook);
		}

		return books;
	}

	public List<Book> getBooksByCategory(String categoryName) {
		return bookMapper.getBooksByCategory(categoryName);
	}

	public void updateBook(Book book) {
		bookMapper.updateBook(book);
	}

	public void registerUser(User user) {
		userMapper.insert(user);

	}

	public boolean loginUser(String username, String password) {

		User user = userMapper.findByUsername(username);
		if (user != null) {
			
			if (isPasswordValid(password, user.getPassword())) {
				
				return true;
			}
		}

		
		return false;
	}

	private boolean isPasswordValid(String providedPassword, String storedPassword) {
		
		return providedPassword.equals(storedPassword);
	}

	public List<Book> searchBooks(String bookCategory, String author, String registrationNumber, String bookName) {
	    
        return bookMapper.searchBooks(bookCategory, author, registrationNumber, bookName);
    }

	public void updateBookStatus(String registrationId) {
        
        bookMapper.updateBookStatusByRegistrationId(registrationId);
        
    }
	public void updateEbookStatus(String registrationId) {
        
        bookMapper.updateEbookStatusByRegistrationId(registrationId);

        
    }

    public byte[] getPdfDataByRegistrationId(String registrationId) {
        Book book = bookMapper.getBookByRegistrationId(registrationId);
        if (book != null) {
            return book.getPdfData();
        }
        return null; // Return null if PDF data is not found
    }
	
	
}
