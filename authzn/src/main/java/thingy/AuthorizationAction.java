package thingy;

import java.security.PrivilegedAction;
import java.util.Optional;

public enum AuthorizationAction implements PriviledgedActionRunner {
    ALLOW,
    DENY;

    public boolean isAllowed() {
        return this == ALLOW;
    }

    public boolean isDenied() {
        return this == DENY;
    }

    public AuthorizationAction compose(AuthorizationAction that) {
        return isDenied() ? this : that;
    }

    @Override
    public <R> R run(PrivilegedAction<R> action) {
        if(isAllowed()) {
            return action.run();
        }
        throw new SecurityException("Oops, not allowed");
    }

    public static Optional<AuthorizationAction> compose(Optional<AuthorizationAction> o1, Optional<AuthorizationAction> o2) {
        return o1.map(
                      a1 -> o2.map(
                              a2 -> Optional.of(a1.compose(a2))
                      ).orElse(o1)
        ).orElse(o2);
    }
}
