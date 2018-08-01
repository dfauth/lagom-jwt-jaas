package jaas;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;
import java.io.IOException;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class MyCallbackHandler implements CallbackHandler {

    private String[] principals;

    public MyCallbackHandler(String[] principals) {
        this.principals = principals;
    }

    public void handle(Callback[] callbacks)
            throws IOException, UnsupportedCallbackException {

        Stream.of(callbacks).forEach(c -> {
            if(c instanceof TestCallback) {
                TestCallback cb = TestCallback.class.cast(c);
                cb.principals(principals);
            }
        });
    }

    public static class TestCallback implements Callback {

        private Consumer<String[]> consumer;

        public TestCallback(Consumer<String[]> consumer) {
            this.consumer = consumer;
        }

        public void principals(String[] principals) {
            consumer.accept(principals);
        }
    }
}
