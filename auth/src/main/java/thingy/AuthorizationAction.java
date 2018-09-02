package thingy;

import java.util.function.BiConsumer;

public enum AuthorizationAction {
    GRANT((p,g) -> p.add(g)),
    REVOKE((p,g) -> p.revoke(g));

    private final BiConsumer<PolicyService, Directive> f;

    AuthorizationAction(BiConsumer<PolicyService, Directive> f) {
        this.f = f;
    }

    public void apply(PolicyService policyService, Directive grant) {
        this.f.accept(policyService, grant);
    }
}
