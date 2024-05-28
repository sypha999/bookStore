package org.findar.bookstore.dtos;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;


@Getter
@Setter
public class AddBookDto {

    private String title;
    private String allAuthors; // should be comma separated
    private Integer pages;
    private String language;
    private String genre;

}
