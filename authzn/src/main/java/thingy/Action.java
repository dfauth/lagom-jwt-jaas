package thingy;

public interface Action<E extends Enum<E>> {

    String ALL = "*";

    default boolean implies(Action<E> action) {
        return this.equals(action);
    }

}
