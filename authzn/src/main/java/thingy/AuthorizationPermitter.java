package thingy;

public interface AuthorizationPermitter {
    <E extends Enum<E> & Action<E>> AuthorizationAction permit(Subject subject, Permission<E> permission);
}
