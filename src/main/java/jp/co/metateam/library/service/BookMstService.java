package jp.co.metateam.library.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import io.micrometer.common.util.StringUtils;
import jp.co.metateam.library.model.Account;
import jp.co.metateam.library.model.AccountDto;
import jp.co.metateam.library.model.BookMst;
import jp.co.metateam.library.model.BookMstDto;
import jp.co.metateam.library.repository.BookMstRepository;

@Service
public class BookMstService {

    private final BookMstRepository bookMstRepository;
    
    @Autowired
    public BookMstService(BookMstRepository bookMstRepository){
        this.bookMstRepository = bookMstRepository;
    }
    
    public List<BookMstDto> findAvailableWithStockCount() {
        List<BookMst> books = this.bookMstRepository.findLimitedBook();
        List<BookMstDto> bookMstDtoList = new ArrayList<BookMstDto>();

        // 書籍の在庫数を取得
        // FIXME: 現状は書籍ID毎にDBに問い合わせている。一度のSQLで完了させたい。
        for (int i = 0; i < books.size(); i++) {
            BookMst book = books.get(i);
            BookMstDto bookMstDto = new BookMstDto();
            bookMstDto.setId(book.getId());
            bookMstDto.setIsbn(book.getIsbn());
            bookMstDto.setTitle(book.getTitle());
            bookMstDtoList.add(bookMstDto);
        }

        return bookMstDtoList;
    }

    @Transactional
    public void save(BookMstDto bookMstDto) {
        try {
            // AccountDtoからAccountへの変換
            BookMst bookMst = new BookMst();

            bookMst.setTitle(bookMstDto.getTitle());
            bookMst.setIsbn(bookMstDto.getIsbn());
            //bookMst.setId(bookMstDto.getId());
            //bookMst.setDeletedAt(this.bookMstRepository.encode(bookMstDto.getDeletedAt())); // パスワードをハッシュ化してから保存
            //bookMst.setEmail(bookMstDto.getEmail());

            // データベースへの保存
            this.bookMstRepository.save(bookMst);
        } catch (Exception e) {
            throw e;
        }
    }
   
    @PostMapping  


    
    public boolean checkbook (BookMstDto bookMstDto,Model model ){
       //データ格納先の定義と、エラー格納先の定義
        String Title = bookMstDto.getTitle();
        String isbn = bookMstDto.getIsbn();
        List<String> validationTitleErrors = new ArrayList<String>();
        List<String> validationIsbnErrors = new ArrayList<String>();

        // 1. 書籍名のバリデーションチェックをする
        if (StringUtils.isEmpty(Title)) {
            validationTitleErrors.add("書籍名は必須です。");
            model.addAttribute("titleErrors",validationTitleErrors);

        } else  if (Title.length() > 255 ) {
            validationTitleErrors.add("書籍名は255文字以内で入力してください");
            model.addAttribute("titleErrors",validationTitleErrors);
        }

        // 2. ISBNのバリデーションチェックを行うよ
        if (StringUtils.isEmpty(isbn)) {
            validationIsbnErrors.add("ISBNは必須です。");
            model.addAttribute("isbnErrors",validationIsbnErrors);
        }else if (isbn.length() != 13 ) {
            validationIsbnErrors.add("ISBNは13文字で入力してください");
            model.addAttribute("isbnErrors",validationIsbnErrors);
        }
        // 3. 文字種チェック（半角数字のみ）
        if (!isbn.matches("^[0-9]+$")) {
            validationIsbnErrors.add("ISBNは半角数字で入力してください");
            model.addAttribute("isbnErrors", validationIsbnErrors);
    }
    if (!validationTitleErrors.isEmpty() || !validationIsbnErrors.isEmpty()) {
        return true;
     }    
     return false;
    }

     public Boolean checkIsbnEntry(BookMstDto bookMstDto,Model model) {

      String getIsbn = bookMstDto.getIsbn();
       List<String>errIsbnList = new ArrayList<>() ;
       List<BookMst> bookMst  = this.bookMstRepository.selectByIsbn(getIsbn);

       if (!bookMst.isEmpty()) {
           errIsbnList.add("登録されているISBNです");
           model.addAttribute("isbnErrors", errIsbnList);
           return true;}
       return false;
   }
}



