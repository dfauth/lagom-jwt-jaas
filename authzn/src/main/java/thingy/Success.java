package thingy;

public class Success<R> extends AuthorizationDecisionMonad<R> {

    private final R value;

    public Success(R value) {
        this.value = value;
    }

}
