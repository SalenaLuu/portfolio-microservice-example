package com.salenaluu.portfolio.blogpost.model;

import com.salenaluu.portfolio.blogpost.utils.enums.Tags;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;


@Data
@AllArgsConstructor
@NoArgsConstructor
@RequiredArgsConstructor
@Document(collection = "blog_post")
public class BlogPost {
    @Id
    private UUID id;

    @Size(min = 10, max = 40)
    @NotBlank(message = "Title can't be empty")
    private @NonNull String title;

    @Size(min = 20, max = 1000)
    @NotBlank(message = "Content can't be empty")
    private @NonNull String content;

    private String publishedAt;

    @Email(message = "Mail-Address invalid")
    private String creatorEmail;
    private Set<Tags> tags = new HashSet<>();
}
