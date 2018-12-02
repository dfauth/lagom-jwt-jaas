package thingy;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Actions<E extends Enum<E> & Action<E>> {

    private final Set<Action<E>> actions;

    public static <E extends Enum<E> & Action<E>> boolean implies(Set<Action<E>> actions, Action<E> action) {
        return implies(actions).apply(action);
    }

    public static <E extends Enum<E> & Action<E>> Function<Action<E>, Boolean> implies(Set<Action<E>> actions) {
        return action -> actions.stream().filter(a -> a.implies(action)).findFirst().isPresent();
    }

    public static <E extends Enum<E> & Action<E>> Actions<E> of(Class<E> clazz) {
        return new Actions(clazz.getEnumConstants());
    }

    public static <E extends Enum<E> & Action<E>> Actions<E> of(E... actions) {
        return new Actions(actions);
    }

    public static <E extends Enum<E> & Action<E>> Actions<E> of(Set<Action<E>> actions) {
        return new Actions(actions);
    }

    private Actions(E[] actions) {
        this(new HashSet(Arrays.asList(actions)));
    }

    private Actions(Set<Action<E>> actions) {
        this.actions = actions;
    }

    public Parser<E> parser() {
        return new Parser(actions);
    }

    public Set<Action<E>> values() {
        return actions;
    }

    public boolean implies(Action<E> action) {
        return implies(this.actions, action);
    }

    public static class Parser<E extends Enum<E>>{

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
            if(Action.ALL.equalsIgnoreCase(action)) {
                return actions;
            }
            return getAction(action).map(a -> Collections.singleton(a)).orElse(Collections.emptySet());
        }
    }
}
