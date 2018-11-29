package test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.Test;
import thingy.*;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

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
        AuthorizationPermitter policy = (subject, permission) -> subject.getPrincipals().stream().map(p -> directive.permits(p, permission)).
                reduce(Optional.empty(), (o1,o2) -> AuthorizationAction.compose(o1,o2)).orElse(DENY);
        AuthorizationAction authorizationAction = policy.permit(new ImmutableSubject(fred), new RolePermission(domain));
        assertTrue(authorizationAction.isAllowed());
    }

    @Test
    public void testActions() {

        Action.Parser<TestAction> parser = Action.Actions.from(TestAction.class).parser();
        {
            Set actions = parser.parseActions("");
            assertTrue(actions.isEmpty());
            assertTrue(actions.size() == 0);
            Assert.assertFalse(actions.contains(TestAction.READ));
            Assert.assertFalse(actions.contains(TestAction.WRITE));
            Assert.assertFalse(actions.contains(TestAction.EXECUTE));
        }

        {
            Set actions = parser.parseActions("read");
            Assert.assertFalse(actions.isEmpty());
            assertTrue(actions.size() == 1);
            assertTrue(actions.contains(TestAction.READ));
            Assert.assertFalse(actions.contains(TestAction.WRITE));
            Assert.assertFalse(actions.contains(TestAction.EXECUTE));
        }

        {
            Set actions = parser.parseActions("read, write, blah");
            Assert.assertFalse(actions.isEmpty());
            assertTrue(actions.size() == 2);
            assertTrue(actions.contains(TestAction.READ));
            assertTrue(actions.contains(TestAction.WRITE));
            Assert.assertFalse(actions.contains(TestAction.EXECUTE));
        }

        {
            Set actions = parser.parseActions("read, write, *, blah");
            Assert.assertFalse(actions.isEmpty());
            assertTrue(actions.size() == 3);
            assertTrue(actions.contains(TestAction.READ));
            assertTrue(actions.contains(TestAction.WRITE));
            assertTrue(actions.contains(TestAction.EXECUTE));
        }

    }

    public static enum TestAction implements Action<TestAction> {
        READ, WRITE, EXECUTE;
    }
}
