package thingy

import java.security.Principal

import scala.collection.SortedSet
import scala.collection.mutable.ListBuffer

object Resource {
   val ROOT = Resource("ROOT")
}

case class Resource(name:String, var parent:Option[Resource] = None, principals:ListBuffer[Principal] = ListBuffer(), actions:ListBuffer[String] = ListBuffer(), nested:scala.collection.mutable.Map[String, Resource] = scala.collection.mutable.Map()) {

  def byPrincipal(principal:String*):Resource = {
    principal.map(p => SimplePrincipal(p)).foreach(p => principals += p)
    this
  }

  def permitsActions(action:String*): Resource = {
    action.foreach(a => actions += a)
    this
  }

  def resource(name:String):Resource = {
    if(nested.contains(name)) {
      nested(name)
    } else {
      val r = Resource(name, Option(this))
      nested.put(name, r)
      r
    }
  }
//    if(this.nested.containsKey(name)) {
//      return this.nested.get(name);
//    }
//    Resource r = new Resource(name, this);
//    this.nested.put(name, r);
//    return r;
//  }

  def find(resource:String):PermissionModel = {
    find(SortedSet(resource.split("[/ | \\.]").toList :_*))
  }
//    return find(new TreeSet(Arrays.asList(resource.split("[/ | \\.]"))));
//  }

  def find(resource:SortedSet[String]):PermissionModel = {
    // root resource
        if(resource.isEmpty) {
          SimplePermissionModel()
        } else if(resource.tail.isEmpty) {
          SimplePermissionModel(this.resource(resource.head))
        } else {
          this.resource(resource.head).find(resource.tail)
        }
  }
//    if(resource.isEmpty()) return new SimplePermissionModel();
//    Iterator<String> it = resource.iterator();
//    String head = it.next();
//    if(it.hasNext()) {
//      SortedSet<String> tail = resource.tailSet(it.next());
//      return resource(head).find(tail);
//    }
//    return new SimplePermissionModel(resource(head), null);
//  }

  def test(action:String, p:Principal):Boolean = {
    (("*".equals(action) || actions.contains(action)) && principals.contains(p)) || parent.map(r => r.test(action, p)).getOrElse(false)
  }
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
