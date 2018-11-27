package thingy;

public interface Principal extends java.security.Principal {
    PrincipalType getPrincipalType();
    String getSource();
    String getName();
}

