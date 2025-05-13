package jp.co.metateam.library.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
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
        boolean checkIsbnResult = bookMstService.checkIsbnEntry(bookMstDto, model);

        // 画面変更します
        if (checkResult || checkIsbnResult) {
            return "book/add"; // バリデーションエラー時、登録画面に戻す
        }

        // 登録処理
        bookMstService.save(bookMstDto);
        return "redirect:/book/index"; // 正常登録後、一覧に戻る

    }

    // 今回からの処理ござ～る

    // 編集画面への遷移

    @GetMapping("/book/edit/{id}")
    public String editBook(@PathVariable("id") Long id, Model model, RedirectAttributes redirectAttributes) {
        // IDに基づいて書籍データを取得する
        BookMstDto book = bookMstService.findById(id);

        if (book == null) {
            // 削除されている → 一覧画面にリダイレクトし、警告メッセージを渡す
            redirectAttributes.addFlashAttribute("errorMessage", "指定された書籍はすでに削除されています。");
            return "redirect:/book/index";
        }

        // 取得した書籍データを model にセット
        // model.addAttribute("title", "書籍編集");
        model.addAttribute("bookMstDto", book); // 編集する書籍データを渡す
        return "book/edit"; // edit.html を表示
    }

    // データを更新させましょう
    @PostMapping("/book/edit")
    public String editBook(@ModelAttribute("bookMstDto") BookMstDto bookMstDto, BindingResult result, Model model,
            RedirectAttributes redirectAttributes) {

        // 書籍がすでに削除されていないか確認
        BookMstDto existingBook = bookMstService.findById(bookMstDto.getId());
        if (existingBook == null) {
            // 削除済みのため、一覧にリダイレクトしつつメッセージを渡す
            redirectAttributes.addFlashAttribute("deleteMessage", "書籍はすでに削除されています。");
            return "redirect:/book/index";
        }

        // 書籍名またはISBNが変更されているかチェック
        boolean isTitleChanged = bookMstService.checkTitleChange(bookMstDto);
        boolean isIsbnChanged = bookMstService.checkIsbnChange(bookMstDto);
        // ✅ 変更点がない場合、ポップアップ用のメッセージを渡して編集画面に戻る
        if (!isTitleChanged && !isIsbnChanged) {
            model.addAttribute("noChangeMessage", "変更点はありません");
            // model.addAttribute("bookMstDto", bookMstDto);
            return "book/edit";
        }

        // タイトルに変更がある場合のバリデーション
        if (isTitleChanged) {
            boolean checkTitleResult = bookMstService.checkbook(bookMstDto, model);
            if (checkTitleResult) {
                return "book/edit";
            }
        }
        // ISBNに変更がある場合のバリデーション
        if (isIsbnChanged) {
            boolean checkIsbnResult = bookMstService.checkIsbnEntry(bookMstDto, model);
            if (checkIsbnResult) {
                return "book/edit";
            }
        }

        // 更新処理を実行
        bookMstService.update(bookMstDto);
        return "redirect:/book/index"; // 更新成功後のリダイレクト
    }

}
