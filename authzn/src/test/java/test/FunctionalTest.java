package test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;
import thingy.*;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.function.Function;

import static org.testng.Assert.*;
import static org.testng.AssertJUnit.assertNotNull;
import static thingy.AuthorizationDecision.ALLOW;
import static thingy.AuthorizationDecision.DENY;
import static thingy.PrincipalType.ROLE;
import static thingy.PrincipalType.USER;


public class FunctionalTest {

    private static final Logger logger = LoggerFactory.getLogger(FunctionalTest.class);

    @Test
    public void testIt() {
        SingleDomainPolicy policy = new SingleDomainPolicy("poo");
        Directive d = new Directive(ROLE.of("role1"));
        policy.onEvent(new DirectiveEvent("admin", d));
        Subject subject = new ImmutableSubject(USER.of("fred"), ROLE.of("role1"));
        Permission permission = new RolePermission("admin");

        AuthorizationDecision result = policy.permit(subject, permission);

        assertFalse(result.isAllowed()); // fails because of differing domains

        // add an identical directive for the correct domain
        policy.onEvent(new DirectiveEvent("poo", d));
        assertTrue(policy.permit(subject, permission).isAllowed());
    }

    @Test
    public void testAuthorizationAction() {
        assertTrue(ALLOW.compose(ALLOW).isAllowed());
        assertTrue(ALLOW.compose(DENY).isDenied());
        assertTrue(DENY.compose(ALLOW).isDenied());
        assertTrue(DENY.compose(DENY).isDenied());

        Optional<AuthorizationDecision> optionAllow = Optional.of(ALLOW);
        Optional<AuthorizationDecision> optionDeny = Optional.of(DENY);
        Optional<AuthorizationDecision> optionEmpty = Optional.empty();

        assertTrue(AuthorizationDecision.compose(optionAllow, optionAllow).get().isAllowed());
        assertTrue(AuthorizationDecision.compose(optionAllow, optionDeny).get().isDenied());
        assertTrue(AuthorizationDecision.compose(optionAllow, optionEmpty).get().isAllowed());

        assertTrue(AuthorizationDecision.compose(optionDeny, optionAllow).get().isDenied());
        assertTrue(AuthorizationDecision.compose(optionDeny, optionDeny).get().isDenied());
        assertTrue(AuthorizationDecision.compose(optionDeny, optionEmpty).get().isDenied());

        assertTrue(AuthorizationDecision.compose(optionEmpty, optionAllow).get().isAllowed());
        assertTrue(AuthorizationDecision.compose(optionEmpty, optionDeny).get().isDenied());
        assertFalse(AuthorizationDecision.compose(optionEmpty, optionEmpty).isPresent());
    }

    @Test
    public void testPriviledgedActionRunner() {
        TestToggle result = new TestToggle();
        TestToggle toggle = ALLOW.run(() -> result.toggle());
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

    @Test
    public void testMonad() {
        TestToggle toggle = new TestToggle();
        TestToggle finalToggle = toggle;
        Function<Void, TestToggle> f = nothing -> finalToggle.toggle();
        AuthorizationDecisionMonad<TestToggle> monad = AuthorizationDecisionMonad.of(f);
        CompletionListener a = new CompletionListener(r -> logger.info("result is " + r));
        CompletionListener b = new CompletionListener(r -> logger.info("result is failure: "+r));
        CompletionListener c = new CompletionListener(r -> logger.info("did not run: "+r));
        monad.onComplete(a);
        monad.onException(b);
        monad.onAuthorizationFailure(c);

        AuthorizationDecisionMonad<TestToggle> result = monad.apply(ALLOW);

        assertNotNull(result);
        assertTrue(a.wasSet());
        assertTrue(toggle.wasToggled());
        assertFalse(b.wasSet());
        assertFalse(c.wasSet());
        a.reset();b.reset();c.reset();

        toggle.reset();
        toggle = new TestToggle("Oops");
        TestToggle finalToggle1 = toggle;
        AuthorizationDecisionMonad<TestToggle> monad1 = AuthorizationDecisionMonad.of(n -> finalToggle1.toggle());
        monad1.onComplete(a);
        monad1.onException(b);
        monad1.onAuthorizationFailure(c);
        result = monad1.apply(ALLOW);

        assertNotNull(result);
        assertFalse(a.wasSet());
        assertTrue(toggle.wasToggled());
        assertTrue(b.wasSet());
        assertFalse(c.wasSet());
        a.reset();b.reset();c.reset();

        result = monad1.apply(DENY);

        assertNotNull(result);
        assertFalse(a.wasSet());
        assertFalse(toggle.wasToggled());
        assertFalse(b.wasSet());
        assertTrue(c.wasSet());
        a.reset();b.reset();c.reset();
    }

    @Test
    public void testMap() {
        TestToggle toggle = new TestToggle();
        TestToggle finalToggle = toggle;
        Function<Void, TestToggle> f = nothing -> finalToggle.toggle();
        AuthorizationDecisionMonad<TestToggle> monad = AuthorizationDecisionMonad.of(f);
        CompletionListener a = new CompletionListener(r -> logger.info("result is " + r));
        CompletionListener b = new CompletionListener(r -> logger.info("result is failure: "+r));
        CompletionListener c = new CompletionListener(r -> logger.info("did not run: "+r));

        AuthorizationDecisionMonad<Boolean> monad1 = monad.map(TestToggle::wasToggled);
        monad1.onComplete(a);
        monad1.onException(b);
        monad1.onAuthorizationFailure(c);
        AuthorizationDecisionMonad<Boolean> result = monad1.apply(ALLOW);

        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertTrue(result.get());
        assertTrue(a.wasSet());
        assertTrue(result.get());
        a.reset();b.reset();c.reset();
    }

    class TestToggle {

        private boolean toggled = false;
        private Optional<String> throwable = Optional.empty();

        public TestToggle() {
        }

        public TestToggle(String oops) {
            this.throwable = Optional.of(oops);
        }

        public TestToggle toggle() {
            toggled = true;
            throwable.map(msg -> {
                throw new RuntimeException(msg);
            });
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

    class CompletionListener<T> implements Consumer<T> {

        private final Consumer<T> nested;
        private AtomicBoolean latch = new AtomicBoolean(false);

        public CompletionListener(Consumer<T> consumer) {
            this.nested = consumer;
        }

        @Override
        public void accept(T t) {
            this.latch.set(true);
            this.nested.accept(t);
        }

        public boolean wasSet() {
            return latch.get();
        }

        public boolean wasSetAndReset() {
            return latch.getAndSet(false);
        }

        public void reset() {
            latch.set(false);
        }
    }
}
