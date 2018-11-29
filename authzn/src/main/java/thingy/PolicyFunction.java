package thingy;

import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;


public class PolicyFunction implements AuthorizationPolicy, Function<Subject, PolicySubjectContext> {

    PolicyModel ROOT = new PolicyModel();
    private List<Directive> directives = new ArrayList<>();

    @Override
    public Optional<AuthorizationAction> permit(Subject subject, BasePermission permission) {
//        PolicyModel policyModel = ROOT.find(permission).forSubject(subject);
//        return policyModel.implies(permission);
        return Optional.empty();
    }

    public <R> R authorize(Subject subject, BasePermission permission, PrivilegedAction<R> action) throws SecurityException {
        if(permit(subject, permission).map(a -> a.isAllowed()).orElse(false)) {
            return action.run();
        }
        throw new SecurityException("Unauthorized: subject "+subject+" not authorized for permission "+permission);
    }

    @Override
    public PolicySubjectContext apply(Subject subject) {
        return p -> permit(subject, p).map(a -> a.isAllowed()).orElse(false);
    }

    public void add(Directive d) {
        this.directives.add(d);
    }
}
