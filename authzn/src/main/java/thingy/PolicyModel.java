package thingy;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


public class PolicyModel {

    Map<String, PolicyModel> children = new HashMap<>();

    public PolicyModel find(BasePermission permission) {
        Iterator<String> it = Arrays.asList(permission.getDomain().split("\\.")).iterator();
        return find(it);
    }

    private PolicyModel find(Iterator<String> it) {
        if(it.hasNext()) {
            String key = it.next();
            PolicyModel policyModel = children.get(key);
            if(policyModel != null) {
                return policyModel.find(it);
            }
        }
        return this;
    }

    public boolean implies(BasePermission permission) {
        return false;
    }

    public PolicyModel forSubject(Subject subject) {
        return subject.getPrincipals().stream().reduce(new PolicyModel(), (m, p) -> m.forPrincipal(p), (m1, m2) -> m1.combine(m2));
    }

    public PolicyModel combine(PolicyModel model) {
        return model;
    }

    public PolicyModel forPrincipal(Principal principal) {
        return this;
    }
}
