package todo.domain.common.exception;

import javax.ejb.ApplicationException;

/**
 * Resouce not found exception.
 * リソースが見つからない場合に発生する例外です。
 * EJBからスローする例外クラスにはApplicationExceptionアノテーションをつけます。
 * このアノテーションが無い場合はjavax.ejb.EJBExceptionにラップしてスローされます。
 */
@ApplicationException
public class ResourceNotFoundException extends RuntimeException{

    public ResourceNotFoundException(String message) {
        super(message);
    }
    
}
