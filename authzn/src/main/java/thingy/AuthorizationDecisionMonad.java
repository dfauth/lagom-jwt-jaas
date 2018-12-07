package thingy;

import java.security.PrivilegedAction;
import java.util.concurrent.Callable;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class AuthorizationDecisionMonad<R> {

    private Consumer<R> onComplete = r -> {};
    private Consumer<RuntimeException> onException = r -> {};
    private Consumer<SecurityException> onAuthorizationFailure = r -> {};
    private Function<Void, R> f;

    public AuthorizationDecisionMonad(Function<Void, R> f) {
        this.f = f;
    }

    public AuthorizationDecisionMonad() {
    }

    public void onComplete(Consumer<R> consumer) {
        onComplete = consumer;
    }

    public void onException(Consumer<RuntimeException> consumer) {
        onException = consumer;
    }

    public void onAuthorizationFailure(Consumer<SecurityException> consumer) {
        onAuthorizationFailure = consumer;
    }

//    public <U> AuthorizationDecisionMonad<U> map(Function<? super PriviledgedActionResult<R>, ? extends U> f) {
//        return new AuthorizationDecisionMonad<U>(v -> f.apply(invoke()));
//    }
//
//    public <U> AuthorizationDecisionMonad<U> flatMap(Function<? super PriviledgedActionResult<R>, AuthorizationDecisionMonad<? extends U>> f) {
//        return new AuthorizationDecisionMonad<U>(v -> f.apply(invoke()).invoke());
//    }
//
//    public <U> AuthorizationDecisionMonad<Tuple2<R, U>> zip(AuthorizationDecisionMonad<U> reader) {
//        return this.flatMap(a -> reader.map(b -> new Tuple2<>(a, b)));
//    }

    public AuthorizationDecisionMonad<R> apply(AuthorizationDecision decision) {
        try {
            R result = decision.run(()->f.apply(null));
            onComplete.accept(result);
            return new Success(result);
        } catch (SecurityException e) {
            onAuthorizationFailure.accept(e);
            return new AuthorizationFailure(e);
        } catch (RuntimeException e) {
            onException.accept(e);
            return new ExecutionFailure(e);
        }
    }

    public static <R> Function<Void, R> adapter(PrivilegedAction<R> action) {
        return ignored -> action.run();
    }

    public static <R> Function<Void, R> adapter(Supplier<R> supplier) {
        return ignored -> supplier.get();
    }

    public static <R> Function<Void, R> adapter(Callable<R> callable) {
        return ignored -> {
            try {
                return callable.call();
            } catch(Exception e) {
                throw new RuntimeException(e);
            }
        };
    }

    public static <R> AuthorizationDecisionMonad<R> of(PrivilegedAction<R> f) {
        return of(adapter(f));
    }

    public static <R> AuthorizationDecisionMonad<R> of(Function<Void,R> f) {
        return new AuthorizationDecisionMonad<R>(f);
    }

    public static <R> AuthorizationDecisionMonad<R> of(Supplier<R> f) {
        return of(adapter(f));
    }

    public static <R> AuthorizationDecisionMonad<R> of(Callable<R> f) {
        return of(adapter(f));
    }

//    public R apply(AuthorizationDecision decision) {
//        return decision.run(() -> f.apply(null));
//    }
//
//    static <R> AuthorizationDecisionMonad<R> monad(Function<Void, R> f) {
//        return monad(adapter(f));
//    }
//
//    static <R> AuthorizationDecisionMonad<R> monad(Callable<R> f) {
//        return monad(adapter(f));
//    }
//
//    static <R> AuthorizationDecisionMonad<R> monad(PrivilegedAction<R> action) {
//        try {
//            R result = run(action);
//            return new Success(result);
//        } catch (SecurityException e) {
//            return new AuthorizationFailure(e);
//        } catch (RuntimeException e) {
//            return new ExecutionFailure(e);
//        }
//    }
}
