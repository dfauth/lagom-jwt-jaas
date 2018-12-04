package thingy;

import java.util.function.BiConsumer;

public class DirectiveEvent {

    private String domain;
    private EventType eventType;
    private Directive directive;

    public DirectiveEvent() {
    }

    public DirectiveEvent(String domain, EventType eventType, Directive directive) {
        this.domain = domain;
        this.eventType = eventType;
        this.directive = directive;
    }

    public String getDomain() {
        return domain;
    }

    public EventType getEventType() {
        return eventType;
    }

    public Directive getDirective() {
        return directive;
    }

    public void process(CompositeAuthorizationPolicy policy) {
        eventType.accept(policy, this);
    }

    public static enum EventType implements BiConsumer<CompositeAuthorizationPolicy, DirectiveEvent> {
        GRANT((p,e) -> p.grantDirective(e.getDomain(), e.getDirective())),
        REVOKE((p,e) -> p.revokeDirective(e.getDomain(), e.getDirective()));

        private final BiConsumer<CompositeAuthorizationPolicy, DirectiveEvent> nested;

        EventType(BiConsumer<CompositeAuthorizationPolicy, DirectiveEvent> nested) {
            this.nested = nested;
        }

        public DirectiveEventBuilder directive(Directive directive) {
            return new DirectiveEventBuilder(this, new DirectiveBuilder(){
                @Override
                public Directive build() {
                    return directive;
                }
            });
        }

        public void accept(CompositeAuthorizationPolicy policy, DirectiveEvent event) {
            nested.accept(policy, event);
        }
    }
}
