package thingy;

import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.List;

import static thingy.AuthorizationDecision.DENY;


public class PolicyFunction implements AuthorizationPolicy {

    PolicyModel ROOT = new PolicyModel();
    private List<Directive> directives = new ArrayList<>();

    @Override
    public AuthorizationDecision permit(Subject subject, Permission permission) {
//        PolicyModel policyModel = ROOT.find(permission).forSubject(subject);
//        return policyModel.implies(permission);
        return DENY;
    }

    public <R> R authorize(Subject subject, ReadWritePermission permission, PrivilegedAction<R> action) throws SecurityException {
        if(permit(subject, permission).isAllowed()) {
            return action.run();
        }
        throw new SecurityException("Unauthorized: subject "+subject+" not authorized for permission "+permission);
    }

    public void add(Directive d) {
        this.directives.add(d);
    }
}
