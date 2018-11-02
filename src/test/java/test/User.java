package test;

public class User {
    public int id;
    public String firstName;
    public String lastName;
    public String email;
    public String username;
    public String password;

    public static class Role {
        public Role() {
        }

        public Role(String roleName, String description) {
            this.roleName = roleName;
            this.description = description;
        }

        public int id;
        public String roleName;
        public String description;
    }
}

