package thingy;


public class BasePermission<E extends Action> {

    private final String resource;
    private Action.Actions<E> actions;
    private final String domain;

    private BasePermission(String name, String resource) {
        this.domain = name;
        this.resource = resource;
    }

    public BasePermission(String name, String resource, String actions) {
        this(name, resource);
        this.actions = parseActions(actions);
    }

    public BasePermission(String name, String resource, Action.Actions<E> actions) {
        this(name, resource);
        this.actions = actions;
    }

    protected Action.Actions<E> parseActions(String actions) {
        return  BaseAction.parser().parse(actions);
    }

    public String getResource() {
        return resource;
    }

    public Action.Actions<E> getActions() {
        return actions; //.stream().collect(Collectors.joining(","));
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
            BasePermission other = getClass().cast(obj);
            return this.resource.equals(other.resource) && this.actions.equals(other.actions);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return resource.hashCode() | actions.hashCode();
    }

    public boolean implies(BasePermission other) {
        return other.domain.contains(this.domain);
    }

    public String getDomain() {
        return domain;
    }

    public boolean impliesDomain(String domain) {
        return this.domain.equalsIgnoreCase(domain);
    }

    static enum BaseAction implements Action<BaseAction> {
        READ, WRITE;

        public static Action.Parser<BaseAction> parser() {
            return new Parser(BaseAction.values());
        }
    }
}
