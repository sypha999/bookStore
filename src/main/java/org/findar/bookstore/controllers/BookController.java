package org.findar.bookstore.controllers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.findar.bookstore.dtos.*;
import org.findar.bookstore.services.BookService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/book")
@RequiredArgsConstructor
public class BookController {
    private final BookService bookService;

    @PostMapping("add")
    public GlobalResponse<?> addBook(@RequestBody AddBookDto book,@RequestHeader(name="Authorization") String token, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse){
        return bookService.addBook(book, httpServletRequest, httpServletResponse);
    }

    @PostMapping("add-content")
    public GlobalResponse<?> addBookContent(@RequestPart(value = "id")String id, @RequestPart(value = "file") MultipartFile file,@RequestHeader(name="Authorization") String token, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse){
        return bookService.addBookContent(id, file, httpServletRequest, httpServletResponse);
    }

    @PatchMapping("modify")
    public GlobalResponse<?> editBookInfo(@RequestBody EditBookDto editBookDto, @RequestHeader(name="Authorization") String token,HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse){
        return bookService.editBookInfo(editBookDto, httpServletRequest, httpServletResponse);
    }
    @PatchMapping("modify-content")
    public GlobalResponse<?> editBookContent(@RequestPart(value = "id")String id, @RequestPart(value = "file") MultipartFile file,@RequestHeader(name="Authorization") String token, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse){
        return bookService.editBookContent(id, file, httpServletRequest, httpServletResponse);
    }

    @DeleteMapping("delete")
    public GlobalResponse<?> deleteBook(@RequestBody EditBookContentDto deleteBookDto, @RequestHeader(name="Authorization") String token,HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse){
        return bookService.deleteBook(deleteBookDto, httpServletRequest, httpServletResponse);
    }

    @GetMapping("get-content")
    public ResponseEntity<byte[]> getBookContent(@RequestBody GetBookContentDto getBookContentDto, @RequestHeader(name="Authorization") String token, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse){
        return bookService.getBookContent(getBookContentDto, httpServletRequest, httpServletResponse);
    }

    @PostMapping("borrow")
    public GlobalResponse<?> borrowBook(@RequestBody GetBookContentDto borrowBookDto, @RequestHeader(name="Authorization") String token,HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse){
        return bookService.borrowBook(borrowBookDto, httpServletRequest, httpServletResponse);
    }

    @PostMapping("return")
    public GlobalResponse<?> returnBook(@RequestBody GetBookContentDto returnBookDto, @RequestHeader(name="Authorization") String token,HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse){
        return bookService.returnBook(returnBookDto, httpServletRequest, httpServletResponse);
    }

    @GetMapping("get-all-books-by-user")
    public GlobalResponse<?> getAllbooksByUser(@RequestBody GetPaginatedDto getPaginatedDto,@RequestHeader(name="Authorization") String token,HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse){
        return bookService.getAllbooksByUser(getPaginatedDto, httpServletRequest, httpServletResponse);
    }

    @GetMapping("get-all-books")
    public GlobalResponse<?> getAllbooks(@RequestBody GetPaginatedDto getPaginatedDto,@RequestHeader(name="Authorization") String token,HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse){
        return bookService.getAllbooks(getPaginatedDto, httpServletRequest, httpServletResponse);
    }

}
