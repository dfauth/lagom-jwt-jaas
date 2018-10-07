package test

//object Configurations {
//  var basicClientWithWebSocket: Function[AutomationContext, Automat] =
//    (c: AutomationContext) => c.onRequest
//      .apply(authHandler)
//      .onResponse
//      .apply(
//        forHttpCode(403)
//          .use(
//            loginHandler(AUTH)
//              .andThen(storeToken)
//              .andThen(
//                subscribeTo(
//                  SUBSCRIPTION,
//                  heartbeatConsumer(c.queue)
//                ) // subscribeTo
//              )// andThen
//          )// use
//      )// apply
//}
//
