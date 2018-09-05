package thingy.permissions;

import java.security.BasicPermission;
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

    public String getResource() {
        return resource;
    }

    @Override
    public String getActions() {
        return actions.stream().collect(Collectors.joining(","));
    }
}
