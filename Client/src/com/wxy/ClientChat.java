package com.wxy;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;

public class ClientChat {
    //这是一个main方法，是程序的入口：
    public static void main(String[] args) {
        ClientJframe clientJframe = new ClientJframe();
        clientJframe.init();
    }
}

class ClientJframe extends JFrame {
    //GUI布局
    //聊天记录显示区
    private JTextArea ta = new JTextArea(10, 20);
    //聊天记录输入区
    private JTextField tf = new JTextField(20);
    private StringBuilder sb = new StringBuilder();
    private String userName = "张三";
    //端口
    // 静态常量主机端口号
    private static final String CONNSTR = "192.168.88.178";
    // 静态常量服务器端口号
    private static final int CONNPORT = 8888;
    private Socket socket = null;

    //Client发送数据
    private DataOutputStream dataOutputStream = null;

    //客户端连接上服务器判断符号
    private boolean isConn = false;

    /**
     * 无参的构造方法 throws HeadlessException
     */
    public ClientJframe() throws HeadlessException {
        super();
    }

    public void init() {


        setTitle("TCP群聊-客户端");
        getContentPane().setLayout(null);
        setBounds(100, 100, 500, 375);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setResizable(false);


        ta.setEditable(false);
        ta.setBorder(new LineBorder(Color.black, 1, false));
        ta.setBounds(0, 0, 408, 219);
        getContentPane().add(ta);


        tf.setBorder(new LineBorder(Color.black, 1, false));
        tf.setBounds(0, 225, 408, 39);
        getContentPane().add(tf);

        final JButton button = new JButton();
        button.setText("发送");
        button.setBounds(28, 285, 117, 28);
        getContentPane().add(button);

        final JButton button_1 = new JButton();
        button_1.setText("清屏");
        button_1.setBounds(181, 285, 106, 28);
        getContentPane().add(button_1);
        // 添加监听，使回车键可以输入数据(判断数据合法性)，
        // 并輸入到聊天框，换行
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String strSend = tf.getText();
                if (strSend.trim().length() == 0) {
                    return;
                }
                //发送消息
                send(strSend);
            }
        });
        tf.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                String strSend = tf.getText();
                // 去掉空格判断长度是否为空
                if (strSend.trim().length() == 0) {
                    return;
                }
                //客户端信息strSend发送到服务器上
                send(strSend);
                tf.setText("");
                //ta.append(strSend + "\n");

            }
        });

        button_1.setText("清屏");
        button_1.setBounds(181, 285, 106, 28);
        getContentPane().add(button_1);
        // 清空事件
        button_1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sb = new StringBuilder();
                ta.setText(sb.toString());
            }
        });




        //关闭事件
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        ta.setEditable(false);//聊天区域不可以输入
        tf.requestFocus();//光标聚焦

        try {
            socket = new Socket(CONNSTR, CONNPORT);
            isConn = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        userName = JOptionPane.showInputDialog(this,"请输入您的昵称");
        this.setVisible(true);
        final JLabel label = new JLabel();
        label.setText("当前用户：");
        label.setBounds(414, 37, 66, 18);
        getContentPane().add(label);

        final JLabel label_1 = new JLabel();
        label_1.setText(userName);
        label_1.setBounds(414, 80, 66, 18);
        getContentPane().add(label_1);
        // 启动多线程
        new Thread(new Receive()).start();


    }



    /**
     * 客户端发送信息到服务器上的方法
     */
    public void send(String str) {

        try {
            dataOutputStream = new DataOutputStream(socket.getOutputStream());
            dataOutputStream.writeUTF(userName+"说:"+str);

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        tf.setText("");
    }

    /**
     * @author 武新宇
     * @deprecated 多线程的类，实现了Runnable接口的类
     */
    class Receive implements Runnable {
        @Override
        public void run() {
            try {
                while (isConn) {
                    DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
                    String str = dataInputStream.readUTF();
                    //通讯消息
                    ta.append(str);
                }
            } catch (SocketException e) {
                System.out.println("服务器意外终止了！");
                ta.append("服务器意外终止了！");
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
}