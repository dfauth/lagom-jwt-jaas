package thingy;

import java.util.function.Consumer;
import java.util.function.Function;

public class AuthorizationDecisionMonad<R> {

    private final AuthorizationDecision decision;
    private Consumer<R> onComplete = r -> {};
    private Consumer<RuntimeException> onException = r -> {};
    private Consumer<SecurityException> onAuthorizationFailure = r -> {};
    private Function<R, ?> f;

    public AuthorizationDecisionMonad(AuthorizationDecision decision) {
        this.decision = decision;
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

    public PriviledgedActionResult<R> run(Function<Void, R> f) {
        try {
            R result = this.decision.run(() -> f.apply(null));
            onComplete.accept(result);
            return new PriviledgedActionResult(result);
        } catch (SecurityException e) {
            onAuthorizationFailure.accept(e);
            return new PriviledgedActionResult.AuthorizationFailure<>(e);
        } catch (RuntimeException e) {
            onException.accept(e);
            return new PriviledgedActionResult.ExecutionFailure<>(e);
        }
    }
}
