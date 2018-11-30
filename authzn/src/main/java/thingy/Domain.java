package thingy;

import java.util.Arrays;
import java.util.function.Function;
import java.util.stream.Collectors;

public interface Domain {

    String getName();

    class DomainResource extends Resource<String, Domain> {

        public static Function<String, Iterable<String>> parseResourceString = p -> Arrays.asList(p.split("\\.")).stream().filter(s -> s.trim().length()>0).collect(Collectors.toSet());

        public DomainResource(Domain domain) {
            super(domain.getName(), parseResourceString, domain);
        }
    }

}

