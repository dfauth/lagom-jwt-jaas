package thingy;

import java.util.Optional;

import static thingy.AuthorizationDecision.ALLOW;

public abstract class ActionPermission<E extends Enum<E> & Action<E>> extends Permission {

    private E action;

    public ActionPermission(String name, String resource, E action) {
        super(name, resource);
        this.action = action;
    }

    protected E parseAction(String action) {
        return parser().parseAction(action).orElseThrow(()->new IllegalArgumentException("Oops. No action named "+action));
    }

    protected abstract Actions.Parser<E> parser();

    public E getAction() {
        return action;
    }

    @Override
    public boolean equals(Object obj) {
        if(super.equals(obj)) {
            if(getClass().equals(obj.getClass())) {
                ActionPermission other = getClass().cast(obj);
                return this.action.equals(other.action);
            }
        }
        return false;
    }

    @Override
    public int hashCode() {
        return super.hashCode() | action.hashCode();
    }

    @Override
    public Optional<AuthorizationDecision> isAuthorizedBy(ResourceActionAuthorizationContext ctx) {
        return ctx.handle(this);
    }

    public Optional<AuthorizationDecision> isAuthorizedBy(String action) {
        return isAuthorizedBy(this.parseAction(action));
    }

    public Optional<AuthorizationDecision> isAuthorizedBy(E action) {
        return action.implies(getAction()) ? Optional.of(ALLOW) : Optional.empty();
    }
}
