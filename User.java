
public class User {
    private String username;
    private String password;
    private String realname;

    public User(String username, String password, String realname) {
        this.username = username;
        this.password = password;
        this.realname = realname;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getRealname() {
        return realname;
    }

    public boolean checkPassword(String password) {
        return this.password.equals(password);
    }
}