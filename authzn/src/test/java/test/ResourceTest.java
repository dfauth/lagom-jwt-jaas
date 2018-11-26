package test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.Test;
import thingy.Directive;
import thingy.DirectiveResource;
import thingy.Resource;
import thingy.ResourceHierarchy;

import java.util.Iterator;
import java.util.Optional;

import static thingy.PrincipalType.ROLE;
import static thingy.SimpleResource.parseResourceString;


public class ResourceTest {

    private static final Logger logger = LoggerFactory.getLogger(ResourceTest.class);

    @Test
    public void testIt() {
        ResourceHierarchy<String, Directive> ROOT = new ResourceHierarchy();
        ROOT.add(
                asResource("/a"),
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
        Assert.assertTrue(r.isPresent());
        Assert.assertEquals(r.get().getPath(), path);
        ResourceHierarchy<String, Directive> h = ROOT.findNearest(parseResourceString.apply(path));
        Assert.assertNotNull(h);
//        Assert.assertEquals(h.getPath(), path);

        path = "/a/b/abc/resourceZ";
        r = ROOT.findResource(parseResourceString.apply(path));
        Assert.assertFalse(r.isPresent());
        h = ROOT.findNearest(parseResourceString.apply(path));
        Assert.assertNotNull(h);
        Assert.assertFalse(h.resource().isPresent());
//        Assert.assertEquals(h.resource().get().getPath(), path);

        path = "/a/ac/abc/resource6";
        Iterable<Directive> iterable = ROOT.findAllInPath(parseResourceString.apply(path));
        Assert.assertNotNull(iterable);
        Iterator<Directive> it = iterable.iterator();
        Assert.assertTrue(it.hasNext());
        it.next();

    }

    private DirectiveResource asResource(String resource) {
        Directive directive = new Directive("domain", ROLE.of("user"), resource);
        return new DirectiveResource(directive);
    }

}
