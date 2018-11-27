package thingy;

import java.util.Set;
import java.util.stream.Collectors;

public class DirectiveBuilder implements Builder<Directive> {

    private AuthorizationAction authznAction;
    private Set<ImmutablePrincipalBuilder> principals;
    private String domain;
    private String actions;
    private String resource;

    public Directive build() {
        return new Directive(domain, principals.stream().map(b -> b.build()).collect(Collectors.toSet()), resource, actions, authznAction);
    }

    public void setAuthznAction(AuthorizationAction authznAction) {
        this.authznAction = authznAction;
    }

    public void setPrincipals(Set<ImmutablePrincipalBuilder> principals) {
        this.principals = principals;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public void setActions(String actions) {
        this.actions = actions;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }
}
