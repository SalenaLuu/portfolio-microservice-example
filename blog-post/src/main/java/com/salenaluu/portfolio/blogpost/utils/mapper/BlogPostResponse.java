package com.salenaluu.portfolio.blogpost.utils.mapper;

public record BlogPostResponse(String title,
                               String content,
                               String email,
                               String[] tags){}