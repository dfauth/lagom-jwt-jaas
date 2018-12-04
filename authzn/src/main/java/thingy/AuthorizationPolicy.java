package thingy;


public interface AuthorizationPolicy {
    <E extends Enum<E> & Action<E>> AuthorizationAction permit(Subject subject, Permission<E> permission);
}

