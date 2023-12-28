
import java.util.*;

public class Client extends User {
    protected Integer money;// 存款

    public Client(String username, String password, String realname) {
        super(username, password, realname);//
        this.money = Integer.valueOf(0);
    }

    public void addmoney(int money) {// 存款
        this.money += money;
    }

    public Integer getmoney() {// 取得存款
        return this.money;
    }

}
