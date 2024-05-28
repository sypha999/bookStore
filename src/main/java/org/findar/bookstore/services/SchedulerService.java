package org.findar.bookstore.services;

import lombok.RequiredArgsConstructor;
import org.findar.bookstore.entities.BlackListEntity;
import org.findar.bookstore.repositories.BlackListRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@EnableScheduling
public class SchedulerService {

    private final BlackListRepository blackListRepo;

    @Scheduled(cron = "0 48 * * * *") // executes after 1 day
    @Async
    public void deleteBlackListedTokens(){
        List<BlackListEntity> entities = blackListRepo.findAll();
        for(BlackListEntity entity : entities){
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime then = entity.getCreatedAt();
            Duration duration = Duration.between(then, now);
            long days = duration.toDays();
            if(days >= 1 ){
                blackListRepo.delete(entity);
            }
        }
    }

}
