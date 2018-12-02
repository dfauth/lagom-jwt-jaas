package thingy;

public abstract class Permission<E extends Enum<E> & Action<E>> {

    private final String resource;
    private E action;
    private final String domain;

    protected Permission(String name, String resource) {
        this.domain = name;
        this.resource = resource;
    }

    public Permission(String name, String resource, String action) {
        this(name, resource);
        this.action = parseAction(action);
    }

    public Permission(String name, String resource, E action) {
        this(name, resource);
        this.action = action;
    }

    protected E parseAction(String action) {
        return parser().parseAction(action).orElseThrow(()->new IllegalArgumentException("Oops. No action named "+action));
    }

    protected abstract Actions.Parser<E> parser();

    public Resource getResource() {
        return new SimpleResource(resource);
    }

    public E getAction() {
        return action;
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
            return this.resource.equals(other.resource) && this.action.equals(other.action);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return resource.hashCode() | action.hashCode();
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
}
