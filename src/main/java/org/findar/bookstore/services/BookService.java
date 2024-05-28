package org.findar.bookstore.services;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.findar.bookstore.dtos.*;
import org.findar.bookstore.dtos.enums.BookStatus;
import org.findar.bookstore.entities.Book;
import org.findar.bookstore.entities.User;
import org.findar.bookstore.exception.BadRequest;
import org.findar.bookstore.exception.NotFound;
import org.findar.bookstore.exception.UnathuorizedAccess;
import org.findar.bookstore.repositories.BookRepository;
import org.findar.bookstore.utils.Helper;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.data.domain.Page;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class BookService {
    private final BookRepository bookRepository;
    private final Helper helper;

    public GlobalResponse<?> addBook(AddBookDto book, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse)  {
        try {
            //Validate and extract user
            User user = validateAndExtractUser(httpServletRequest, httpServletResponse);
            Book newBook = new Book();

            //Attempt to add book to database
            BeanUtils.copyProperties(book, newBook);
            newBook.setAuthors(Arrays.asList(book.getAllAuthors().split(",")));
            newBook.setOwner(user);

            bookRepository.saveAndFlush(newBook);
            return getGlobalResponse(httpServletResponse, newBook,"Book added successfully");
        }
        catch (Exception e){
        return bookServiceError(httpServletResponse, e.getMessage(),"Could not add Book");
        }

    }

    public GlobalResponse<?> addBookContent(String id, MultipartFile file, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse)  {
        try {
            //Validate and extract user
            User user = validateAndExtractUser(httpServletRequest, httpServletResponse);
            Book newBook = bookRepository.findById(id).orElseThrow(()-> new NotFound("Book not found"));

            //Attempt to add book to database
            String[] filetype = Objects.requireNonNull(file.getOriginalFilename()).split("[.]");
            newBook.setFileFormat(filetype[filetype.length - 1].toUpperCase());
            newBook.setFileSize(file.getSize());
            newBook.setFileContent(file.getBytes());
            newBook.setUpdatedAt(LocalDateTime.now());
            bookRepository.saveAndFlush(newBook);
            return getGlobalResponse(httpServletResponse, newBook,"Book contents added successfully");
        }
        catch (Exception e){
            return bookServiceError(httpServletResponse, e.getMessage(),"Could not add Book contents");
        }

    }

    public GlobalResponse<?> editBookInfo(EditBookDto editBookDto, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        try {
            //Validate and extract user
            User user = validateAndExtractUser(httpServletRequest, httpServletResponse);
            //Verify that book exist
            Book bookInDb = bookRepository.findById(editBookDto.getId()).orElseThrow(() -> new NotFound("Book not found"));

            //Ensure that only the owner can edit the book details
            if (!bookInDb.getOwner().equals(user))
                throw new UnathuorizedAccess("Only book owner has the right to edit book");

            BeanUtils.copyProperties(editBookDto, bookInDb);
            bookInDb.setUpdatedAt(LocalDateTime.now());
            bookRepository.saveAndFlush(bookInDb);
            return getGlobalResponse(httpServletResponse, bookInDb,"Book edited successfully");
        }
        catch (Exception e){
            return bookServiceError(httpServletResponse, e.getMessage(),"Could not edit book's details");
        }
    }

    public GlobalResponse<?> editBookContent(String id,MultipartFile file, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        try {
            //Validate and extract user
            User user = validateAndExtractUser(httpServletRequest, httpServletResponse);
            //Verify that book exist
            Book bookInDb = bookRepository.findById(id).orElseThrow(() -> new NotFound("Book not found"));
            //Ensure that only the owner can edit the book contents
            if (!bookInDb.getOwner().equals(user))
                throw new UnathuorizedAccess("Only book owner has the right to edit book's content");
            String[] filetype = Objects.requireNonNull(file.getOriginalFilename()).split("[.]");
            bookInDb.setFileFormat(filetype[filetype.length - 1].toUpperCase());
            bookInDb.setFileSize(file.getSize());
            bookInDb.setFileContent(file.getBytes());
            bookInDb.setUpdatedAt(LocalDateTime.now());
            bookRepository.saveAndFlush(bookInDb);
            return getGlobalResponse(httpServletResponse, bookInDb,"Book edited successfully");
        }
        catch (Exception e){
            return bookServiceError(httpServletResponse, e.getMessage(), "Could not edit book's content");
        }
    }

    public GlobalResponse<?> deleteBook(EditBookContentDto deleteBookDto, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        try {
            //Validate and extract user
            User user = validateAndExtractUser(httpServletRequest, httpServletResponse);
            //Verify that book exist
            Book bookInDb = bookRepository.findById(deleteBookDto.getId()).orElseThrow(() -> new NotFound("Book not found"));
            //Ensure that only the owner can delete the book
            if (!bookInDb.getOwner().equals(user))
                throw new UnathuorizedAccess("Only book owner has the right to delete book");
            //delete book from database
            bookRepository.delete(bookInDb);
            return getGlobalResponse(httpServletResponse, bookInDb,"Book deleted successfully");
        }
        catch (Exception e){
            return bookServiceError(httpServletResponse, e.getMessage(),"Could not delete book");
        }
    }

    public ResponseEntity<byte[]> getBookContent(GetBookContentDto getBookContentDto, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        try {
            //Validate and extract user
            User user = validateAndExtractUser(httpServletRequest, httpServletResponse);
            Book book = bookRepository.findById(getBookContentDto.getBookId()).orElseThrow(()-> new NotFound("Book not found"));
            return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,"attachment; filename=\""+book.getTitle()+"."+book.getFileFormat().toLowerCase()+"\"").body(book.getFileContent());
        }
        catch (Exception e){
            e.printStackTrace();
        throw new BadRequest("Could not get book's content because "+e.getMessage());
        }
    }

    public GlobalResponse<?> borrowBook(GetBookContentDto borrowBookDto, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        try {
            //Validate and extract user
            User user = validateAndExtractUser(httpServletRequest, httpServletResponse);
            //Verify that book exist
            Book bookInDb = bookRepository.findById(borrowBookDto.getBookId()).orElseThrow(() -> new NotFound("Book not found"));
            bookInDb.setBookStatus(BookStatus.NOT_AVAILABLE);
            bookInDb.setLender(user);
            bookInDb.setBorrowCount(bookInDb.getBorrowCount()+1);
            bookInDb.setLastBorrowed(LocalDateTime.now());
            bookInDb.setUpdatedAt(LocalDateTime.now());
            bookRepository.saveAndFlush(bookInDb);
            return getGlobalResponse(httpServletResponse, bookInDb,"Book borrowed successfully");
        }
        catch (Exception e){
            return bookServiceError(httpServletResponse, e.getMessage(),"Could not borrow book");
        }
    }

    public GlobalResponse<?> returnBook(GetBookContentDto returnBookDto, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        try {
            //Validate and extract user
            User user = validateAndExtractUser(httpServletRequest, httpServletResponse);
            //Verify that book exist
            Book bookInDb = bookRepository.findById(returnBookDto.getBookId()).orElseThrow(() -> new NotFound("Book not found"));
            if (bookInDb.getLender() == null){
                throw new BadRequest("You are not currently a lender of this book");
            }
            if(!bookInDb.getLender().equals(user)){
                throw new BadRequest("You are not currently a lender of this book");
            }
            bookInDb.setBookStatus(BookStatus.AVAILABLE);
            bookInDb.setLender(null);
            bookInDb.setUpdatedAt(LocalDateTime.now());
            bookRepository.saveAndFlush(bookInDb);
            return getGlobalResponse(httpServletResponse, bookInDb,"Book returned successfully");
        }
        catch (Exception e){
            return bookServiceError(httpServletResponse, e.getMessage(),"Could not return book");
        }
    }

    public GlobalResponse<?> getAllbooksByUser(GetPaginatedDto getPaginatedDto,HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
    try {
        User user = validateAndExtractUser(httpServletRequest, httpServletResponse);
        List<Book> books = bookRepository.findAllByOwner(user);
        return getGlobalResponse(getPaginatedDto, httpServletResponse, books);
    }
    catch (Exception e){
        return bookServiceError(httpServletResponse, e.getMessage(),"Could not get  books");
    }
    }

    public GlobalResponse<?> getAllbooks(GetPaginatedDto getPaginatedDto,HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        try {
            validateAndExtractUser(httpServletRequest, httpServletResponse);
            List<Book> books = bookRepository.findAll();
            return getGlobalResponse(getPaginatedDto, httpServletResponse, books);
        }
        catch (Exception e){
            return bookServiceError(httpServletResponse, e.getMessage(),"Could not get  books");
        }
    }

    private GlobalResponse<?> getGlobalResponse(GetPaginatedDto getPaginatedDto, HttpServletResponse httpServletResponse, List<Book> books) {
        Page<ViewBookDto> allBooks = helper.getBookPagedResponse(getPaginatedDto,books);

        GlobalResponse<Page<ViewBookDto>> response = new GlobalResponse<>();
        response.setStatus(HttpStatus.ACCEPTED);
        response.setMessage("All books gotten successfully");
        response.setData(allBooks);
        httpServletResponse.setStatus(200);
        return response;
    }
    private GlobalResponse<?> getGlobalResponse(HttpServletResponse httpServletResponse, Book book,String message) {
        GlobalResponse<ViewBookDto> response = new GlobalResponse<>();
        response.setStatus(HttpStatus.ACCEPTED);
        response.setMessage(message);
        ViewBookDto viewBookDto = new ViewBookDto();
        BeanUtils.copyProperties(book,viewBookDto);
        response.setData(viewBookDto);
        httpServletResponse.setStatus(200);
        return response;
    }
    private GlobalResponse<String> bookServiceError(HttpServletResponse httpServletResponse, String debugMessage, String message) {
        GlobalResponse<String> response = new GlobalResponse<>();
        response.setStatus(HttpStatus.BAD_REQUEST);
        response.setMessage(message);
        response.setDebugMessage(debugMessage);
        httpServletResponse.setStatus(400);
        return response;
    }
    private User validateAndExtractUser(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        //Validate logged-in user
        helper.validateSession(httpServletRequest, httpServletResponse);
        //Get the logged-in user
        return helper.extractUser(httpServletRequest, httpServletResponse);
    }
}
