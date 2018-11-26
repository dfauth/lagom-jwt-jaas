package thingy;

public class RolePermission extends BasePermission {

    public RolePermission(String name) {
        super(name, "*", "*");
    }
}
