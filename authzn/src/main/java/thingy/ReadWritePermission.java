package thingy;

public class ReadWritePermission extends ActionPermission<ReadWritePermission.ReadableWritableActions> {

    public ReadWritePermission(String name, String resource, ReadableWritableActions action) {
        super(name, resource, action);
    }

    protected ReadableWritableActions parseAction(String action) {
        return Actions.of(ReadableWritableActions.class).parser().parseAction(action).orElseThrow(()-> new IllegalArgumentException("Oops. No action named "+action));
    }

    @Override
    protected Actions.Parser<ReadableWritableActions> parser() {
        return Actions.of(ReadableWritableActions.class).parser();
    }

    public enum ReadableWritableActions implements Action<ReadableWritableActions> {
        READ, WRITE;
    }
}
