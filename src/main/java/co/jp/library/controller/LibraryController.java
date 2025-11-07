package co.jp.library.controller;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import co.jp.library.entity.Book;
import co.jp.library.entity.Category;
import co.jp.library.entity.User;
import co.jp.library.service.LibraryService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
public class LibraryController {

	@Autowired
	private LibraryService libraryService;

	@GetMapping("/")
	public String index(Model model) {
		List<Book> books = libraryService.getAllBooks();

		model.addAttribute("books", books);
		model.addAttribute("categories", libraryService.getAllCategories());
		return "index";
	}

	@GetMapping("/books")
	public String getBooks(Model model) {
		List<Book> Books = libraryService.getBooks();
		model.addAttribute("books", Books);
		model.addAttribute("categories", libraryService.getAllCategories());
		return "index";
	}

	@GetMapping("/ebooks")
	public String getEBooks(Model model) {
		List<Book> eBooks = libraryService.getEBooks();
		model.addAttribute("books", eBooks);
		model.addAttribute("categories", libraryService.getAllCategories());
		return "index";
	}

	@GetMapping("/search")
	public String searchBooks(@RequestParam(required = false) String bookCategory,
			@RequestParam(required = false) String author, @RequestParam(required = false) String registrationNumber,
			@RequestParam(required = false) String bookName, Model model) {

				
		List<Book> matchingBooks = libraryService.searchBooks(bookCategory, author, registrationNumber, bookName);
		// Set isEbook for each book based on bookType
    for (Book book : matchingBooks) {
        boolean isEbook = "EBook".equalsIgnoreCase(book.getBookType());
        book.setIsEbook(isEbook);
    }
		model.addAttribute("books", matchingBooks);
		model.addAttribute("categories", libraryService.getAllCategories());
		return "index";
	}

	@GetMapping("/category/{categoryName}")
public String getBooksByCategory(@PathVariable String categoryName, Model model) {
    List<Book> booksInCategory = libraryService.getBooksByCategory(categoryName);
    
    // Set isEbook for each book based on bookType
    for (Book book : booksInCategory) {
        boolean isEbook = "EBook".equalsIgnoreCase(book.getBookType());
        book.setIsEbook(isEbook);
    }
    
    model.addAttribute("books", booksInCategory);
    model.addAttribute("categories", libraryService.getAllCategories()); model.addAttribute("categories", libraryService.getAllCategories());
    
    return "index";
}


	@GetMapping("/add")
	public String showAddBookForm(Model model) {
		model.addAttribute("books", new Book());
		List<Category> categories = libraryService.getAllCategories();
		model.addAttribute("categories", categories);
		return "addBook";
	}

	@PostMapping("/book/add")
	
	public String addBook(Model model, @ModelAttribute("books") Book book,
			@RequestParam("pdfFile") MultipartFile pdfFile, @RequestParam("bookType") String bookType)
			throws IOException {

		book.setBookType(bookType);

		if ("ebook".equals(bookType)) {
			if (pdfFile != null && !pdfFile.isEmpty()) {
				String fileName = pdfFile.getOriginalFilename();
				book.setFileName(fileName);

				libraryService.addBook(book, pdfFile);
			} else {
				System.out.println("File not uploaded");
			}
		} else {
			libraryService.addBook(book, pdfFile);
		}

		List<Category> categories = libraryService.getAllCategories();
		model.addAttribute("categories", categories);

		List<Book> books = libraryService.getAllBooks();
		model.addAttribute("books", books);

		return "index";
	}

	@GetMapping("/category")
	public String AddCategory(Model model) {

		// System.out.println("Number of categories: " + categories.size()); //
		// Debugging line
		model.addAttribute("categories", new Category());
		model.addAttribute("books", new Book());
		return "addCategory";
	}

	@PostMapping("/category/add")
	public String AddCategoryConfirm(Model model, @ModelAttribute("categories") Category category,
			@RequestParam("categoryId") Long categoryId, @RequestParam("categoryName") String categoryName) {

		libraryService.addCategory(category);
		List<Category> categories = libraryService.getAllCategories();
		model.addAttribute("categories", categories);
		List<Book> books = libraryService.getAllBooks();
		model.addAttribute("books", books);
		return "index";
	}

	@GetMapping("/register")
	public String showRegistrationForm(Model model) {
		
		User user = new User();
		model.addAttribute("user", user);
		return "register";
	}

	@PostMapping("/registerConfirm")
	public String registerAccount(@Valid @ModelAttribute("user") User user, BindingResult result, Model model) {
		if (result.hasErrors()) {

			return "register";
		}
		libraryService.registerUser(user);
		List<Category> categories = libraryService.getAllCategories();
		model.addAttribute("categories", categories);
		List<Book> books = libraryService.getAllBooks();
		model.addAttribute("books", books);

		return "index";
	}

	@GetMapping("/login")
	public String showLoginForm(Model model) {

		return "login";
	}
@GetMapping("/logout")
	public String showLogoutForm(Model model, HttpSession session) {
		session.removeAttribute("login");
		return "redirect:/";
	}

	@PostMapping("/loginSuccess")
	public String loginAccount(@RequestParam String username, @RequestParam String password, Model model, HttpSession session) {
		boolean loginSuccessful = libraryService.loginUser(username, password);

		if (loginSuccessful) {
			session.setAttribute("login", "True");
			List<Category> categories = libraryService.getAllCategories();
			model.addAttribute("categories", categories);
			List<Book> books = libraryService.getAllBooks();
			model.addAttribute("books", books);

			return "index";
		} else {
			model.addAttribute("loginError", "Invalid credentials. Please try again.");
			return "login";
		}
	}

	@GetMapping("/update/{registrationId}")
	public String updateBook(@PathVariable String registrationId, Model model) {

		Book book = libraryService.getBookByRegistrationId(registrationId);
		List<Category> categories = libraryService.getAllCategories();
		model.addAttribute("categories", categories);
		model.addAttribute("book", book);
		return "updateBook";
	}

	@PostMapping("/updateConfirm/{registrationId}")
	public String updateBookConfirm(Model model, @ModelAttribute Book book, @PathVariable String registrationId) {
		book.setRegistrationId(registrationId);
		libraryService.updateBook(book);
		List<Book> books = libraryService.getAllBooks();
		model.addAttribute("books", books);
		model.addAttribute("categories", libraryService.getAllCategories());
		return "index";
	}
	
	 /**
	 * @param model
	 * @param registrationId
	 * @param session
	 * @return
	 */
	@GetMapping("/lend/{registrationId}")
    public ResponseEntity<Void> lendBook(Model model, @PathVariable String registrationId, HttpSession session) {
        String loginCheck = (String) session.getAttribute("login");

        if (loginCheck != null) {
            Book book = libraryService.getBookByRegistrationId(registrationId);

            if ("Book".equalsIgnoreCase(book.getBookType())) {
                libraryService.updateBookStatus(registrationId);
                return ResponseEntity.ok().build();
            } else {
                return ResponseEntity.notFound().build();
            }
        } else {
            // User is not logged in, redirect to the login page
            return ResponseEntity.status(HttpStatus.FOUND)
                .header("Location", "/login") // Adjust the login URL as needed
                .build();
        }
    }


@GetMapping("/download/{registrationId}")
public void downloadPdf(@PathVariable String registrationId, HttpServletResponse response,HttpSession session ) throws IOException {

	String loginCheck=(String) session.getAttribute("login");

	if(loginCheck!=null){
    
    byte[] pdfData = libraryService.getPdfDataByRegistrationId(registrationId);

    if (pdfData != null) {
        // Set the response content type to PDF
        response.setContentType("application/pdf");

        // Provide a suggested filename for the downloaded PDF
        response.setHeader("Content-Disposition", "attachment; filename=" + registrationId + ".pdf");

        // Write the PDF data to the response output stream
        try (OutputStream outStream = response.getOutputStream()) {
            outStream.write(pdfData);
        } catch (IOException e) {
            // Handle any exceptions that might occur while writing the response
            e.printStackTrace();
        }
    } else {
        // Handle the case where the PDF data was not found (e.g., return a 404 response)
        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
    }

	}else {
        
        response.sendRedirect("/login"); 
    }
}

}
