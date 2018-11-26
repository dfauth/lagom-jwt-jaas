package thingy;

import java.security.PrivilegedAction;


public interface PriviledgedActionAuthorizer {
    <R> R authorize(Subject subject, BasePermission permission, PrivilegedAction<R> action) throws SecurityException;
}
