package test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.Test;
import thingy.Action;
import thingy.Actions;

import java.util.Set;

import static org.testng.Assert.assertTrue;


public class ActionsTest {

    private static final Logger logger = LoggerFactory.getLogger(ActionsTest.class);

    @Test
    public void testImplies() {
        Actions<TestAction> actions = Actions.of(TestAction.class);
        Actions<TestAction> tmp = Actions.of(actions.values());
        assertTrue(tmp.implies(TestAction.READ));
    }

    @Test
    public void testActions() {

        Actions.Parser<TestAction> parser = Actions.of(TestAction.class).parser();
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
