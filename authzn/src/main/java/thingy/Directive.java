package thingy;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;

import static thingy.AuthorizationAction.ALLOW;


public class Directive {
    private final Set<ImmutablePrincipal> principals;
    private final String actions;
    private final String resource;
    private final AuthorizationAction authznAction;

    public Directive(ImmutablePrincipal principal) {
        this(Collections.singleton(principal), "/", "*", ALLOW);
    }

    public Directive(ImmutablePrincipal principal, String resource) {
        this(Collections.singleton(principal), resource, "*", ALLOW);
    }

    public Directive(ImmutablePrincipal principal, String resource, String actions) {
        this(Collections.singleton(principal), resource, actions, ALLOW);
    }

    public Directive(Set<ImmutablePrincipal> principals, String resource, String actions, AuthorizationAction authznAction) {
        this.principals = principals;
        this.resource = resource;
        this.actions = actions;
        this.authznAction = authznAction;
    }

    public Set<ImmutablePrincipal> getPrincipals() {
        return principals;
    }

    public String getActions() {
        return actions;
    }

    public String getResource() {
        return resource;
    }

    public AuthorizationAction getAuthznAction() {
        return authznAction;
    }

    public <E extends Enum<E>> Set<Action<E>> forPrincipal(thingy.Principal p) {
        if(principals.contains(p)) {
            // return new Action.Parser<E>(new Action.Actions(clazz).values()).parseActions(actions);
        }
        return Collections.emptySet();
    }

    public ResourceAuthorizationContext forResource(Resource resource) {
        return new ResourceAuthorizationContext(){
            @Override
            public <F extends Enum<F> & Action<F>> ActionResourceAuthorizationContext forAction(F action) {
                return principal -> {
                    if(new DirectiveResource(Directive.this).getIterablePath().equals(resource.getIterablePath())) {
                        if(principals.contains(principal)) {
                            if(Actions.of(action.getDeclaringClass()).parser().parseActions(getActions()).contains(action)) {
                                return Optional.ofNullable(getAuthznAction());
                            }
                        }
                    }
                    return Optional.empty();
                };
            }
        };
    }

    public <E extends Enum<E> & Action<E>> Optional<AuthorizationAction> permits(thingy.Principal p, Permission<E> permission) {
        return forResource(permission.getResource()).forAction(permission.getAction()).forPrincipal(p);
//        if(principals.contains(p)) {
//            if(resource.equalsIgnoreCase(permission.getResource())) {
//                if(permission.getActions().implies(actions)) {
//                    return Optional.of(authznAction);
//                }
//            }
//        }
//        return Optional.empty();
    }
}
