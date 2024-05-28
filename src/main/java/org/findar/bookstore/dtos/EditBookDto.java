package org.findar.bookstore.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EditBookDto extends AddBookDto{
    private String id;
}
