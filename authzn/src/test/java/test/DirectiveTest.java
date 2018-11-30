package test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;
import thingy.Directive;
import thingy.DirectiveBuilder;
import thingy.Domain;
import thingy.ResourceHierarchy;

import java.io.IOException;
import java.util.Iterator;
import java.util.function.Consumer;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;
import static org.testng.AssertJUnit.assertEquals;
import static thingy.PrincipalType.USER;


public class DirectiveTest {

    private static final Logger logger = LoggerFactory.getLogger(DirectiveTest.class);

    @Test
    public void testIt() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            String value = mapper.writeValueAsString(new Directive(USER.of("fred")));
            logger.info("directive: "+value);
            DirectiveBuilder builder = mapper.readValue(value, DirectiveBuilder.class);
            logger.info("builder: "+builder);
            Directive directive = builder.build();
            assertEquals(directive.getResource(), "/");
            assertTrue(directive.getPrincipals().contains(USER.of("fred")));
        } catch (JsonProcessingException e) {
            logger.error(e.getMessage(), e);
            throw new RuntimeException(e);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testDomainResources() {

        TestDomain.A.getName();
        ResourceHierarchy g = HIERARCHY.findNearest(thingy.Domain.DomainResource.parseResourceString.apply("aa.bb.ff"));
        assertNotNull(g);
        assertNotNull(g.resource());
        assertTrue(g.resource().isPresent());

        Iterable<Domain> h = HIERARCHY.findAllInPath(Domain.DomainResource.parseResourceString.apply("aa.bb.ff"));
        assertNotNull(h);
        Iterator<Domain> it = h.iterator();
        assertNotNull(it);
        assertTrue(it.hasNext());
        Domain next = it.next();
        assertNotNull(next);
        assertEquals(next.getName(), "aa.bb");

        assertTrue(it.hasNext());
        next = it.next();
        assertNotNull(next);
        assertEquals(next.getName(), "aa");

        assertFalse(it.hasNext());
    }

    public static ResourceHierarchy<String, Domain> HIERARCHY = new ResourceHierarchy<String, Domain>();

    enum TestDomain implements Domain {

        A("aa", r -> HIERARCHY.add(new DomainResource(r))),
        AB("aa.bb", r -> HIERARCHY.add(new DomainResource(r))),
        ABC("aa.bb.cc", r -> HIERARCHY.add(new DomainResource(r))),
        ABD("aa.bb.dd", r -> HIERARCHY.add(new DomainResource(r))),
        ABE("aa.bb.ee", r -> HIERARCHY.add(new DomainResource(r))),
        AAC("aa.aa.cc", r -> HIERARCHY.add(new DomainResource(r))),
        AAE("aa.aa.ee", r -> HIERARCHY.add(new DomainResource(r)))
        ;

        private final String name;

        TestDomain(String name) {
            this(name, r -> {});
        }

        public String getName() {
            return name;
        }

        TestDomain(String name, Consumer<Domain> consumer) {
            this.name = name;
            consumer.accept(this);
        }
    }

}
