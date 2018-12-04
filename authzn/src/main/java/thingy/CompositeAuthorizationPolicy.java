package thingy;

public class CompositeAuthorizationPolicy implements AuthorizationPolicy {

    void grantDirective(String domain, Directive directive) {}
    void revokeDirective(String domain, Directive directive){}

    @Override
    public <E extends Enum<E> & Action<E>> AuthorizationAction permit(Subject subject, Permission<E> permission) {
        return null;
    }

    public void onEvent(DirectiveEvent directiveEvent) {
        directiveEvent.process(this);
    }
}
