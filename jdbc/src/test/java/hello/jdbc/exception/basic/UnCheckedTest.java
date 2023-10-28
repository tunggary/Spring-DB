package hello.jdbc.exception.basic;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

@Slf4j
public class UnCheckedTest {
    static class UnCheckedException extends RuntimeException {
        public UnCheckedException(String message) {
            super(message);
        }
    }

    static class Service {
        Repository repository = new Repository();
        public void callCatch() {
            try {
                repository.call();
            } catch (UnCheckedException e) {
                log.info("언체크 예외 catch");
            }
        }

        public void callThrow() {
            repository.call();
            log.info("언체크 예외 throw");
        }
    }
    static class Repository {
        public void call(){
            throw new UnCheckedException("언체크 예외 발생");
        }
    }

    @Test
    void unCheckedCatchTest(){
        Service service = new Service();
        service.callCatch();
    }

    @Test
    void unCheckedThrowTest(){
        Service service = new Service();
        Assertions.assertThatThrownBy(() -> service.callThrow())
                .isInstanceOf(UnCheckedException.class);
    }
}
