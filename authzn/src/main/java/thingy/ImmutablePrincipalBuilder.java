package thingy;

public class ImmutablePrincipalBuilder implements Builder<ImmutablePrincipal> {

    private PrincipalType type;
    private String source;
    private String name;

    public ImmutablePrincipal build() {
        return new ImmutablePrincipal(type, source, name);
    }

    public void setPrincipalType(PrincipalType type) {
        this.type = type;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public void setName(String name) {
        this.name = name;
    }
}
