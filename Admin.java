
public class Admin extends User {
    public Admin(String username, String password, String realname) {
        super(username, password, realname);
    }

    public void recordmoney(Client user, int money) {
        user.addmoney(money);
    }

}