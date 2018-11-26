package thingy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;


public interface Subject {
    Set<Principal> getPrincipals();
}
