package thingy;

import java.util.Set;

public class ReadWritePermission extends ActionPermission<ReadWritePermission.ReadableWritableActions> {

    public ReadWritePermission(String name, String resource, ReadableWritableActions action) {
        super(name, resource, action);
    }

    protected Set<ReadableWritableActions> parseActions(String action) {
        return Actions.of(ReadableWritableActions.class).parser().parseActions(action);
    }

    @Override
    protected Actions.Parser<ReadableWritableActions> parser() {
        return Actions.of(ReadableWritableActions.class).parser();
    }

    public enum ReadableWritableActions implements Action<ReadableWritableActions> {
        READ, WRITE;
    }
}
