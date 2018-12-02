package test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.Test;
import thingy.*;

import java.util.Optional;

import static org.testng.Assert.assertTrue;
import static thingy.AuthorizationAction.DENY;
import static thingy.PrincipalType.USER;


public class PermissionTest {

    private static final Logger logger = LoggerFactory.getLogger(PermissionTest.class);

    @Test
    public void testIt() {
        RolePermission role0 = new RolePermission("admin");
        RolePermission role1 = new RolePermission("admin.1");
        BlahPermission role2 = new BlahPermission("admin.2");
        assertTrue(role0.implies(role1)); // must be the same class and wildcarded on the name
        assertTrue(role0.implies(role2));
        Assert.assertFalse(role1.implies(role2));
        Assert.assertFalse(role2.implies(role1));
        Assert.assertFalse(role2.implies(role0));
        Assert.assertFalse(role1.implies(role0));
    }

    @Test
    public void testPolicy() {
        String domain = "domain";
        ImmutablePrincipal fred = USER.of("fred");
        Directive directive = new Directive(fred);
        AuthorizationPermitter policy = new AuthorizationPermitter() {
            @Override
            public <E extends Enum<E> & Action<E>> AuthorizationAction permit(Subject subject, Permission<E> permission) {
                return subject.getPrincipals().stream().map(p -> directive.permits(p, permission)).
                        reduce(Optional.empty(), (o1, o2) -> AuthorizationAction.compose(o1, o2)).orElse(DENY);
            }
        };
        AuthorizationAction authorizationAction = policy.permit(new ImmutableSubject(fred), new RolePermission(domain));
        assertTrue(authorizationAction.isAllowed());
    }

}
