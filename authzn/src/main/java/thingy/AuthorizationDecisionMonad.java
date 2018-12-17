package thingy;

import java.security.PrivilegedAction;
import java.util.concurrent.Callable;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

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

    public static <R> Callable<R> adapter(PrivilegedAction<R> action) {
        return () -> action.run();
    }

    public static <R> Callable<R> adapter(Supplier<R> supplier) {
        return () -> supplier.get();
    }

    public static <R> Callable<R> adapter(Function<Void, R> f) {
        return () -> f.apply(null);
    }

    public static <R> AuthorizationDecisionMonad<R> of(PrivilegedAction<R> f) {
        return of(adapter(f));
    }

    public static <R> AuthorizationDecisionMonad<R> of(Function<Void,R> f) {
        return of(adapter(f));
    }

    public static <R> AuthorizationDecisionMonad<R> of(Supplier<R> f) {
        return of(adapter(f));
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
