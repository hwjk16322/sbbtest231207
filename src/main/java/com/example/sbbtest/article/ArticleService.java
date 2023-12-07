package com.example.sbbtest.article;

import com.example.sbbtest.siteUser.SiteUser;
import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ArticleService {

    private final ArticleRepository articleRepository;

    public List<Article> getList(String kw) {

        Specification<Article> spec = search(kw);
        List<Article> articleList = articleRepository.findAll(spec);
        return articleList;
    }

    public void create(String subject, String content, SiteUser user) {
        Article article = new Article();
        article.setSubject(subject);
        article.setContent(content);
        article.setLocalDateTime(LocalDateTime.now());
        article.setAuthor(user);
        this.articleRepository.save(article);
    }

    public Article findById(Integer id) {
        Optional<Article> oa = this.articleRepository.findById(id);
        return oa.get();
    }

    public Article getArticle(Integer id) {
        Optional<Article> oa = this.articleRepository.findById(id);
        return oa.get();
    }


    public void modify(Article article, String subject, String content) {
        article.setSubject(subject);
        article.setContent(content);
        article.setModifyDate(LocalDateTime.now());
        this.articleRepository.save(article);
    }

    public void delete(Article article) {
        this.articleRepository.delete(article);
    }


    private Specification<Article> search(String kw) {
        return new Specification<>() {
            private static final long serialVersionUID = 1L;
            @Override
            public Predicate toPredicate(Root<Article> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                query.distinct(true);
                Join<Article, SiteUser> u1 = root.join("author", JoinType.LEFT);
                return cb.or(cb.like(root.get("subject"), "%" + kw + "%"),
                        cb.like(root.get("content"), "%" + kw + "%"),
                        cb.like(u1.get("username"), "%" + kw + "%"));
            }
        };
    }
}
