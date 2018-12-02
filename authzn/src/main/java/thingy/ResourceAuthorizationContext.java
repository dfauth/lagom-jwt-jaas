package thingy;

public interface ResourceAuthorizationContext {

    <E extends Enum<E> & Action<E>> ActionResourceAuthorizationContext forAction(E action);
}
