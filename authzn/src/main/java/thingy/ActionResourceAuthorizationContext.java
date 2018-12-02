package thingy;

import java.util.Optional;

public interface ActionResourceAuthorizationContext<E extends Enum<E> & Action<E>> {

    Optional<AuthorizationAction> forPrincipal(Principal principal);
}
