package org.findar.bookstore.dtos;

import lombok.Getter;
import lombok.Setter;
import org.findar.bookstore.dtos.enums.BookStatus;
import org.findar.bookstore.entities.User;
import java.time.LocalDateTime;

import java.util.List;

@Getter
@Setter
public class ViewBookDto {
    private String title;
    private List<String> authors;
    private String format;
    private Integer pages;
    private String language;
    private String genre;
    private String fileFormat;
    private Long fileSize;
    private LocalDateTime lastBorrowed;
    private Integer borrowCount;
    private BookStatus  bookStatus;
}
