package thingy.permissions;

import java.security.BasicPermission;
import java.security.Permission;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


public class BasePermission extends BasicPermission {

    private final String resource;
    private final List<String> actions;

    public BasePermission(String name, String resource, String actions) {
        super(name, actions);
        this.resource = resource;
        this.actions = Arrays.stream(actions.split(",")).map(a -> a.trim()).collect(Collectors.toList());
    }

    @Override
    public boolean implies(Permission p) {
        return equals(p) && accept();
    }

    /**
     * callback to allow implementation of custom logic modifying central policy decision.
     * ie. include here any addition logic outside the permission/resource/action/principal model necessary to authorize
     * the action on the resource
     *
     * @return boolean
     */
    public boolean accept() {
        return true;
    }

    public String getResource() {
        return resource;
    }

    @Override
    public String getActions() {
        return actions.stream().collect(Collectors.joining(","));
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == null) {
            return false;
        }
        if(obj == this) {
            return true;
        }
        if(getClass().equals(obj.getClass())) {
            BasePermission other = getClass().cast(obj);
            return this.resource.equals(other.resource) && this.actions.equals(other.actions);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return resource.hashCode() | actions.hashCode();
    }
}
