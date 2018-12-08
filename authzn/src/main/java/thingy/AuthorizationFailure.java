package thingy;

public class AuthorizationFailure<R> extends AuthorizationDecisionMonad<R> {

    private final SecurityException e;

    public AuthorizationFailure(SecurityException e) {
        this.e = e;
    }

    @Override
    public boolean isUnauthorised() {
        return true;
    }

    public R get() {
        throw new IllegalStateException("Cannot get the result as authorization failed");
    }

}
