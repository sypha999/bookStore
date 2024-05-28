package org.findar.bookstore.repositories;


import org.findar.bookstore.entities.BlackListEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BlackListRepository extends JpaRepository<BlackListEntity,String> {
    public Optional<BlackListEntity> findByToken(String token);

}
