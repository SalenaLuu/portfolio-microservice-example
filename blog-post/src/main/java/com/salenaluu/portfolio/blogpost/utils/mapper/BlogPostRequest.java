package com.salenaluu.portfolio.blogpost.utils.mapper;

public record BlogPostRequest(String title,
                              String content,
                              String[] tags){}