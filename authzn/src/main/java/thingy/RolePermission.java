package thingy;

import static thingy.AnyAction.ANY_ACTION;

public class RolePermission extends Permission<AnyAction> {

    public RolePermission(String name) {
        super(name, "/", ANY_ACTION);
    }

//    @Override
//    protected Action<E> parseAction(String action) {
//        return Action.Actions.from(Action.DefaultAction.class).parser().parseAction(action).orElseThrow(()->new IllegalArgumentException("Oops. No action named "+action));
//    }

    protected Actions.Parser<AnyAction> parser() {
        return Actions.of(AnyAction.class).parser();
    }

}
