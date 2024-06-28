package Customer;

import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import java.awt.Font;
import javax.swing.SwingConstants;
import javax.swing.JTextField;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import org.json.JSONException;
import org.json.JSONObject;

public class Cust_Login extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private JTextField emailTxt;
    private JTextField pwdTxt;
    private ServerSocket serverSocket; // Store ServerSocket as an instance variable

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    Cust_Login frame = new Cust_Login();
                    frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public Cust_Login() {
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Use DISPOSE_ON_CLOSE instead of EXIT_ON_CLOSE
        setBounds(100, 100, 450, 300);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

        setContentPane(contentPane);
        contentPane.setLayout(null);

        JLabel lblNewLabel = new JLabel("Welcome To A.K.A.A Flower Shop");
        lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
        lblNewLabel.setFont(new Font("Tahoma", Font.BOLD, 14));
        lblNewLabel.setBounds(10, 10, 426, 13);
        contentPane.add(lblNewLabel);

        JLabel lblNewLabel_1 = new JLabel("LOGIN");
        lblNewLabel_1.setFont(new Font("Tahoma", Font.BOLD, 14));
        lblNewLabel_1.setHorizontalAlignment(SwingConstants.CENTER);
        lblNewLabel_1.setBounds(10, 33, 426, 13);
        contentPane.add(lblNewLabel_1);

        JLabel lblNewLabel_2 = new JLabel("Email :");
        lblNewLabel_2.setHorizontalAlignment(SwingConstants.RIGHT);
        lblNewLabel_2.setFont(new Font("Tahoma", Font.BOLD, 13));
        lblNewLabel_2.setBounds(10, 97, 118, 13);
        contentPane.add(lblNewLabel_2);

        JLabel lblNewLabel_2_1 = new JLabel("Password :");
        lblNewLabel_2_1.setHorizontalAlignment(SwingConstants.RIGHT);
        lblNewLabel_2_1.setFont(new Font("Tahoma", Font.BOLD, 13));
        lblNewLabel_2_1.setBounds(10, 135, 118, 13);
        contentPane.add(lblNewLabel_2_1);

        emailTxt = new JTextField();
        emailTxt.setBounds(167, 95, 218, 19);
        contentPane.add(emailTxt);
        emailTxt.setColumns(10);

        pwdTxt = new JTextField();
        pwdTxt.setColumns(10);
        pwdTxt.setBounds(167, 133, 218, 19);
        contentPane.add(pwdTxt);

        JButton loginBtn = new JButton("Login");
        loginBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Runnable run = new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Socket s = new Socket("10.200.109.19", 8080);
                            DataOutputStream out = new DataOutputStream(s.getOutputStream());

                            out.writeUTF("Cust_Login");

                            String email = emailTxt.getText();
                            out.writeUTF(email);

                            String pwd = pwdTxt.getText();
                            out.writeUTF(pwd);

                            out.writeUTF("Customer");

                            out.close();
                            s.close();

                        } catch (UnknownHostException e) {
                            e.printStackTrace();
                            JOptionPane.showMessageDialog(Cust_Login.this, "Failed to send data: Unknown host", "Error", JOptionPane.ERROR_MESSAGE);
                        } catch (IOException e) {
                            e.printStackTrace();
                            JOptionPane.showMessageDialog(Cust_Login.this, "Failed to send data: I/O error", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                };

                Thread thr1 = new Thread(run);
                thr1.start();
            }
        });
        loginBtn.setFont(new Font("Tahoma", Font.BOLD, 12));
        loginBtn.setBounds(58, 172, 327, 21);
        contentPane.add(loginBtn);

        JLabel lblNewLabel_2_1_1 = new JLabel("No account? Please sign-up here :");
        lblNewLabel_2_1_1.setHorizontalAlignment(SwingConstants.RIGHT);
        lblNewLabel_2_1_1.setFont(new Font("Tahoma", Font.BOLD, 13));
        lblNewLabel_2_1_1.setBounds(43, 216, 232, 13);
        contentPane.add(lblNewLabel_2_1_1);

        JButton btnSignUp = new JButton("Sign Up");
        btnSignUp.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Cust_SignUp signUpFrame = new Cust_SignUp();
                signUpFrame.setVisible(true);
                Cust_Login.this.dispose();
            }
        });
        btnSignUp.setFont(new Font("Tahoma", Font.BOLD, 12));
        btnSignUp.setBounds(285, 214, 85, 21);
        contentPane.add(btnSignUp);

        // Add window listener to handle window closing
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                // Close the server socket when the window is closed
                if (serverSocket != null && !serverSocket.isClosed()) {
                    try {
                        serverSocket.close();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });

        closeFrameReceiver();
        setVisible(true);
    }

    public void closeFrameReceiver() {
        Thread thrUDP = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    serverSocket = new ServerSocket(5000); // Initialize serverSocket here
                    while (true) {
                        Socket clientSocket = serverSocket.accept();
                        DataInputStream in = new DataInputStream(clientSocket.getInputStream());
                        System.out.println("Cust_Login action received");
                        String action = in.readUTF();

                        if (action.equals("Login Successful")) {
                            int c_id = Integer.valueOf(in.readUTF());
                            JOptionPane.showMessageDialog(Cust_Login.this, "Login successful!");
                            
                            Cust_Login.this.dispose();
                            new Cust_Dashboard(c_id);
                        }
                        

                        in.close();
                        clientSocket.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    //JOptionPane.showMessageDialog(null, "Failed to receive data: I/O error", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        thrUDP.start();
    }
}
