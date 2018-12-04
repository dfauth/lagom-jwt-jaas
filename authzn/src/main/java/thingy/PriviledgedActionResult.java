package thingy;

import java.util.Optional;

public class PriviledgedActionResult<R> {

    private final Optional<R> result;

    public PriviledgedActionResult(R result) {
        this(Optional.of(result));
    }

    protected PriviledgedActionResult() {
        this(Optional.empty());
    }

    protected PriviledgedActionResult(Optional<R> result) {
        this.result = result;
    }

    public static class ExecutionFailure<R> extends PriviledgedActionResult<R> {

        private final RuntimeException e;

        public ExecutionFailure(RuntimeException e) {
            this.e = e;
        }
    }

    public static class AuthorizationFailure<R> extends PriviledgedActionResult<R> {

        private final SecurityException e;

        public AuthorizationFailure(SecurityException e) {
            this.e = e;
        }
    }
}
