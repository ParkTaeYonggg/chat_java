package tcp;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class TCPClient2 {

    Scanner sc;

    public static void main(String[] args) {
        new TCPClient2().ClientMain();
    }

    TCPClient2() {
        sc = new Scanner(System.in);
    }

    void ClientMain() {

        Socket socket;

        // 서버 연결
        try {
            socket = new Socket("localHost", 10000);
            System.out.println("접속완료");

            System.out.println("닉네임을 결정해 주세요.");
            String name = sc.nextLine();
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            out.writeUTF(name);
            out.flush();

            // 메시지 보내기 : 전체에게
            ClientSender cs = new ClientSender(socket);
            cs.start();

            // 메시지 받기 : 서버에서
            ClientReceiver cr = new ClientReceiver(socket);
            cr.start();

        } catch (IOException e) {

            e.printStackTrace();
        }

    }

    class ClientSender extends Thread {
        Socket socket;
        DataOutputStream out;

        ClientSender(Socket socket) throws IOException {
            this.socket = socket;
            out = new DataOutputStream(socket.getOutputStream());
        }

        @Override
        public void run() {

            while (true) {
                System.out.println("입력) ");
                String msg = sc.nextLine();

                try {
                    out.writeUTF(msg);
                    out.flush();

                    if (msg.equalsIgnoreCase("x")) {
                        break;
                    }

                } catch (IOException e) {

                    e.printStackTrace();
                }
            }
        }
    }
    
   
    
    

    class ClientReceiver extends Thread {
        Socket socket;
        DataInputStream in;

        ClientReceiver(Socket socket) throws IOException {
            this.socket = socket;
            in = new DataInputStream(socket.getInputStream());
        }

        @Override
        public void run() {
            while (true) {
                
                try {
                    
                    System.out.println(in.readUTF());
                    
                } catch (IOException e) {

                    e.printStackTrace();
                }
                
            }
        }

    }
    
   
    

}
