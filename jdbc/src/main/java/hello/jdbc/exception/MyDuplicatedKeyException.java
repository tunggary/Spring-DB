package hello.jdbc.exception;

public class MyDuplicatedKeyException extends MyDBException{

    public MyDuplicatedKeyException() {
        super();
    }

    public MyDuplicatedKeyException(String message) {
        super(message);
    }

    public MyDuplicatedKeyException(String message, Throwable cause) {
        super(message, cause);
    }


    public MyDuplicatedKeyException(Throwable cause) {
        super(cause);
    }
}
