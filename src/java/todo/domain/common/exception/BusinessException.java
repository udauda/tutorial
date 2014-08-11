package todo.domain.common.exception;

import javax.ejb.ApplicationException;

/**
 * Business exception.
 * 業務例外発生時の例外クラスです。
 * EJBからスローする例外クラスにはApplicationExceptionアノテーションをつけます。
 * このアノテーションが無い場合はjavax.ejb.EJBExceptionにラップしてスローされます。
 */
@ApplicationException
public class BusinessException extends RuntimeException {
    public BusinessException(String messeage) {
        super(messeage);
    }
}
