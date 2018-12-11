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

    public <E extends Enum<E> & Action<E>> Optional<AuthorizationDecision> permits(thingy.Principal p, Permission<E> permission) {
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
