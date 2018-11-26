package thingy;

public interface AuthorizationPermitter {
    boolean permit(Subject subject, BasePermission permission);
}
