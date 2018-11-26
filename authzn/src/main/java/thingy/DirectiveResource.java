package thingy;

public class DirectiveResource extends SimpleResource<Directive> {

    public DirectiveResource(Directive directive) {
        super(directive.getResource(), directive);
    }
}
