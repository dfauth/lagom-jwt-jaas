package thingy;

import static thingy.Action.DefaultAction.DEFAULT;

public class RolePermission extends Permission<Action.DefaultAction> {

    public RolePermission(String name) {
        super(name, "/", DEFAULT);
    }

//    @Override
//    protected Action<E> parseAction(String action) {
//        return Action.Actions.from(Action.DefaultAction.class).parser().parseAction(action).orElseThrow(()->new IllegalArgumentException("Oops. No action named "+action));
//    }

    protected Action.Parser<Action.DefaultAction> parser() {
        return Action.Actions.from(Action.DefaultAction.class).parser();
    }

}
