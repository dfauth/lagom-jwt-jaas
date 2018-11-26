package thingy;

import java.security.Principal;
import java.util.Set;

public class DirectiveBuilder {

    private AuthorizationAction authznAction;
    private Set<Principal> principals;
    private String domain;
    private String actions;
    private String resource;

    public Directive build() {
        return new Directive(getDomain(), getPrincipals(), getResource(), getActions(), getAuthznAction());

    }

    public AuthorizationAction getAuthznAction() {
        return authznAction;
    }

    public void setAuthznAction(AuthorizationAction authznAction) {
        this.authznAction = authznAction;
    }

    public Set<Principal> getPrincipals() {
        return principals;
    }

    public void setPrincipals(Set<Principal> principals) {
        this.principals = principals;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getActions() {
        return actions;
    }

    public void setActions(String actions) {
        this.actions = actions;
    }

    public String getResource() {
        return resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }
}
