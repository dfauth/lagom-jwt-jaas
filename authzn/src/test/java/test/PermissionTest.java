package test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.Test;
import thingy.*;

import java.util.Optional;
import java.util.Set;

import static org.testng.Assert.assertTrue;
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
        Directive directive = new Directive(domain, fred);
        AuthorizationPermitter policy = (subject, permission) -> subject.getPrincipals().stream().map(p -> directive.permits(p, permission)).
                reduce(Optional.empty(), (o1,o2) -> AuthorizationAction.compose(o1,o2));
                //filter(o -> o.isPresent()).findFirst().orElse(Optional.of(DENY));
        Optional<AuthorizationAction> authorizationAction = policy.permit(new ImmutableSubject(fred), new RolePermission(domain));
        assertTrue(authorizationAction.isPresent());
        assertTrue(authorizationAction.get().isAllowed());
    }

    @Test
    public void testActions() {

        {
            Set actions = TestAction.parser().parse("").actions();
            assertTrue(actions.isEmpty());
            assertTrue(actions.size() == 0);
            Assert.assertFalse(actions.contains(TestAction.READ));
            Assert.assertFalse(actions.contains(TestAction.WRITE));
            Assert.assertFalse(actions.contains(TestAction.EXECUTE));
        }

        {
            Set actions = TestAction.parser().parse("read").actions();
            Assert.assertFalse(actions.isEmpty());
            assertTrue(actions.size() == 1);
            assertTrue(actions.contains(TestAction.READ));
            Assert.assertFalse(actions.contains(TestAction.WRITE));
            Assert.assertFalse(actions.contains(TestAction.EXECUTE));
        }

        {
            Set actions = TestAction.parser().parse("read, write, blah").actions();
            Assert.assertFalse(actions.isEmpty());
            assertTrue(actions.size() == 2);
            assertTrue(actions.contains(TestAction.READ));
            assertTrue(actions.contains(TestAction.WRITE));
            Assert.assertFalse(actions.contains(TestAction.EXECUTE));
        }

        {
            Set actions = TestAction.parser().parse("read, write, *, blah").actions();
            Assert.assertFalse(actions.isEmpty());
            assertTrue(actions.size() == 3);
            assertTrue(actions.contains(TestAction.READ));
            assertTrue(actions.contains(TestAction.WRITE));
            assertTrue(actions.contains(TestAction.EXECUTE));
        }

    }

    static enum TestAction implements Action<TestAction> {

        READ, WRITE, EXECUTE;

        public static Action.Parser<TestAction> parser() {
            return new Parser(TestAction.values());
        }
    }
}
