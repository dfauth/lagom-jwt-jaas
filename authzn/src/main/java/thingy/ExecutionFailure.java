package thingy;

public class ExecutionFailure<R> extends AuthorizationDecisionMonad<R> {

    private final Exception e;

    public ExecutionFailure(Exception e) {
        this.e = e;
    }

    @Override
    public boolean isException() {
        return true;
    }

    public R get() {
        throw new IllegalStateException("Cannot get the result of a failed action");
    }

}
