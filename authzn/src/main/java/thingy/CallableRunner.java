package thingy;

import java.security.PrivilegedAction;
import java.util.concurrent.Callable;
import java.util.function.Function;
import java.util.function.Supplier;


public interface CallableRunner {

    <R> R run(Callable<R> callable) throws SecurityException;

    static <R> Callable<R> adapter(PrivilegedAction<R> action) {
        return () -> action.run();
    }

    static <R> Callable<R> adapter(Supplier<R> supplier) {
        return () -> supplier.get();
    }

    static <R> Callable<R> adapter(Function<Void, R> f) {
        return () -> f.apply(null);
    }
}
