package thingy;

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

    public static enum EventType {
        GRANT,
        REVOKE;

        public DirectiveEventBuilder directive(Directive directive) {
            return new DirectiveEventBuilder(this, new DirectiveBuilder(){
                @Override
                public Directive build() {
                    return directive;
                }
            });
        }
    }
}
