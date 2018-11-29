package thingy;

public interface ResourceAuthorizationContext {

    <E extends Enum<E>> ActionResourceAuthorizationContext forAction(E action);
}
