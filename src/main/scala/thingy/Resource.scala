package thingy

import java.security.Principal

import scala.collection.SortedSet
import scala.collection.mutable.ListBuffer

object Resource {
   val ROOT = Resource("ROOT")
}

case class Resource(name:String, var parent:Option[Resource] = None, principals:ListBuffer[Principal] = ListBuffer(), actions:ListBuffer[String] = ListBuffer(), nested:Map[String, Resource] = Map()) {

  def byPrincipal(principal:String*):Resource = {
    principal.map(p => SimplePrincipal(p)).foreach(p => principals += p)
    this
  }

  def permitsActions(action:String*): Resource = {
    action.foreach(a => actions += a)
    this
  }

  def resource(name:String):Resource = ???
//    if(this.nested.containsKey(name)) {
//      return this.nested.get(name);
//    }
//    Resource r = new Resource(name, this);
//    this.nested.put(name, r);
//    return r;
//  }

  def find(resource:String):PermissionModel = ???
//    return find(new TreeSet(Arrays.asList(resource.split("[/ | \\.]"))));
//  }

  def find(resource:SortedSet[String]):PermissionModel = ???
//    if(resource.isEmpty()) return new SimplePermissionModel();
//    Iterator<String> it = resource.iterator();
//    String head = it.next();
//    if(it.hasNext()) {
//      SortedSet<String> tail = resource.tailSet(it.next());
//      return resource(head).find(tail);
//    }
//    return new SimplePermissionModel(resource(head), null);
//  }

  def test(action:String, p:Principal):Boolean = ???
//    return ("*".equals(action) || this.actions.contains(action)) && this.principals.contains(p) || (this.parent != null && this.parent.test(action, p));
//  }
}

/**

public static final Resource ROOT = new Resource();

    private final String name;
    private Resource parent;
    private Set<Principal> principals = new HashSet<>();
    private Set<String> actions = new HashSet<>();
    private Map<String,Resource> nested = new HashMap<>();

    private Resource() {
        this("ROOT");
    }

    private Resource(String name) {
        this(name, null);
    }

    private Resource(String name, Resource parent) {
        this.name = name;
        this.parent = parent;
    }

    Resource byPrincipal(String... principal) {
        this.principals.addAll(Arrays.asList(principal).stream().map(p -> new SimplePrincipal(p)).collect(Collectors.toList()));
        return this;
    }

    Resource permitsActions(String... actions) {
        this.actions.addAll(Arrays.asList(actions));
        return this;
    }

    Resource resource(String name) {
        if(this.nested.containsKey(name)) {
            return this.nested.get(name);
        }
        Resource r = new Resource(name, this);
        this.nested.put(name, r);
        return r;
    }

    public PermissionModel find(String resource) {
        return find(new TreeSet(Arrays.asList(resource.split("[/ | \\.]"))));
    }

    public PermissionModel find(SortedSet<String> resource) {
        if(resource.isEmpty()) return new SimplePermissionModel();
        Iterator<String> it = resource.iterator();
        String head = it.next();
        if(it.hasNext()) {
            SortedSet<String> tail = resource.tailSet(it.next());
            return resource(head).find(tail);
        }
        return new SimplePermissionModel(resource(head), null);
    }

    public boolean test(String action, Principal p) {
        return ("*".equals(action) || this.actions.contains(action)) && this.principals.contains(p) || (this.parent != null && this.parent.test(action, p));
    }



  */
