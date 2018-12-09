package thingy;

public interface DirectiveEventAware extends AuthorizationPolicy {

    boolean grantDirective(String domain, Directive directive);
    boolean revokeDirective(String domain, Directive directive);

    default boolean onEvent(DirectiveEvent directiveEvent) {
        return directiveEvent.process(this);
    }
}
