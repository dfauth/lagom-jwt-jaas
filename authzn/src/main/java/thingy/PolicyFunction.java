package thingy;

import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;


public class PolicyFunction implements AuthorizationPolicy, Function<Subject, PolicySubjectContext> {

    PolicyModel ROOT = new PolicyModel();
    private List<Directive> directives = new ArrayList<>();

    @Override
    public boolean permit(Subject subject, BasePermission permission) {
        PolicyModel policyModel = ROOT.find(permission).forSubject(subject);
        return policyModel.implies(permission);
    }

    public <R> R authorize(Subject subject, BasePermission permission, PrivilegedAction<R> action) throws SecurityException {
        if(permit(subject, permission)) {
            return action.run();
        }
        throw new SecurityException("Unauthorized: subject "+subject+" not authorized for permission "+permission);
    }

    @Override
    public PolicySubjectContext apply(Subject subject) {
        return p -> permit(subject, p);
    }

    public void add(Directive d) {
        this.directives.add(d);
    }
}
