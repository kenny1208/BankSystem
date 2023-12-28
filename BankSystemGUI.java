import java.io.*;
import java.util.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

public class BankSystemGUI extends JFrame {
    private static String USERS_FILE = "users.txt";
    private static String MONEY_FILE = "money.txt";
    private static void writeLogToFile(String log) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String currentTime = dateFormat.format(new Date());// 取得現在時間

            FileWriter writer = new FileWriter("log.txt", true);
            writer.write(currentTime + " - " + log + "\n");
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private static DefaultListModel<String> logListModel = new DefaultListModel<>();
    private static JList<String> logList = new JList<>(logListModel);
    public static ArrayList<Client> clientList = new ArrayList<>(); // 存放客戶資料
    public static ArrayList<Admin> adminList = new ArrayList<>(); // 存放管理員資料

    public BankSystemGUI() {
        setTitle("銀行儲蓄系統");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayout(3, 1));

        JLabel titleLabel = new JLabel("歡迎使用銀行儲蓄系統系統");
        titleLabel.setHorizontalAlignment(JLabel.CENTER);
        mainPanel.add(titleLabel);

        JButton loginButton = new JButton("登入");
        loginButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showLoginMenu();
            }
        });
        mainPanel.add(loginButton);

        JButton exitButton = new JButton("退出");
        exitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        mainPanel.add(exitButton);

        add(mainPanel, BorderLayout.CENTER);
        add(new JScrollPane(logList), BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void showLoginMenu() {
        JFrame loginFrame = new JFrame("登入");
        loginFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        loginFrame.setLayout(new GridLayout(3, 2));

        JLabel usernameLabel = new JLabel("帳號：");
        JTextField usernameField = new JTextField();
        JLabel passwordLabel = new JLabel("密碼：");
        JPasswordField passwordField = new JPasswordField();
        JButton loginButton = new JButton("登入");

        loginButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());
                User currentUser = authenticateUser(username, password);
                if (currentUser != null) {
                    if (currentUser instanceof Admin) {
                        writeLogToFile("Admin " + currentUser.getUsername() + " login");
                        showAdminMenu((Admin) currentUser);
                    } else if (currentUser instanceof Client) {
                        writeLogToFile("Client " + currentUser.getUsername() + " login");
                        showClientMenu((Client) currentUser);
                    }
                    loginFrame.dispose();
                } else {
                    JOptionPane.showMessageDialog(loginFrame, "帳號或密碼錯誤！", "錯誤", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        loginFrame.add(usernameLabel);
        loginFrame.add(usernameField);
        loginFrame.add(passwordLabel);
        loginFrame.add(passwordField);
        loginFrame.add(loginButton);

        loginFrame.pack();
        loginFrame.setLocationRelativeTo(null);
        loginFrame.setVisible(true);
    }

    private User authenticateUser(String username, String password) {

        for (Client client : clientList) {
            if (client.getUsername().equals(username) && client.checkPassword(password)) {
                return client;
            }
        }

        for (Admin admin : adminList) {
            if (admin.getUsername().equals(username) && admin.checkPassword(password)) {
                return admin;
            }
        }
        return null;
    }

    private static Client findcClient(String username) {// 尋找客戶
        for (Client client : clientList) {
            if (client.getUsername().equals(username)) {
                return client;
            }
        }
        return null;
    }

    private void showAdminMenu(Admin admin) {
        JFrame adminFrame = new JFrame("Admin " + admin.getUsername());
        adminFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        adminFrame.setLayout(new GridLayout(2, 1));

        JButton recordprofileButton = new JButton("搜尋客戶資料");
        recordprofileButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String username = JOptionPane.showInputDialog(adminFrame, "請輸入客戶使用者名稱：");
                Client client = findcClient(username);
                if (client != null) {
                    JFrame clientInfoFrame = new JFrame("客戶資訊 - " + client.getUsername());// 設定視窗標題
                    clientInfoFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);// 設定關閉視窗時的動作
                    clientInfoFrame.setLayout(new GridLayout(2, 1));// 設定版面為2*1的格狀

                    JTextArea clientInfoTextArea = new JTextArea();// 建立文字區域
                    clientInfoTextArea.setEditable(false);// 設定文字區域不可編輯
                    // clientInfoTextArea.setText(client.toString());// 將客戶資料設定至文字區域
                    clientInfoTextArea.setText("name: " + client.getRealname() + "\nusername: " + client.getUsername()
                            + "\nmoney: " + client.getmoney() + "\npassword: " + client.getPassword());

                    JButton recordDepositButton = new JButton("登記存款");
                    recordDepositButton.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                            Admin admin = new Admin("admin", "0000", "admin");
                            String moneyInput = JOptionPane.showInputDialog("請輸入金額：");
                            int money = Integer.parseInt(moneyInput);

                            admin.recordmoney(client, money);
                            savemoney();
                            JOptionPane.showMessageDialog(null, "登記成功！", "成功", JOptionPane.INFORMATION_MESSAGE);
                            clientInfoTextArea.setText("name: " + client.getRealname() + "\nusername: "
                                    + client.getUsername() + "\nmoney: " + client.getmoney() + "\npassword: "
                                    + client.getPassword());
                        }
                    });

                    clientInfoFrame.add(clientInfoTextArea);
                    clientInfoFrame.add(recordDepositButton);

                    clientInfoFrame.pack();
                    clientInfoFrame.setLocationRelativeTo(adminFrame);
                    clientInfoFrame.setVisible(true);
                } else {
                    JOptionPane.showMessageDialog(adminFrame, "找不到該客戶！");
                }
            }
        });

        JButton recordDepositButton = new JButton("登記客戶存款");
        recordDepositButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                recordClientMoney();
            }
        });

        JButton addnewUserButton = new JButton("add new User");
        addnewUserButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFrame addnewUserFrame = new JFrame("add new User");
                addnewUserFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                addnewUserFrame.setLayout(new GridLayout(4, 2));

                JLabel usernameLabel = new JLabel("帳號：");
                JTextField usernameField = new JTextField();
                JLabel passwordLabel = new JLabel("密碼：");
                JPasswordField passwordField = new JPasswordField();
                JLabel realnameLabel = new JLabel("姓名：");
                JTextField realnameField = new JTextField();
                JButton addnewUserButton = new JButton("確定");

                addnewUserButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        String username = usernameField.getText();
                        String password = new String(passwordField.getPassword());
                        String realname = realnameField.getText();
                        if (username.equals("") || password.equals("") || realname.equals("")) {
                            JOptionPane.showMessageDialog(addnewUserFrame, "請輸入完整資料！", "錯誤",
                                    JOptionPane.ERROR_MESSAGE);
                        } else {
                            clientList.add(new Client(username, password, realname));
                            try (PrintWriter writer = new PrintWriter(new FileWriter(USERS_FILE, true))) {
                                writer.println("C," + username + "," + password + "," + realname);
                            } catch (IOException e1) {
                                e1.printStackTrace();
                            }
                            JOptionPane.showMessageDialog(addnewUserFrame, "新增成功！", "成功",
                                    JOptionPane.INFORMATION_MESSAGE);
                            addnewUserFrame.dispose();
                        }
                    }
                });

                addnewUserFrame.add(usernameLabel);
                addnewUserFrame.add(usernameField);
                addnewUserFrame.add(passwordLabel);
                addnewUserFrame.add(passwordField);
                addnewUserFrame.add(realnameLabel);
                addnewUserFrame.add(realnameField);
                addnewUserFrame.add(addnewUserButton);

                addnewUserFrame.pack();
                addnewUserFrame.setLocationRelativeTo(null);
                addnewUserFrame.setVisible(true);
            }
        });

        JButton logoutButton = new JButton("登出");
        logoutButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                writeLogToFile("Admin " + admin.getUsername() + " logout");
                adminFrame.dispose();
            }
        });

        adminFrame.add(recordprofileButton);// 將搜尋客戶資料按鈕加入adminFrame
        adminFrame.add(recordDepositButton);// 將登記客戶存款按鈕加入adminFrame
        adminFrame.add(addnewUserButton);// 將add new User按鈕加入adminFrame
        adminFrame.add(logoutButton);// 將登出按鈕加入adminFrame

        adminFrame.pack();// 設定adminFrame的大小
        adminFrame.setLocationRelativeTo(null);// 設定adminFrame的位置
        adminFrame.setVisible(true);// 顯示adminFrame
    }

    private void showClientMenu(Client client) {
        JFrame adminFrame = new JFrame("client " + client.getUsername());
        adminFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        adminFrame.setLayout(new GridLayout(3, 1));

        JButton recordDepositButton = new JButton("查詢帳戶餘額");
        recordDepositButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Call a method to display the account balance
                displayAccountBalance(client);
            }
        });

        JButton saveMoneyButton = new JButton("存錢");
        saveMoneyButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFrame depositFrame = new JFrame("存款");
                depositFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                depositFrame.setLayout(new GridLayout(2, 1));

                JLabel depositLabel = new JLabel("請輸入存款金額：");
                JTextField depositTextField = new JTextField();
                JButton depositButton = new JButton("確定");

                depositButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        int money = Integer.parseInt(depositTextField.getText());
                        client.addmoney(money);
                        savemoney();
                        JOptionPane.showMessageDialog(null, "存款成功！", "提示", JOptionPane.INFORMATION_MESSAGE);
                        displayAccountBalance(client);
                        depositFrame.dispose();
                    }
                });

                depositFrame.add(depositLabel);
                depositFrame.add(depositTextField);
                depositFrame.add(depositButton);

                depositFrame.pack();
                depositFrame.setLocationRelativeTo(null);
                depositFrame.setVisible(true);
            }
        });

        JButton getMoneyButton = new JButton("提款");
        getMoneyButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFrame withdrawFrame = new JFrame("提款");
                withdrawFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                withdrawFrame.setLayout(new GridLayout(2, 1));

                JLabel withdrawLabel = new JLabel("請輸入提款金額：");
                JTextField withdrawTextField = new JTextField();
                JButton withdrawButton = new JButton("確定");

                withdrawButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        int money = Integer.parseInt(withdrawTextField.getText());
                        if (client.getmoney() >= money) {
                            client.addmoney(-money);
                            savemoney();
                            JOptionPane.showMessageDialog(null, "提款成功！", "提示", JOptionPane.INFORMATION_MESSAGE);
                            displayAccountBalance(client);
                        } else {
                            JOptionPane.showMessageDialog(null, "餘額不足！", "提示", JOptionPane.WARNING_MESSAGE);
                            displayAccountBalance(client);
                        }
                        withdrawFrame.dispose();
                    }
                });

                withdrawFrame.add(withdrawLabel);
                withdrawFrame.add(withdrawTextField);
                withdrawFrame.add(withdrawButton);

                withdrawFrame.pack();
                withdrawFrame.setLocationRelativeTo(null);
                withdrawFrame.setVisible(true);
            }
        });

        JButton logoutButton = new JButton("登出");
        logoutButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                writeLogToFile("Client " + client.getUsername() + " logout");
                adminFrame.dispose();
            }
        });

        adminFrame.add(recordDepositButton);
        adminFrame.add(saveMoneyButton);
        adminFrame.add(getMoneyButton);
        adminFrame.add(logoutButton);

        adminFrame.pack();
        adminFrame.setLocationRelativeTo(null);
        adminFrame.setVisible(true);
    }

    private void displayAccountBalance(Client client) {
        // Check if the client exists
        if (client != null) {
            // Get the account balance
            double balance = client.getmoney();
            // Display the account balance
            JOptionPane.showMessageDialog(null, "Account Balance: " + balance);
        } else {
            JOptionPane.showMessageDialog(null, "Client not found.");
        }
    }

    // 讀取使用者資料
    private static void loadUsers() {
        try (Scanner scanner = new Scanner(new File(USERS_FILE))) {// Users.txt
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] parts = line.split(",");
                String userType = parts[0];
                String username = parts[1];
                String password = parts[2];
                String realname = parts[3];

                if (userType.equals("A")) {
                    adminList.add(new Admin(username, password, realname)); // Change 'adminList' to 'admin'
                } else if (userType.equals("C")) {
                    clientList.add(new Client(username, password, realname)); // Fixed the variable name from 'clinet'
                                                                              // to // 'clientList'
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static void loadMoney() {
        try (Scanner scanner = new Scanner(new File(MONEY_FILE))) {
            // 逐行讀取檔案內容
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                // 將每一行以逗號分隔，解析為不同的部分
                String[] parts = line.split(",");
                String clientname = parts[0]; // 第一部分為客戶的名字（假設）
                int money = Integer.parseInt(parts[1]); // 第二部分為金額

                // 在已有的客戶清單中尋找符合該名字的客戶，並為其添加金額
                for (Client client : clientList) {
                    if (client.getUsername().equals(clientname)) {
                        client.addmoney(money); // 將金額添加至客戶的賬戶中
                        break; // 找到符合的客戶後就結束迴圈
                    }
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace(); // 處理找不到檔案的情況
        }
    }

    private void recordClientMoney() {// 登記客戶存款
        Admin admin = new Admin("admin", "0000", "admin");
        String clientUsername = JOptionPane.showInputDialog("請輸入客戶帳號：");

        Client client = findcClient(clientUsername);
        if (client != null) {
            String moneyInput = JOptionPane.showInputDialog("請輸入金額：");
            int money = Integer.parseInt(moneyInput);

            admin.recordmoney(client, money);
            savemoney();
            JOptionPane.showMessageDialog(null, "登記成功！", "成功", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(null, "找不到該客戶！", "錯誤", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void findClientMoney() {// 查詢客戶存款
        String clientUsername = JOptionPane.showInputDialog("請輸入客戶帳號：");

        Client client = findcClient(clientUsername);
        if (client != null) {
            JOptionPane.showMessageDialog(null, "客戶餘額為：" + client.getmoney(), "成功", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(null, "找不到該客戶！", "錯誤", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static void savemoney() {// 存錢
        try (PrintWriter writer = new PrintWriter(new FileWriter(MONEY_FILE))) {
            for (Client client : clientList) {
                Integer money = client.getmoney(); // 取得客戶的金額
                writer.println(client.getUsername() + "," + money);// 將客戶的名字和金額寫入檔案
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                loadUsers();
                loadMoney();
                new BankSystemGUI();
            }
        });
    }
}
