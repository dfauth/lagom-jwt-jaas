package thingy;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;

import static thingy.AuthorizationDecision.ALLOW;


public class Directive {
    private final Set<ImmutablePrincipal> principals;
    private final Set<String> actions;
    private final String resource;
    private final AuthorizationDecision authznAction;

    public Directive(ImmutablePrincipal principal) {
        this(Collections.singleton(principal), "/", Collections.singleton("*"), ALLOW);
    }

    public Directive(ImmutablePrincipal principal, String resource) {
        this(Collections.singleton(principal), resource, Collections.singleton("*"), ALLOW);
    }

    public Directive(ImmutablePrincipal principal, String resource, String action) {
        this(Collections.singleton(principal), resource, DirectiveBuilder.splitTrim(action), ALLOW);
    }

    public Directive(Set<ImmutablePrincipal> principals, String resource, Set<String> actions, AuthorizationDecision authznAction) {
        this.principals = principals;
        this.resource = resource;
        this.actions = actions;
        this.authznAction = authznAction;
    }

    public Set<ImmutablePrincipal> getPrincipals() {
        return principals;
    }

    public Set<String> getActions() {
        return actions;
    }

    public String getResource() {
        return resource;
    }

    public AuthorizationDecision getAuthznAction() {
        return authznAction;
    }

    public ResourceActionAuthorizationContext forPrincipal(thingy.Principal p) {
        if(principals.contains(p)) {
            return new ResourceActionAuthorizationContext() {
                @Override
                public <E extends Enum<E> & Action<E>> Optional<AuthorizationDecision> handle(ActionPermission<E> permission) {
                    return actions.stream().map(a -> permission.isAuthorizedBy(a)).reduce(Optional.empty(), (o1, o2) -> AuthorizationDecision.compose(o1, o2));
                }

                @Override
                public Optional<AuthorizationDecision> handle(RolePermission permission) {
                    return Optional.of(ALLOW);
                }
            };
        }
        return cannotAuthorize();
    }

    private ResourceActionAuthorizationContext cannotAuthorize() {
        return permission -> Optional.empty();
    }
}
