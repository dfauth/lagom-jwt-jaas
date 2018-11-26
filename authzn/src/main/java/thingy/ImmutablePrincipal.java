package thingy;

public class ImmutablePrincipal implements Principal {

    private final PrincipalType principalType;
    private final String source;
    private final String name;

    ImmutablePrincipal(PrincipalType principalType, String name) {
        this(principalType,"default",name);
    }

    ImmutablePrincipal(PrincipalType principalType, String source, String name) {
        this.principalType = principalType;
        this.source = source;
        this.name = name;
    }

    @Override
    public PrincipalType getType() {
        return principalType;
    }

    @Override
    public String getSource() {
        return source;
    }

    @Override
    public String getName() {
        return String.format("{0}:{1}:{2}",getType().name(),getSource(),name);
    }
}
