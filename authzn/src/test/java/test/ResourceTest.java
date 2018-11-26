package test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;
import thingy.Directive;
import thingy.DirectiveResource;
import thingy.Resource;
import thingy.ResourceHierarchy;

import java.util.Iterator;
import java.util.Optional;

import static org.testng.Assert.*;
import static org.testng.AssertJUnit.assertFalse;
import static thingy.PrincipalType.ROLE;
import static thingy.SimpleResource.parseResourceString;


public class ResourceTest {

    private static final Logger logger = LoggerFactory.getLogger(ResourceTest.class);

    @Test
    public void testIt() {
        ResourceHierarchy<String, Directive> ROOT = new ResourceHierarchy();
        ROOT.add(
                asResource("/a"),
                asResource("/a/ab"),
                asResource("/a/ab/abc"),
                asResource("/a/ab/abc/resource0"),
                asResource("/a/ab/abc/resource1"),
                asResource("/a/ab/abc/resource2"),
                asResource("/a/ab/abc/resource3"),
                asResource("/a/ab/abd/resource4"),
                asResource("/a/ab/abd/resource5"),
                asResource("/a/ac/abc/resource6"),
                asResource("/a/b/abe/resource7")
                );

        String path = "/a/ab/abc/resource0";
        Optional<Resource<String, Directive>> r = ROOT.findResource(parseResourceString.apply(path));
        assertTrue(r.isPresent());
        assertEquals(r.get().getPath(), path);
        ResourceHierarchy<String, Directive> h = ROOT.findNearest(parseResourceString.apply(path));
        assertNotNull(h);
//        assertEquals(h.getPath(), path);

        path = "/a/b/abc/resourceZ";
        r = ROOT.findResource(parseResourceString.apply(path));
        assertFalse(r.isPresent());
        h = ROOT.findNearest(parseResourceString.apply(path));
        assertNotNull(h);
        assertFalse(h.resource().isPresent());
//        assertEquals(h.resource().get().getPath(), path);

        path = "/a/ac/abc/resource6";
        Iterable<Directive> iterable = ROOT.findAllInPath(parseResourceString.apply(path));
        assertNotNull(iterable);
        Iterator<Directive> it = iterable.iterator();
        assertTrue(it.hasNext());
        Directive next = it.next();
        assertNotNull(next);
        assertEquals(next.getResource(), path);
        assertTrue(it.hasNext());
        next = it.next();
        assertNotNull(next);
        assertEquals(next.getResource(), "/a");
        assertFalse(it.hasNext());

        path = "/a/ab/abc/resource0";
        iterable = ROOT.findAllInPath(parseResourceString.apply(path));
        assertNotNull(iterable);
        it = iterable.iterator();
        assertTrue(it.hasNext());
        next = it.next();
        assertNotNull(next);
        assertEquals(next.getResource(), path);

        assertTrue(it.hasNext());
        next = it.next();
        assertNotNull(next);
        assertEquals(next.getResource(), "/a/ab/abc");
        assertTrue(it.hasNext());

        assertTrue(it.hasNext());
        next = it.next();
        assertNotNull(next);
        assertEquals(next.getResource(), "/a/ab");
        assertTrue(it.hasNext());

        assertTrue(it.hasNext());
        next = it.next();
        assertNotNull(next);
        assertEquals(next.getResource(), "/a");
        assertFalse(it.hasNext());

        path = "/c/ab/abc/resource0";
        iterable = ROOT.findAllInPath(parseResourceString.apply(path));
        assertNotNull(iterable);
        it = iterable.iterator();
        assertFalse(it.hasNext());

    }

    private DirectiveResource asResource(String resource) {
        Directive directive = new Directive("domain", ROLE.of("user"), resource);
        return new DirectiveResource(directive);
    }

}
