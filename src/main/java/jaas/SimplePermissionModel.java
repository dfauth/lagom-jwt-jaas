package jaas;

import java.security.Principal;

class SimplePermissionModel implements PermissionModel {

    private Resource resource;
    private String action;

    public SimplePermissionModel() {
        this(Resource.ROOT, "*");
    }

    public SimplePermissionModel(Resource resource) {
        this(resource, "*");
    }

    public SimplePermissionModel(Resource resource, String action) {
        this.resource = resource;
        this.action = action;
    }

    @Override
    public boolean test(Principal p) {
        return resource.test(action, p);
    }

    @Override
    public PermissionModel withAction(String action) {
        this.action = action;
        return this;
    }
}
