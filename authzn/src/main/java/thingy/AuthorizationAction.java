package thingy;

public enum AuthorizationAction {
    GRANT, //((p,g) -> p.add(g)),
    REVOKE; //((p,g) -> p.revoke(g));

    public boolean isAllowed() {
        return this == GRANT;
    }
}
