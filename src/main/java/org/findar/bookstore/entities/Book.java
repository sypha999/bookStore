package org.findar.bookstore.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import org.findar.bookstore.dtos.enums.BookStatus;
import org.findar.bookstore.entities.helpers.BaseEntity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@RequiredArgsConstructor
public class Book extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String bookId;
    @Column(nullable = false)
    private String title;
    @ElementCollection(targetClass = ArrayList.class,fetch = FetchType.EAGER)
    private List<String> authors;
    private Integer pages;
    private String language;
    private String genre;
    private String fileFormat;
    private Long fileSize;
    @Lob
    private byte [] fileContent = new byte[0];
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastBorrowed;
    private Integer borrowCount=0;
    @Enumerated(EnumType.STRING)
    private BookStatus bookStatus=BookStatus.AVAILABLE;
    @ManyToOne(fetch = FetchType.LAZY)
    private User owner;
    @OneToOne(fetch = FetchType.LAZY)
    private User lender;
}

