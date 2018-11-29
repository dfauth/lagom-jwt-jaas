package thingy;

import java.util.Optional;

public interface AuthorizationPermitter {
    AuthorizationAction permit(Subject subject, Permission permission);
}
