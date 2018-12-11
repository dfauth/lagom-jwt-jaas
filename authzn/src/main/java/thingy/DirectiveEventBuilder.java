package thingy;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;

public class DirectiveEventBuilder implements Builder<DirectiveEvent> {

    private DirectiveEvent.EventType eventType;
    private Builder<Directive> builder;
    private String domain;

    @JsonCreator
    public DirectiveEventBuilder(
            @JsonProperty("eventType") DirectiveEvent.EventType eventType,
            @JsonProperty("directive") DirectiveBuilder builder) {
        this.setEventType(eventType);
        this.setDirectiveBuilder(builder);
    }

    public DirectiveEvent build() {
        return new DirectiveEvent(domain, eventType, builder.build());
    }

    public DirectiveEvent inDomain(String domain) {
        this.setDomain(domain);
        return build();
    }

    public void setEventType(DirectiveEvent.EventType eventType) {
        this.eventType = eventType;
    }

    @JsonSetter("directive")
    public void setDirectiveBuilder(DirectiveBuilder builder) {
        this.builder = builder;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }
}
