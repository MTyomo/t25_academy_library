package jp.co.metateam.library.controller;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
import jp.co.metateam.library.repository.BookMstRepository;
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

    // もりりゅー流 登録処理
    @PostMapping("/book/add")
    public String createBook(@ModelAttribute("bookMstDto") BookMstDto bookMstDto, BindingResult result, Model model) {

        boolean checkResult = bookMstService.checkValidTitle(bookMstDto, model);
        boolean checkIsbnResult = bookMstService.checkValidIsbn(bookMstDto, model);

        // 画面変更します
        if (checkResult || checkIsbnResult) {
            return "book/add"; // バリデーションエラー時、登録画面に戻す
        }

        // 登録処理
        bookMstService.save(bookMstDto);
        return "redirect:/book/index"; // 正常登録後、一覧に戻る

    }

    // 編集画面への遷移
    @GetMapping("/book/edit/{id}")
    public String editBook(@PathVariable("id") Long id, Model model, RedirectAttributes redirectAttributes) {
        // IDに基づいて書籍データを取得する
        BookMstDto book = bookMstService.findById(id);

        if (book == null) {
            // 削除されている → 一覧画面にリダイレクトし、警告メッセージを渡す
            redirectAttributes.addFlashAttribute("popupMessage", "指定された書籍はすでに削除されています。");
            return "redirect:/book/index";
        }

        // 取得した書籍データを model にセット
        model.addAttribute("bookMstDto", book); // 編集する書籍データを渡す
        return "book/edit";
    }

    // データを更新させましょう
    @PostMapping("/book/edit")
    public String editBook(@ModelAttribute("bookMstDto") BookMstDto bookMstDto, BindingResult result, Model model,
            RedirectAttributes redirectAttributes) {

        // 書籍がすでに削除されていないか確認
        BookMstDto existingBook = bookMstService.findById(bookMstDto.getId());
        if (existingBook == null) {
            redirectAttributes.addFlashAttribute("popupMessage", "指定された書籍はすでに削除されています。");
            return "redirect:/book/index";
        }

        // 変更点があるか確認
        if (bookMstDto.getTitle().equals(existingBook.getTitle())
                && bookMstDto.getIsbn().equals(existingBook.getIsbn())) {
            model.addAttribute("noChangeMessage", "変更点はありません");
            return "book/edit";
        }

        // タイトルに変更がある場合のバリデーション
        boolean isInvalidTitle = false;
        if (!bookMstDto.getTitle().equals(existingBook.getTitle())) {
            isInvalidTitle = bookMstService.checkValidTitle(bookMstDto, model);
        }
        // ISBNに変更がある場合のバリデーション
        if (!bookMstDto.getIsbn().equals(existingBook.getIsbn())) {
            boolean isInvalidIsbn = bookMstService.checkValidIsbn(bookMstDto, model);
            if (isInvalidIsbn) {
                return "book/edit";
            }
        }

        if (isInvalidTitle) {
            return "book/edit";
        }

        // 更新処理を実行
        bookMstService.update(bookMstDto);
        return "redirect:/book/index"; // 更新成功後のリダイレクト
    }

    // 今回変更分

    // @GetMapping("book/delete/{id}")
    // @ResponseBody
    // public ResponseEntity<String> deleteBook(@PathVariable Long id) {

    // try {
    // bookMstService.deleteBook(id);
    // return ResponseEntity.ok("削除が完了しました");
    // // すでに削除されている書籍を削除しようとした場合など、状態に問題があるときのエラー
    // } catch (IllegalStateException e) {
    // return ResponseEntity.status(400).body(e.getMessage());
    // // 指定されたIDの書籍が存在しない場合
    // } catch (RuntimeException e) {
    // return ResponseEntity.status(404).body(e.getMessage());
    // } catch (Exception e) {
    // return ResponseEntity.status(500).body("予期しないエラーが発生しました");
    // }
    // }

    @GetMapping("book/delete/{id}")
    // @ResponseBody
    public String deleteBook(@PathVariable Long id,RedirectAttributes redirectAttributes) {
        try {
            bookMstService.deleteBook(id);
            redirectAttributes.addFlashAttribute("popupMessage", "削除が完了しました");
            return "redirect:/book/index"; 
        } catch (IllegalStateException e) {
            return e.getMessage(); // すでに削除済みなど
        } catch (RuntimeException e) {
            return e.getMessage(); // 書籍が存在しないなど
        } catch (Exception e) {
            return "予期しないエラーが発生しました。";
        }
    }

}
