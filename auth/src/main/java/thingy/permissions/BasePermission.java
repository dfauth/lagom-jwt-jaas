package thingy.permissions;

import java.security.BasicPermission;


public class BasePermission extends BasicPermission {

    private final String resource;

    public BasePermission(String name, String resource, String actions) {
        super(name, actions);
        this.resource = resource;
    }

    public String getResource() {
        return resource;
    }
}
