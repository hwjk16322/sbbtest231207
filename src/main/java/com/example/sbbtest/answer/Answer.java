package com.example.sbbtest.answer;

import com.example.sbbtest.article.Article;
import com.example.sbbtest.siteUser.SiteUser;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class Answer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(columnDefinition = "TEXT")
    private String content;

    @CreatedDate
    private LocalDateTime localDateTime;

    @ManyToOne
    private Article article;

    private LocalDateTime modifyDate;

    @ManyToOne
    private SiteUser author;
}
