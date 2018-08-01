package jaas;

import java.security.Principal;

interface PermissionModel {
    boolean test(Principal p);

    PermissionModel withAction(String action);
}
