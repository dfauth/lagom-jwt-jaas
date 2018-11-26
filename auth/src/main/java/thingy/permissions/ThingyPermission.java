package thingy.permissions;

import java.security.BasicPermission;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


public class ThingyPermission extends BasicPermission {

    private final String resource;
    private final List<String> actions;

    public ThingyPermission(String name, String resource, String actions) {
        super(name, actions);
        this.resource = resource;
        this.actions = Arrays.stream(actions.split(",")).map(a -> a.trim()).collect(Collectors.toList());
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
            ThingyPermission other = getClass().cast(obj);
            return this.resource.equals(other.resource) && this.actions.equals(other.actions);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return resource.hashCode() | actions.hashCode();
    }
}
