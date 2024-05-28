package org.findar.bookstore.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetPaginatedDto {
    private int page;
    private int size;
    private int start;
}
