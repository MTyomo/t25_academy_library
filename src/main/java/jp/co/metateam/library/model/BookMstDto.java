package jp.co.metateam.library.model;

import java.security.Timestamp;

import org.springframework.beans.factory.annotation.Autowired;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import jp.co.metateam.library.repository.BookMstRepository;
import lombok.Getter;
import lombok.Setter;

/**
 * 書籍マスタDTO
 */
@Getter
@Setter
public class BookMstDto {

    private Long id;

    private String isbn;

    private String title;

    private Timestamp deletedAt;

    private BookMst bookMst;

    private Boolean deletedFlag;
}
