package thingy;

public interface Principal extends java.security.Principal {
    PrincipalType getType();
    String getSource();
    String getName();
}

