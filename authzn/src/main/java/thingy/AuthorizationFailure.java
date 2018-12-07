package thingy;

public class AuthorizationFailure<R> extends AuthorizationDecisionMonad<R> {

    private final SecurityException e;

    public AuthorizationFailure(SecurityException e) {
        this.e = e;
    }
}
