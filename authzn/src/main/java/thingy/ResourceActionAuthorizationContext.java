package thingy;

import java.util.Optional;

public interface ResourceActionAuthorizationContext {

    default <E extends Enum<E> & Action<E>> Optional<AuthorizationDecision> handle(ActionPermission<E> permission) {
        return Optional.empty(); //defers a decision
    }

    Optional<AuthorizationDecision> handle(RolePermission permission);
}
