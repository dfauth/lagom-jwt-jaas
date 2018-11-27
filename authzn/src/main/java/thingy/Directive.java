package thingy;

import java.util.Collections;
import java.util.Set;

import static thingy.AuthorizationAction.GRANT;


public class Directive {
    private final Set<ImmutablePrincipal> principals;
    private final String domain;
    private final String actions;
    private final String resource;
    private final AuthorizationAction authznAction;

    public Directive(String domain, ImmutablePrincipal principal) {
        this(domain, Collections.singleton(principal), "*", "*", GRANT);
    }

    public Directive(String domain, ImmutablePrincipal principal, String resource) {
        this(domain, Collections.singleton(principal), resource, "*", GRANT);
    }

    public Directive(String domain, ImmutablePrincipal principal, String resource, String actions) {
        this(domain, Collections.singleton(principal), resource, actions, GRANT);
    }

    public Directive(String domain, Set<ImmutablePrincipal> principals, String resource, String actions, AuthorizationAction authznAction) {
        this.domain = domain;
        this.principals = principals;
        this.resource = resource;
        this.actions = actions;
        this.authznAction = authznAction;
    }

    public Set<ImmutablePrincipal> getPrincipals() {
        return principals;
    }

    public String getDomain() {
        return domain;
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

    public boolean permits(thingy.Principal p, BasePermission permission) {
        if(permission.impliesDomain(domain)) {
            if(principals.contains(p)) {
                if(resource.equalsIgnoreCase(permission.getResource())) {
                    if(permission.getActions().implies(actions)) {
                        return authznAction.isAllowed();
                    }
                }
            }
        }
        return false;
    }
}
