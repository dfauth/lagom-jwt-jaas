package thingy;

import java.util.function.BiConsumer;

public enum AuthorizationAction {
    GRANT((p,g) -> p.add(g)),
    REVOKE((p,g) -> p.revoke(g));

    private final BiConsumer<PolicyService,Grant> f;

    AuthorizationAction(BiConsumer<PolicyService,Grant> f) {
        this.f = f;
    }

    public void apply(PolicyService policyService, Grant grant) {
        this.f.accept(policyService, grant);
    }
}
