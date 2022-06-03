package com.salenaluu.portfolio.blogpost.utils.mapper;

public record BlogPostRequestUpdate(String oldTitle,
                                    String newTitle,
                                    String content,
                                    String[] tags) {}