package thingy;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;

public class Resource<K,V> {

    private final String key;
    private final Iterable<K> path;
    protected final V payload;

    public Resource(String path, Function<String, Iterable<K>> parser, V payload) {
        this.key = path;
        this.path = parser.apply(path);
        this.payload = payload;
    }

    public Iterable<K> getIterablePath() {
        return path;
    }

    public String getPath() {
        return key;
    }

    public void walkPath(Consumer<K> consumer) {
        Iterator<K> it = getIterablePath().iterator();
        while(it.hasNext()) {
            consumer.accept(it.next());
        }
    }

    public Optional<V> getPayload() {
        return Optional.ofNullable(payload);
    }

}
