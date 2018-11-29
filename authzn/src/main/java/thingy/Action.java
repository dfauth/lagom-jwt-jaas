package thingy;

import org.apache.log4j.LogManager;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

public interface Action<E extends Enum<E>> {

    String ALL = "*";

    class Parser<E extends Enum<E>>{

        private final Set<E> actions;

        public Parser(Set<E> actions) {
            this.actions = actions;
        }

        public Set<E> parseActions(String str) {
            return Arrays.stream(str.split(",")).flatMap(s -> getActions(s.trim()).stream()).collect(Collectors.toSet());
        }

        public Optional<E> parseAction(String str) {
            return getAction(str.trim());
        }

        private Optional<E> getAction(String action) {
            return actions.stream().filter(a -> a.name().equalsIgnoreCase(action)).findFirst();
        }

        public Set<E> getActions(String action) {
            if(ALL.equalsIgnoreCase(action)) {
                return actions;
            }
            return getAction(action).map(a -> Collections.singleton(a)).orElse(Collections.emptySet());
        }
    }

    class Actions<E extends Enum<E>> {

        private static final org.apache.log4j.Logger logger = LogManager.getLogger(Actions.class);
        private final Set<E> actions;

        public static <E extends Enum<E>> Actions<E> from(Class<E> clazz) {
            return new Actions<E>(clazz);
        }

        private Actions(Class<E> clazz) {
            try {
                actions = new HashSet<>(Arrays.asList((E[])clazz.getMethod("values", new Class[]{}).invoke(clazz,new Object[]{})));
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

        public Set<E> allActions() {
            return actions;
        }

        public Parser<E> parser() {
            return new Parser(actions);
        }

    }

    enum DefaultAction implements Action<DefaultAction> {
        DEFAULT;
    }

}
