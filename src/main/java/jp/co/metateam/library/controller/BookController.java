package jp.co.metateam.library.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;
import jp.co.metateam.library.model.Account;
import jp.co.metateam.library.model.AccountDto;
import jp.co.metateam.library.model.BookMst;
import jp.co.metateam.library.model.BookMstDto;
import jp.co.metateam.library.service.BookMstService;
import lombok.extern.log4j.Log4j2;

//バリデーションチェック自作
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
//import javax.validation.Valid;

/**
 * 書籍関連クラス
 */
@Log4j2
@Controller
public class BookController {

    private final BookMstService bookMstService;

    @Autowired
    public BookController(BookMstService bookMstService) {
        this.bookMstService = bookMstService;
    }

    @GetMapping("/book/index")
    public String index(Model model) {
        // 書籍を全件取得
        List<BookMstDto> bookMstList = this.bookMstService.findAvailableWithStockCount();

        model.addAttribute("bookMstList", bookMstList);

        return "book/index";
    }

    @GetMapping("/book/add")
    public String add(Model model) {
        if (!model.containsAttribute("bookMstDto")) {
            model.addAttribute("bookMstDto", new BookMstDto());
        }

        return "book/add";
    }


    // もりりゅー流
    @PostMapping("/book/add")
    public String createBook(@ModelAttribute("bookMstDto") BookMstDto bookMstDto, BindingResult result, Model model) {

        boolean checkResult = bookMstService.checkbook(bookMstDto, model);
        // 画面変更します
        if (checkResult) {
            return "book/add"; // バリデーションエラー時、登録画面に戻す
        }
        boolean checkIsbnResult = bookMstService.checkIsbnEntry(bookMstDto, model);
        if (checkIsbnResult) {
            return "book/add"; // バリデーションエラー時、登録画面に戻す
        }

        // 登録処理
        bookMstService.save(bookMstDto);
        return "redirect:/book/index"; // 正常登録後、一覧に戻る

    }


}
