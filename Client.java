
import java.util.*;
import java.io.File;
import java.io.FileNotFoundException;

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

    public String getTransactionDetails() {
        String filePath = "transation_details.txt";
        StringBuilder details = new StringBuilder();

        try {
            File file = new File(filePath);
            Scanner scanner = new Scanner(file);

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if (line.contains(getUsername())) {// 如果有找到使用者名稱
                    details.append(line).append("\n");// 一行一行讀取
                }
            }

            scanner.close();
        } catch (FileNotFoundException e) {// 檔案不存在
            e.printStackTrace();// 印出錯誤訊息
        }

        return details.toString();// 回傳字串
    }

}
