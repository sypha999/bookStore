package org.findar.bookstore.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@RequiredArgsConstructor
public class BlackListEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String blackListId;
    private String token;
    private LocalDateTime createdAt = LocalDateTime.now();
}
