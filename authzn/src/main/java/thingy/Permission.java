package thingy;

import java.util.Optional;

public abstract class Permission {

    private final String resource;
    private final String domain;

    public Permission(String name, String resource) {
        this.domain = name;
        this.resource = resource;
    }

    public Resource getResource() {
        return new SimpleResource(resource);
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == null) {
            return false;
        }
        if(obj == this) {
            return true;
        }
        if(getClass().equals(obj.getClass())) {
            Permission other = getClass().cast(obj);
            return this.resource.equals(other.resource);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return resource.hashCode() | domain.hashCode();
    }

    public boolean implies(Permission other) {
        return other.domain.contains(this.domain);
    }

    public String getDomain() {
        return domain;
    }

    public boolean impliesDomain(String domain) {
        return this.domain.equalsIgnoreCase(domain);
    }

    public abstract Optional<AuthorizationDecision> isAuthorizedBy(ResourceActionAuthorizationContext ctx);
}
