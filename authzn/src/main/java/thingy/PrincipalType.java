package thingy;

public enum PrincipalType {
    USER, ROLE;

    public ImmutablePrincipal of(String source, String name) {
        return new ImmutablePrincipal(this, source, name);
    }

    public ImmutablePrincipal of(String name) {
        return new ImmutablePrincipal(this, name);
    }
}
