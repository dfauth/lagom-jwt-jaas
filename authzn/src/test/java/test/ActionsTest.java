package test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;
import thingy.Action;
import thingy.Actions;

import java.util.Collections;
import java.util.Set;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;


public class ActionsTest {

    private static final Logger logger = LoggerFactory.getLogger(ActionsTest.class);

    @Test
    public void testImplies() {
        {
            Actions<TestAction> actions = Actions.of(TestAction.class);
            assertTrue(actions.implies(TestAction.READ));
            assertFalse(Actions.<TestAction>of(Collections.emptySet()).implies(TestAction.READ));
        }

        {
            Actions<CrudAction> actions = Actions.of(CrudAction.class);
            assertTrue(actions.implies(CrudAction.READ));
            assertFalse(Actions.<CrudAction>of(Collections.emptySet()).implies(CrudAction.READ));

            actions = Actions.of(CrudAction.READ);
            assertTrue(actions.implies(CrudAction.READ));
            assertFalse(actions.implies(CrudAction.CREATE));
            assertFalse(actions.implies(CrudAction.UPDATE));
            assertFalse(actions.implies(CrudAction.DELETE));

            actions = Actions.of(CrudAction.CREATE);
            assertTrue(actions.implies(CrudAction.READ));
            assertTrue(actions.implies(CrudAction.CREATE));
            assertFalse(actions.implies(CrudAction.UPDATE));
            assertFalse(actions.implies(CrudAction.DELETE));

            actions = Actions.of(CrudAction.UPDATE);
            assertTrue(actions.implies(CrudAction.READ));
            assertTrue(actions.implies(CrudAction.UPDATE));
            assertFalse(actions.implies(CrudAction.CREATE));
            assertFalse(actions.implies(CrudAction.DELETE));

            actions = Actions.of(CrudAction.DELETE);
            assertTrue(actions.implies(CrudAction.READ));
            assertTrue(actions.implies(CrudAction.DELETE));
            assertFalse(actions.implies(CrudAction.CREATE));
            assertFalse(actions.implies(CrudAction.UPDATE));
        }
    }

    @Test
    public void testActions() {

        Actions.Parser<TestAction> parser = Actions.of(TestAction.class).parser();
        {
            Set actions = parser.parseActions("");
            assertTrue(actions.isEmpty());
            assertTrue(actions.size() == 0);
            assertFalse(actions.contains(TestAction.READ));
            assertFalse(actions.contains(TestAction.WRITE));
            assertFalse(actions.contains(TestAction.EXECUTE));
        }

        {
            Set actions = parser.parseActions("read");
            assertFalse(actions.isEmpty());
            assertTrue(actions.size() == 1);
            assertTrue(actions.contains(TestAction.READ));
            assertFalse(actions.contains(TestAction.WRITE));
            assertFalse(actions.contains(TestAction.EXECUTE));
        }

        {
            Set actions = parser.parseActions("read, write, blah");
            assertFalse(actions.isEmpty());
            assertTrue(actions.size() == 2);
            assertTrue(actions.contains(TestAction.READ));
            assertTrue(actions.contains(TestAction.WRITE));
            assertFalse(actions.contains(TestAction.EXECUTE));
        }

        {
            Set actions = parser.parseActions("read, write, *, blah");
            assertFalse(actions.isEmpty());
            assertTrue(actions.size() == 3);
            assertTrue(actions.contains(TestAction.READ));
            assertTrue(actions.contains(TestAction.WRITE));
            assertTrue(actions.contains(TestAction.EXECUTE));
        }

    }

    public static enum TestAction implements Action<TestAction> {
        READ, WRITE, EXECUTE;
    }

    public static enum CrudAction implements Action<CrudAction> {
        CREATE(2), READ(1), UPDATE(2), DELETE(2);

        private int value;

        CrudAction(int value) {
            this.value = value;
        }

        @Override
        public <E extends Enum<E> & Action<E>> boolean implies(Action<E> action) {
            return Action.super.implies(action) || this.value > ((CrudAction)action).value;
        }
    }
}
