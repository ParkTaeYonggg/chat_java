package tcp;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;



public class TCPServer2 {
    // 변수들
    Scanner sc;
    Map<String,DataOutputStream> map;
    ArrayList<String> list;
    
    // 메인 불러오기만 사용할 것. /////////////////////////
    public static void main(String[] args) {
        new TCPServer2().ServerMain();
    }// main
    ////////////////////////////////////////////////
    
    //생성자
    TCPServer2() {
        sc = new Scanner(System.in);
        map = new HashMap<>();
        list = new ArrayList<>();
    }
    
    
    void ServerMain() {
        ServerSocket server = null;
        Socket socket;
        try {
            // 서버생성
            server = new ServerSocket(10000);
            
            while (true) {
                // 서버 접속 대기
                System.out.println("접속 대기중");
                socket = server.accept();
                
                if (socket.isConnected()) {
                    
                    // 정보 받아드릴 쓰레드 클래스 : 메인 서버에만.
                    ServerReceiver sr = new ServerReceiver (socket);
                    sr.start();
                    
                }
                 
            }
        } catch (IOException e) {
    
            e.printStackTrace();
        }
    }
    
    
   class ServerReceiver extends Thread {
       Socket socket;
       DataOutputStream out = null;
       DataInputStream in = null;
       
       
       ServerReceiver (Socket socket) throws IOException {
           this.socket = socket;
           in = new DataInputStream(socket.getInputStream());
           out = new DataOutputStream(socket.getOutputStream());
           
           
       }
       
       
       @Override
    public void run() {
           String name = null;
           
           System.out.println("클라이언트와 연결됨.");
           System.out.println(socket.getInetAddress().getHostAddress() + ">>접속");
           
           try {
               // 접속자 정보 등록 : 동일한 닉네임은 숫자가 추가됨.
               name = in.readUTF();
               list.add(name);
               if (map.containsKey(name)) {
                   name = name+(namePlus(list,name));
                   map.put(name, out);
               } else { 
                   map.put(name, out);
               }
               // 서버접속 인원 수 및 서버 접속 유저 네임.
               System.out.println("<접속자 명단>    현재 접속 인원 : " + map.size() + "명");
               for (String s : map.keySet()) {
                   System.out.println(s);
               }
               
               while (true) {
                   String msg = in.readUTF();
                   
                   if (msg.equalsIgnoreCase("상담연결")) {
                       // 상담연결 메소드 이동
                       ClientServiceCenter csc = new ClientServiceCenter(socket);
                       csc.conciliator();
                       msg = "부재중..";
                   }
                   
                   System.out.println("<서버> " + name + " : " + msg);
                   
                   
                   
                   if (msg.equalsIgnoreCase("x")) {
                       break;
                   }
                   
                   
                   // 정보 보낼 쓰레드 클래스 : 전체 이용자에게.
                   ServerSender ss = new ServerSender(map,msg,name);
                   ss.start();
                   
               }
            
            
            
        } catch (IOException e) {

            e.printStackTrace();
        } finally {
           
                System.out.println(name + "님이 퇴장하셨습니다.");
                map.remove(name);
           
        }
    }
       
       int namePlus(ArrayList<String> list, String name) {
           int returnNum;
           int tempNum = 0;
           
           for (String s : list) {
               if (s.equalsIgnoreCase(name)) {
                   tempNum++;
               }
           }
           returnNum = tempNum;
           tempNum = 0;
           return returnNum;
       }
       
   }
    
   
   class ServerSender extends Thread {
       
       DataOutputStream out;
       Iterator<String> ite;
       Map<String,DataOutputStream> map;
       String msg = null;
       String name;
       ServerSender (Map<String,DataOutputStream> map, String msg, String name) {
           this.map = map;
           this.msg = msg;
           this.name = name;
       }
       
     @Override
    public void run() {
         
         ite = map.keySet().iterator();
         
         while (ite.hasNext()) {
             // 전체 이용자에게 보내는 메시지.
             
             out = map.get(ite.next());
             
             try {
                out.writeUTF(name + ") " + msg);
                out.flush();
            } catch (IOException e) {

                e.printStackTrace();
            }
         }
    }
       
       
   }
   
   
   
   class ClientServiceCenter {
       Socket socket;
       
       ClientServiceCenter (Socket socket) {
           this.socket = socket;
       }
       
       void conciliator () {
           
           System.out.println("상담사가 연결되었습니다.");
           System.out.println("무엇이든 질문하세요.");
           
           // 상담사와 1:1 쓰레드 클래스 만들어야 할 자리임.
           // 주고 받기 -> 단, 다른 것과 다르게 맵을 사용할 필요가 없음.
           
           
           
           System.out.println("상담이 종료되었습니다.");
           
       }
       
       
       
   }
   
   
   
   int inNum(int i, int j) {
       return (i> 100) ? 'g' : (i < 100) ? 'b' : 'ㅋ';
   }
   
   
   
   
   
   
   
   
   
   
   
}// public class
