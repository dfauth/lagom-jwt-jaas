package thingy;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;


public class ImmutableSubject implements Subject {
    private final Principal userPrincipal;
    private final Set<Principal> rolePrincipals;
    private final Set<Principal> all;

    public ImmutableSubject(Principal userPrincipal) {
        this(userPrincipal, Collections.emptySet());
    }

    public ImmutableSubject(Principal userPrincipal, Principal... rolePrincipals) {
        this(userPrincipal, new HashSet(Arrays.asList(rolePrincipals)));
    }

    ImmutableSubject(Principal userPrincipal, Set<Principal> rolePrincipals) {
        this.userPrincipal = userPrincipal;
        this.rolePrincipals = rolePrincipals;
        this.all = getPrincipals();
    }

    @Override
    public Set<Principal> getPrincipals() {
        if(this.all != null) {
            return this.all;
        }
        HashSet<Principal> tmp = new HashSet<>(rolePrincipals);
        tmp.add(userPrincipal);
        return Collections.unmodifiableSet(tmp);
    }
}
