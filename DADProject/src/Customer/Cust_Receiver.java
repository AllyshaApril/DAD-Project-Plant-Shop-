package Customer;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class Cust_Receiver {

	public void startReceiver() {
		System.out.println("Cust_Receiver action STARTED");
        Thread thrUDP = new Thread(() -> {
            try (ServerSocket serverSocket = new ServerSocket(9999)) {
                while (true) {
                    Socket clientSocket = serverSocket.accept();
                    DataInputStream in = new DataInputStream(clientSocket.getInputStream());
                    
                    String action = in.readUTF();
                    System.out.println("ACTION received: "+action);
                    switch (action) {
                        case "Login Successful":                        	
                        	System.out.println("Cust_Login action received");
                        	int c_id = Integer.valueOf(in.readUTF());
                        	Cust_Login login = new Cust_Login();
                        	JOptionPane.showMessageDialog(login, "Login successful!");
                        	login.dispose();
                        	new Cust_Dashboard(c_id);
                            break;
                        default:
                            System.out.println("Unknown action: " + action);
                    }

                    in.close();
                    clientSocket.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        thrUDP.start();
    }
	
	public static void main(String[] args) {
		Cust_Receiver cust_receiver = new Cust_Receiver();
		cust_receiver.startReceiver();
	}

}
