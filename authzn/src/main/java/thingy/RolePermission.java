package thingy;

import java.util.Optional;

public class RolePermission extends Permission {

    public RolePermission(String name) {
        super(name, "/");
    }

    @Override
    public Optional<AuthorizationDecision> isAuthorizedBy(ResourceActionAuthorizationContext ctx) {
        return ctx.handle(this);
    }
}
