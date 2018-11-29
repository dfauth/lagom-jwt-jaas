package thingy;

import java.util.Optional;

public interface AuthorizationPermitter {
    Optional<AuthorizationAction> permit(Subject subject, BasePermission permission);
}
