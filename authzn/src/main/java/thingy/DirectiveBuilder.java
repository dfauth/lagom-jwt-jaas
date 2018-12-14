package thingy;

import com.fasterxml.jackson.annotation.JsonSetter;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import static thingy.AuthorizationDecision.ALLOW;
import static thingy.AuthorizationDecision.DENY;

public class DirectiveBuilder implements Builder<Directive> {

    private AuthorizationDecision decision;
    private Set<Builder<ImmutablePrincipal>> principals;
    private Set<String> actions;
    private String resource;

    public static DirectiveBuilder allow() {
        return new DirectiveBuilder().withDecision(ALLOW);
    }

    public static DirectiveBuilder deny() {
        return new DirectiveBuilder().withDecision(DENY);
    }

    private DirectiveBuilder withDecision(AuthorizationDecision decision) {
        this.decision = decision;
        return this;
    }

    public Directive build() {
        return new Directive(principals.stream().map(b -> b.build()).collect(Collectors.toSet()), resource, actions, decision);
    }

    public void setDecision(AuthorizationDecision decision) {
        this.decision = decision;
    }

    public void setPrincipals(Set<ImmutablePrincipalBuilder> principals) {
        this.principals = principals.stream().map(p -> (Builder<ImmutablePrincipal>)p).collect(Collectors.toSet());
    }

    public void setActions(String actions) {
        setActions(splitTrim(actions));
    }

    @JsonSetter("actions")
    public void setActions(Set<String> actions) {
        this.actions = actions;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }

    public DirectiveBuilder withPrincipals(ImmutablePrincipal... principals) {
        this.principals = Arrays.stream(principals).map(p -> (Builder<ImmutablePrincipal>) () -> p).collect(Collectors.toSet());
        return this;
    }

    public DirectiveBuilder withResource(String resource) {
        this.resource = resource;
        return this;
    }

    public DirectiveBuilder withActions(String actions) {
        setActions(splitTrim(actions));
        return this;
    }

    protected static Set<String> splitTrim(String actions) {
        return Arrays.stream(actions.split(",")).map(s -> s.trim()).collect(Collectors.toSet());
    }
}
