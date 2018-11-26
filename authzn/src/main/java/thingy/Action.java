package thingy;

import org.apache.log4j.LogManager;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public interface Action<E extends Enum<E>> {

    String ALL = "*";

    String name();

    class Parser<E extends Enum<E>>{
        private final Set<Action<E>> actions;

        public Parser(Action<E>[] actions) {
            this.actions = new HashSet<>(Arrays.asList(actions));
        }

        public Actions parse(String str) {
            return new Actions(Arrays.stream(str.split(",")).flatMap(s -> getActions(s.trim()).stream()).collect(Collectors.toSet()));
        }

        private Set<Action<E>> getActions(String action) {
            if(ALL.equalsIgnoreCase(action)) {
                return actions;
            }
            return actions.stream().filter(a -> a.name().equalsIgnoreCase(action)).collect(Collectors.toSet());
        }
    }

    class Actions<E extends Action> {

        private static final org.apache.log4j.Logger logger = LogManager.getLogger(Actions.class);
        private final Set<E> actions;

        public Actions(Class<E> clazz) {
            try {
                actions = new HashSet<>(Arrays.asList((E[])clazz.getMethod("values").invoke(clazz,new Object[]{0})));
            } catch (IllegalAccessException e) {
                logger.error(e.getMessage(), e);
                throw new RuntimeException(e);
            } catch (InvocationTargetException e) {
                logger.error(e.getMessage(), e);
                throw new RuntimeException(e);
            } catch (NoSuchMethodException e) {
                logger.error(e.getMessage(), e);
                throw new RuntimeException(e);
            }
        }

        public Actions(Set<E> actions) {
            this.actions = actions;
        }

        public Set<E> actions() {
            return actions;
        }

        public boolean implies(String actions) {
            return false;
        }
    }
}
