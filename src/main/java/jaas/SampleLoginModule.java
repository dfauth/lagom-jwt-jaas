package jaas;

import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.login.LoginException;
import javax.security.auth.spi.LoginModule;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

public class SampleLoginModule implements LoginModule {

        private Subject subject;
    private CallbackHandler handler;
    private String[] principals;

    @Override
        public void initialize(Subject subject, CallbackHandler callbackHandler, Map<String, ?> sharedState, Map<String, ?> options) {
                this.subject = subject;
                this.handler = callbackHandler;
        }

        @Override
        public boolean login() throws LoginException {
            try {
                this.handler.handle(new Callback[]{new MyCallbackHandler.TestCallback(c -> {this.principals = c;}){}});
                return true;
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (UnsupportedCallbackException e) {
                throw new RuntimeException(e);

            }
        }

        @Override
        public boolean commit() {
            Arrays.asList(principals).stream().map(p -> new SimplePrincipal(p)).forEach(p -> subject.getPrincipals().add(p));
            return true;
        }

        @Override
        public boolean abort() {
                return true;
        }

        @Override
        public boolean logout() {
                return true;
        }
}
