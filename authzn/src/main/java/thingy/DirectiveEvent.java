package thingy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.BiConsumer;

import static thingy.DirectiveEvent.EventType.GRANT;

public class DirectiveEvent {

    private static final Logger logger = LoggerFactory.getLogger(DirectiveEvent.class);

    private String domain;
    private EventType eventType;
    private Directive directive;

    public DirectiveEvent() {
    }

    public DirectiveEvent(String domain, Directive directive) {
        this(domain, GRANT, directive);
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

    public boolean process(DirectiveEventAware policy) {
        try {
            eventType.accept(policy, this);
            return true;
        } catch(RuntimeException e) {
            logger.error(e.getMessage(), e);
            return false;
        }
    }

    public static enum EventType implements BiConsumer<DirectiveEventAware, DirectiveEvent> {
        GRANT((p,e) -> p.grantDirective(e.getDomain(), e.getDirective())),
        REVOKE((p,e) -> p.revokeDirective(e.getDomain(), e.getDirective()));

        private final BiConsumer<DirectiveEventAware, DirectiveEvent> nested;

        EventType(BiConsumer<DirectiveEventAware, DirectiveEvent> nested) {
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

        public void accept(DirectiveEventAware policy, DirectiveEvent event) {
            nested.accept(policy, event);
        }
    }
}
