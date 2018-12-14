package thingy;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class SingleDomainPolicy implements DirectiveEventAware {

    private String domain;
    private List<Directive> directives = new ArrayList<>();

    public SingleDomainPolicy(String domain) {
        this.domain = domain;
    }

    @Override
    public boolean grantDirective(String domain, Directive d) {
        if(domain.equalsIgnoreCase(this.domain)) {
            this.directives.add(d);
        }
        return true;
    }

    @Override
    public boolean revokeDirective(String domain, Directive d) {
        if(domain.equalsIgnoreCase(this.domain)) {
            this.directives.remove(d);
        }
        return true;
    }

    @Override
    public <E extends Enum<E> & Action<E>> Set<Directive> directivesFor(Permission permission) {
        return new HashSet(directives);
    }
}
