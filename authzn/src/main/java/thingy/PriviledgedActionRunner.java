package thingy;

import java.security.PrivilegedAction;


public interface PriviledgedActionRunner {
    <R> R run(PrivilegedAction<R> action) throws SecurityException;
}
