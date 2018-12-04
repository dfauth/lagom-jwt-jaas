package thingy;


public interface AuthorizationPolicy {
    <E extends Enum<E> & Action<E>> AuthorizationDecision permit(Subject subject, Permission<E> permission);
}

