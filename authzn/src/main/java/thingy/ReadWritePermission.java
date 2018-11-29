package thingy;

import java.util.Optional;

public class ReadWritePermission extends Permission<ReadWritePermission.ReadableWritableActions> {

    protected ReadWritePermission(String name, String resource) {
        super(name, resource);
    }

    public ReadWritePermission(String name, String resource, String action) {
        super(name, resource, action);
    }

    public ReadWritePermission(String name, String resource, ReadableWritableActions action) {
        super(name, resource, action);
    }

    protected ReadableWritableActions parseAction(String action) {
        return Action.Actions.from(ReadableWritableActions.class).parser().parseAction(action).orElseThrow(()-> new IllegalArgumentException("Oops. No action named "+action));
    }

    @Override
    protected Action.Parser<ReadableWritableActions> parser() {
        return Action.Actions.from(ReadableWritableActions.class).parser();
    }

    enum ReadableWritableActions implements Action<ReadableWritableActions> {
        READ, WRITE;
    }
}
