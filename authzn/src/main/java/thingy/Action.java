package thingy;

public interface Action<E extends Enum<E>> {

    String ALL = "*";

    default <E extends Enum<E> & Action<E>> boolean implies(Action<E> action) {
        return this.equals(action);
    }

}
