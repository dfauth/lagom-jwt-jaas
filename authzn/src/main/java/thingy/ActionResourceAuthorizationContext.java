package thingy;

import java.util.Optional;

public interface ActionResourceAuthorizationContext {

    Optional<AuthorizationAction> forPrincipal(Principal principal);
}
