package thingy;

import java.security.PrivilegedAction;


public interface PriviledgedActionAuthorizer {
    <R> R authorize(Subject subject, ReadWritePermission permission, PrivilegedAction<R> action) throws SecurityException;
}
