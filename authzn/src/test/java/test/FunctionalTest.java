package test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;
import thingy.*;

import java.util.Optional;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;
import static org.testng.AssertJUnit.assertNotNull;
import static thingy.AuthorizationAction.ALLOW;
import static thingy.AuthorizationAction.DENY;
import static thingy.PrincipalType.ROLE;
import static thingy.PrincipalType.USER;


public class FunctionalTest {

    private static final Logger logger = LoggerFactory.getLogger(FunctionalTest.class);

    @Test
    public void testIt() {
        PolicyFunction policy = new PolicyFunction();
        Directive d = new Directive(ROLE.of("role1"));
        policy.add(d);
        Subject subject = new ImmutableSubject(USER.of("fred"), ROLE.of("role1"));
        Permission permission = new RolePermission("admin");

        AuthorizationAction result = policy.permit(subject, permission);

        assertFalse(result.isAllowed());
    }

    @Test
    public void testAuthorizationAction() {
        assertTrue(ALLOW.compose(ALLOW).isAllowed());
        assertTrue(ALLOW.compose(DENY).isDenied());
        assertTrue(DENY.compose(ALLOW).isDenied());
        assertTrue(DENY.compose(DENY).isDenied());

        Optional<AuthorizationAction> optionAllow = Optional.of(ALLOW);
        Optional<AuthorizationAction> optionDeny = Optional.of(DENY);
        Optional<AuthorizationAction> optionEmpty = Optional.empty();

        assertTrue(AuthorizationAction.compose(optionAllow, optionAllow).get().isAllowed());
        assertTrue(AuthorizationAction.compose(optionAllow, optionDeny).get().isDenied());
        assertTrue(AuthorizationAction.compose(optionAllow, optionEmpty).get().isAllowed());

        assertTrue(AuthorizationAction.compose(optionDeny, optionAllow).get().isDenied());
        assertTrue(AuthorizationAction.compose(optionDeny, optionDeny).get().isDenied());
        assertTrue(AuthorizationAction.compose(optionDeny, optionEmpty).get().isDenied());

        assertTrue(AuthorizationAction.compose(optionEmpty, optionAllow).get().isAllowed());
        assertTrue(AuthorizationAction.compose(optionEmpty, optionDeny).get().isDenied());
        assertFalse(AuthorizationAction.compose(optionEmpty, optionEmpty).isPresent());
    }

    @Test
    public void testAuthorizationActionRunner() {
        PriviledgedActionRunner authorizationAction = ALLOW;
        TestToggle result = new TestToggle();
        TestToggle toggle = authorizationAction.run(() -> result.toggle());
        assertNotNull(toggle);
        assertTrue(toggle.wasToggled());

        toggle.reset();
        TestToggle tmp = null;
        try {
            tmp = DENY.run(() -> result.toggle());
        } catch (SecurityException e) {
            logger.info(e.getMessage(), e);
        }
        assertNull(tmp);
        assertNotNull(toggle);
        assertFalse(toggle.wasToggled());
    }

    class TestToggle {

        private boolean toggled = false;

        public TestToggle toggle() {
            toggled = true;
            return this;
        }

        public boolean wasToggled() {
            boolean tmp = toggled;
            reset();
            return tmp;
        }

        public void reset() {
            toggled = false;
        }
    }
}
