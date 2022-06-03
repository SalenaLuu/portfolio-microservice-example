package com.salenaluu.portfolio.blogpost.utils.interfaces;

import org.springframework.context.annotation.Bean;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public interface IDateTimeCreator {
    @Bean
    static String createDateTime(){
        return LocalDateTime
                .now()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
}