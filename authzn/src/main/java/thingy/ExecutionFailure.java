package thingy;

public class ExecutionFailure<R> extends AuthorizationDecisionMonad<R> {

    private final RuntimeException e;

    public ExecutionFailure(RuntimeException e) {
        this.e = e;
    }
}
