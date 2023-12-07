package com.example.sbbtest.article;

import com.example.sbbtest.siteUser.SiteUser;
import com.example.sbbtest.siteUser.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class ArticleController {

    private final ArticleService articleService;
    private final UserService userService;

    @GetMapping("/article/list")
    public String list(Model model, @RequestParam(value = "kw", defaultValue = "") String kw) {
        List<Article> article = articleService.getList(kw);
        model.addAttribute("article", article);
        model.addAttribute("kw",kw);
        return "article_list";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/article/create")
    public String create(Model model) {
        model.addAttribute("articleForm", new ArticleForm());
        return "article_form";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/article/create")
    public String create(@Valid ArticleForm articleForm,
                         BindingResult bindingResult, Principal principal) {
        if (bindingResult.hasErrors()) {
            return "article_form";
        }
        SiteUser siteUser = this.userService.getUser(principal.getName());
        this.articleService.create(articleForm.getSubject(), articleForm.getContent(), siteUser);
        return "redirect:/article/list";
    }

    @GetMapping("/article/detail/{id}")
    public String detail(Model model, @PathVariable(name = "id") Integer id) {
        Article article = this.articleService.findById(id);
        model.addAttribute("article", article);
        return "article_detail";
    }

    @GetMapping("/article/modify/{id}")
    @PreAuthorize("isAuthenticated()")
    public String modify(ArticleForm articleForm, @PathVariable("id") Integer id, Principal principal) {
        Article article = this.articleService.getArticle(id);
        if (!article.getAuthor().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다.");
        }
        articleForm.setSubject(article.getSubject());
        articleForm.setContent(article.getContent());
        return "article_form";
    }



    @PreAuthorize("isAuthenticated()")
    @PostMapping("/article/modify/{id}")
    public String modify(@Valid ArticleForm articleForm, BindingResult bindingResult,
                         Principal principal, @PathVariable("id") Integer id) {
        if (bindingResult.hasErrors()) {
            return "article_form";
        }
        Article article = this.articleService.getArticle(id);
        if (!article.getAuthor().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다.");
        }
        this.articleService.modify(article, articleForm.getSubject(), articleForm.getContent());
        return String.format("redirect:/article/detail/%s", id);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/article/delete/{id}")
    public String delete(Principal principal, @PathVariable("id") Integer id) {
        Article article = this.articleService.getArticle(id);
        if (!article.getAuthor().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "삭제권한이 없습니다.");
        }
        this.articleService.delete(article);
        return "redirect:/";
    }
}
