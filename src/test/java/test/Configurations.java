package test;

import automat.Automat;
import automat.AutomationContext;

import java.util.function.Function;

import static automat.Automat.Utils.forHttpCode;
import static automat.Functions.*;
//import static test.TestResource$.MODULE$.*;

public class Configurations {

    public static Function<AutomationContext, Automat> basicClientWithWebSocket = c -> c.onRequest().
            apply(authHandler).
            onResponse().
            apply(
                    forHttpCode(401).
                            use(
                                    loginHandler(TestResource$.MODULE$.AUTH())
                                            .andThen(storeToken)
                                            .andThen(subscribeTo(
                                                    TestResource$.MODULE$.SUBSCRIPTION(),
                                                    heartbeatConsumer(c.queue())
                                                    ) // subscribeTo
                                            ) // andThen
                            ) // use
            );// apply;
}
