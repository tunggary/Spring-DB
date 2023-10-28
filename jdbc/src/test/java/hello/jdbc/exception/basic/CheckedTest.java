package hello.jdbc.exception.basic;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Slf4j
public class CheckedTest {
    /**
     * Exception을 상속받은 예외는 체크 예외가 된다.
     */
    static class MyCheckedException extends Exception {
        public MyCheckedException(String message) {
            super(message);
        }
    }

    static class Service {
        Repository repository = new Repository();

        /**
         * 예외를 잡아서 처리하는 코드
         */
        public void callCatch() throws Exception {
            try {
                repository.call(1);
            } catch (MyCheckedException e) {
                log.info("체크 예외 catch");
            }
        }

        /**
         * 체크 예외를 밖으로 던지는 코드
         * @throws MyCheckedException
         */
        public void callThrow() throws Exception {
            repository.call(1);
            log.info("체크 예외 throw");
        }
    }

    static class Repository {
        public void call(int param) throws Exception {
            if (param == 1) {
                throw new MyCheckedException("체크 예외 발생");
            } else {
                throw new Exception("체크 예외 2");
            }
        }
    }

    @Test
    void checkedCatchTest() throws Exception {
        Service service = new Service();
        service.callCatch();
    }

    @Test
    void checkedThrowTest(){
        Service service = new Service();
        assertThatThrownBy(()->service.callThrow())
                .isInstanceOf(MyCheckedException.class);
    }
}
