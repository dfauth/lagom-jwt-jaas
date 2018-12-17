package thingy;

import java.util.concurrent.Callable;
import java.util.function.Consumer;
import java.util.function.Function;

public class AuthorizationDecisionMonad<R> {

    private Consumer<R> onComplete = r -> {};
    private Consumer<Exception> onException = r -> {};
    private Consumer<SecurityException> onAuthorizationFailure = r -> {};
    private Callable<R> f;

    public AuthorizationDecisionMonad(Callable<R> callable) {
        this.f = callable;
    }

    public AuthorizationDecisionMonad() {
    }

    public void onComplete(Consumer<R> consumer) {
        onComplete = consumer;
    }

    public void onException(Consumer<Exception> consumer) {
        onException = consumer;
    }

    public void onAuthorizationFailure(Consumer<SecurityException> consumer) {
        onAuthorizationFailure = consumer;
    }

    public <U> AuthorizationDecisionMonad<U> map(Function<? super R, ? extends U> g) {
        return new AuthorizationDecisionMonad<U>(() -> g.apply(f.call()));
    }

    public AuthorizationDecisionMonad<R> apply(AuthorizationDecision decision) {
        if(decision.isAllowed()) {
            try {
                R result = f.call();
                onComplete.accept(result);
                return new Success(result);
            } catch (Exception e) {
                onException.accept(e);
                return new ExecutionFailure(e);
            }
        } else {
            SecurityException e = new SecurityException("Oops, not allowed");
            onAuthorizationFailure.accept(e);
            return new AuthorizationFailure(e);
        }
    }

    public static <R> AuthorizationDecisionMonad<R> of(Callable<R> f) {
        return new AuthorizationDecisionMonad<R>(f);
    }

    public R get() {
        throw new IllegalStateException("Cannot get the result without a decision");
    }

    public boolean isSuccess() {
        return false;
    }

    public boolean isException() {
        return false;
    }

    public boolean isUnauthorised() {
        return false;
    }

}
