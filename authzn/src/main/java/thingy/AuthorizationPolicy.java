package thingy;


import java.util.Arrays;
import java.util.Optional;
import java.util.Set;

import static thingy.AuthorizationDecision.DENY;

public interface AuthorizationPolicy {

    default <E extends Enum<E> & Action<E>> AuthorizationDecision permit(Subject subject, Permission permission) {
        return subject.getPrincipals().stream().flatMap(p -> directivesFor(permission).stream().map(d -> permission.isAuthorizedBy(d.forPrincipal(p)))).
                reduce(Optional.empty(), (o1, o2) -> AuthorizationDecision.compose(o1, o2)).orElse(DENY);
    }

    default <E extends Enum<E> & Action<E>> AuthorizationDecision permitForAllOf(Subject subject, ActionPermission<E>... permissions) {
        return subject.getPrincipals().stream().flatMap(p -> Arrays.stream(permissions).flatMap(p1 -> directivesFor(p1).stream().map(d -> p1.isAuthorizedBy(d.forPrincipal(p))))).
                reduce(Optional.empty(), (o1, o2) -> AuthorizationDecision.compose(o1, o2)).orElse(DENY);
    }

    <E extends Enum<E> & Action<E>> Set<Directive> directivesFor(Permission permission);
}

