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

}
