import key.AES;
import key.ECC;
import key.RSA;
import sun.applet.Main;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeCellEditor;
import java.applet.Applet;
import java.applet.AudioClip;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.util.Random;

public class EncryptionGUI {
    public EncryptionGUI() {
        //textArea
        textArea2.setEditable(false);
        textArea3.setEditable(true);
        textArea4.setEditable(false);

        //nodeTree
        DefaultMutableTreeNode node1 = new DefaultMutableTreeNode("192.168.10");
        node1.add(new DefaultMutableTreeNode("192.168.10.45"));
        node1.add(new DefaultMutableTreeNode("192.168.10.74"));
        node1.add(new DefaultMutableTreeNode("192.168.10.98"));

        DefaultMutableTreeNode node2 = new DefaultMutableTreeNode("192.168.20");
        node2.add(new DefaultMutableTreeNode("192.168.20.9"));
        node2.add(new DefaultMutableTreeNode("192.168.20.24"));
        node2.add(new DefaultMutableTreeNode("192.168.20.84"));

        DefaultMutableTreeNode top = new DefaultMutableTreeNode("192.168.30");

        top.add(new DefaultMutableTreeNode("网段"));
        top.add(node1);
        top.add(node2);

        NodeTree = new JTree(top);
        // 添加选择事件
        NodeTree.setRootVisible(false);



        //comboBox
        comboBox.addItem("RSA");
        comboBox.addItem("ECC");
        comboBox.addItem("AES");

        EncryptionButton.addActionListener(new ActionListener() {
            /**
             * Invoked when an action occurs.
             *
             * @param e
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                String currentEncryption = comboBox.getSelectedItem().toString();

                String plaintext = EditorPane.getText();
                String ciphertext;

                if (true) { //currentEncryption.equals("ECC")
                    // if (!textField2.getText().isEmpty()) {
                    //     ecc.setPrivatekey(Long.parseLong(textField2.getText()));
                    // }

                    //log
                    textArea_Log.append("私钥：" + String.valueOf(ecc.getPrivatekey()) + "\n");
                    textArea_Log.append("公钥：" + String.valueOf(ecc.getPublickey().toString()) + "\n");
                    textArea_Log.append("基点：" + String.valueOf(ecc.getPare().toString()) + "\n");

                    //encryption
                    ciphertext = ecc.encryption(plaintext);
                    textArea2.setText(ciphertext);
                } else if (currentEncryption.equals("RSA")) {
                    ciphertext = rsa.Encryption(plaintext);
                    textArea2.setText(ciphertext);
                } else if (currentEncryption.equals("AES")) {
                    MainWindow.setBackground(Color.RED);
                    textArea2.setText("这不是密钥协商算法！");
                    textArea3.setText("这不是密钥协商算法！");
                    textArea4.setText("这不是密钥协商算法！");
                }
            }
        });

        SendButton.addActionListener(new ActionListener() {
            /**
             * Invoked when an action occurs.
             *
             * @param e
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    System.out.println("connecting...");
                    String IP = textField3.getText();
                    Socket s = new Socket(IP, 8011);
                    System.out.println("client connection successful");

                    Random M1 = new Random();

                    OutputStream os = s.getOutputStream();
                    OutputStreamWriter osw = new OutputStreamWriter(os);
                    PrintWriter pw = new PrintWriter(osw, true);

                    //privatekey
                    String plainText_copy = EditorPane.getText();
                    pw.println(plainText_copy + "@@@" + textArea2.getText());
                    textArea_Log.setText("消息M1" + M1.nextInt(10000000) + "发送成功");
                    System.out.println("client send complete");

                    s.close();

                    try {
                        ServerSocket ss2 = new ServerSocket(8022);

                        Socket s2 = ss2.accept();
                        System.out.println("server connection successful");

                        InputStream is = s.getInputStream();
                        InputStreamReader isr = new InputStreamReader(is);
                        BufferedReader br = new BufferedReader(isr);

                        String ciphertext = br.readLine();
                        System.out.println("server receive complete");


                        s2.close();
                        ss2.close();
                    } catch (Exception e2) {
                        e2.printStackTrace();
                    }
                } catch(Exception exception) {
                    exception.printStackTrace();
                }
            }
        });

        RecieveButton.addActionListener(new ActionListener() {
            /**
             * Invoked when an action occurs.
             *
             * @param e
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!ReceiveString.isEmpty()) {
                    radioButton.doClick();
                    Random M2 = new Random();
                    String[] StringListofReceive = ReceiveString.split("@@@");
                    // long l=Long.parseLong(StringListofReceive[0]);
                    // ecc.setPrivatekey(Long.parseLong(StringListofReceive[0]));
                    textArea_Log.setText("接收M1，并生成消息M2" + M2.nextInt(10000000) + "，并返回验证消息");
                    PlainTextCopy = StringListofReceive[0];
                    textArea3.setText(StringListofReceive[1]);

                    ReceiveString = "";

                    try {
                        System.out.println("connecting...");
                        String IP = textField3.getText();
                        Socket s2 = new Socket(IP, 8022);
                        System.out.println("client connection successful");

                        Random M1 = new Random();

                        OutputStream os = s2.getOutputStream();
                        OutputStreamWriter osw = new OutputStreamWriter(os);
                        PrintWriter pw = new PrintWriter(osw, true);

                        //privatekey
                        String plainText_copy = EditorPane.getText();
                        pw.println(plainText_copy + "@@@" + textArea2.getText());
                        textArea_Log.setText("消息M1" + M1.nextInt(10000000) + "发送成功");
                        System.out.println("client send complete");

                        s2.close();

                    } catch(Exception exception) {
                        exception.printStackTrace();
                    }
                }
            }
        });

        DecryptionButton.addActionListener(new ActionListener() {
            /**
             * Invoked when an action occurs.
             *
             * @param e
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                String currentEncryption = comboBox.getSelectedItem().toString();

                String ciphertext = textArea3.getText();
                String plaintext;
                if (true) { //currentEncryption.equals("ECC")
                    plaintext = ecc.decryption(ciphertext);
                    textArea4.setText(PlainTextCopy);
                } else if (currentEncryption.equals("RSA")) {
                    plaintext = rsa.Decryption(ciphertext);
                    textArea4.setText(plaintext);
                } else if (currentEncryption.equals("AES")) {
                    textArea2.setText("这不是密钥协商算法！");
                    textArea3.setText("这不是密钥协商算法！");
                    textArea4.setText("这不是密钥协商算法！");
                }
            }
        });
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("密钥协商系统(DKY授权)");
        EncryptionGUI MainGUI = new EncryptionGUI();

        //image
        class BackgroundPanel extends JPanel {

             private static final long serialVersionUID = -6352788025440244338L;
             private Image image = null;
             public BackgroundPanel(Image image) {
                 this.image = image;
             }

             protected void paintComponent(Graphics g) {
                 g.drawImage(image, 0, 0, this.getWidth(), this.getHeight(), this);
             }
        }

        Image image = new ImageIcon("1.JPG").getImage();
        MainGUI.imagePanel = new BackgroundPanel(image);

        //BGM
        try {
            URL musicUrl = new URL("file:" + System.getProperty("user.dir").toString() + "/3.mp3");
            AudioClip ac = Applet.newAudioClip(musicUrl);
            ac.play();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }




        frame.setContentPane(MainGUI.MainWindow);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);

        //local server
        while (true) {
            try {
                ServerSocket ss = new ServerSocket(8011);

                Socket s = ss.accept();
                System.out.println("server connection successful");

                InputStream is = s.getInputStream();
                InputStreamReader isr = new InputStreamReader(is);
                BufferedReader br = new BufferedReader(isr);

                String ciphertext = br.readLine();
                System.out.println("server receive complete");

                if (!ciphertext.isEmpty()) {
                    MainGUI.radioButton.setSelected(true);
                    MainGUI.ReceiveString = ciphertext;
                }

                s.close();
                ss.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private JPanel MainWindow;
    private JComboBox comboBox;
    private JEditorPane EditorPane;
    private JButton SendButton;
    private JButton RecieveButton;
    private JTree NodeTree;
    private JPanel EncryptionPanel;
    private JPanel DecryptionPanel;
    private JPanel SendandReceive;
    private JPanel SendNode;
    private JRadioButton radioButton;
    private JPanel UserAction;
    private JTextArea textArea2;
    private JTextField textField2;
    private JButton EncryptionButton;
    private JButton DecryptionButton;
    private JTextArea textArea4;
    private JEditorPane textArea3;
    private JPanel imagePanel;
    private JTextArea textArea_Log;
    private JTextField textField3;

    ECC ecc = new ECC();
    RSA rsa = new RSA();
    AES aes = new AES();

    private String ReceiveString;
    private String PlainTextCopy;
}
