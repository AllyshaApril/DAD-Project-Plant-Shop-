package Customer;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import ShopOwner.Dashboard;

import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.Font;
import javax.swing.SwingConstants;
import javax.swing.JTextField;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.awt.event.ActionEvent;

public class Cust_SignUp extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField nameTxt;
	private JTextField numTxt;
	private JTextField emailTxt;
	private JTextField pwdTxt;
	private DatagramSocket udp;
	private InetAddress partnerAddress;
	private int partnerPort;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Cust_SignUp frame = new Cust_SignUp();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public Cust_SignUp() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel lblNewLabel = new JLabel("Welcome To A.K.A.A Flower Shop");
		lblNewLabel.setBounds(10, 10, 426, 17);
		lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel.setFont(new Font("Tahoma", Font.BOLD, 14));
		contentPane.add(lblNewLabel);
		
		JLabel lblNewLabel_1 = new JLabel("SIGN UP");
		lblNewLabel_1.setBounds(10, 34, 426, 17);
		lblNewLabel_1.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel_1.setFont(new Font("Tahoma", Font.BOLD, 14));
		contentPane.add(lblNewLabel_1);
		
		JLabel lblNewLabel_2 = new JLabel("Name :");
		lblNewLabel_2.setHorizontalAlignment(SwingConstants.RIGHT);
		lblNewLabel_2.setFont(new Font("Tahoma", Font.BOLD, 12));
		lblNewLabel_2.setBounds(10, 71, 118, 13);
		contentPane.add(lblNewLabel_2);
		
		nameTxt = new JTextField();
		nameTxt.setBounds(151, 69, 206, 19);
		contentPane.add(nameTxt);
		nameTxt.setColumns(10);
		
		JLabel lblNewLabel_2_1 = new JLabel("Phone Number :");
		lblNewLabel_2_1.setHorizontalAlignment(SwingConstants.RIGHT);
		lblNewLabel_2_1.setFont(new Font("Tahoma", Font.BOLD, 12));
		lblNewLabel_2_1.setBounds(10, 103, 118, 13);
		contentPane.add(lblNewLabel_2_1);
		
		numTxt = new JTextField();
		numTxt.setColumns(10);
		numTxt.setBounds(151, 101, 206, 19);
		contentPane.add(numTxt);
		
		JLabel lblNewLabel_2_1_1 = new JLabel("Email :");
		lblNewLabel_2_1_1.setHorizontalAlignment(SwingConstants.RIGHT);
		lblNewLabel_2_1_1.setFont(new Font("Tahoma", Font.BOLD, 12));
		lblNewLabel_2_1_1.setBounds(10, 133, 118, 13);
		contentPane.add(lblNewLabel_2_1_1);
		
		emailTxt = new JTextField();
		emailTxt.setColumns(10);
		emailTxt.setBounds(151, 131, 206, 19);
		contentPane.add(emailTxt);
		
		JLabel lblNewLabel_2_1_1_1 = new JLabel("Password :");
		lblNewLabel_2_1_1_1.setHorizontalAlignment(SwingConstants.RIGHT);
		lblNewLabel_2_1_1_1.setFont(new Font("Tahoma", Font.BOLD, 12));
		lblNewLabel_2_1_1_1.setBounds(10, 163, 118, 13);
		contentPane.add(lblNewLabel_2_1_1_1);
		
		pwdTxt = new JTextField();
		pwdTxt.setColumns(10);
		pwdTxt.setBounds(151, 161, 206, 19);
		contentPane.add(pwdTxt);
		
		JButton signupBtn = new JButton("Submit");
		signupBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
                Runnable run = new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Socket s = new Socket("10.200.109.19", 8080);
                            DataOutputStream out = new DataOutputStream(s.getOutputStream());
                            
                            out.writeUTF("Cust_SignUp");
                            
                            String name = nameTxt.getText();
                            out.writeUTF(name);
                            
                            String num = numTxt.getText();
                            out.writeUTF(num);
                            
                            String email = emailTxt.getText();
                            out.writeUTF(email);
                            
                            String pwd = pwdTxt.getText();
                            out.writeUTF(pwd);
                            
                            out.close();
                            s.close();

                            //JOptionPane.showMessageDialog(SignUp.this, "Data sent successfully!");

                        } catch (UnknownHostException e) {
                            e.printStackTrace();
                            JOptionPane.showMessageDialog(Cust_SignUp.this, "Failed to send data: Unknown host", "Error", JOptionPane.ERROR_MESSAGE);
                        } catch (IOException e) {
                            e.printStackTrace();
                            JOptionPane.showMessageDialog(Cust_SignUp.this, "Failed to send data: I/O error", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                };

                Thread thr1 = new Thread(run);
                thr1.start();
            }
		});
		signupBtn.setFont(new Font("Tahoma", Font.BOLD, 12));
		signupBtn.setBounds(55, 195, 327, 21);
		contentPane.add(signupBtn);
		
		JLabel lblNewLabel_2_1_1_2 = new JLabel("Already have an account? Please login here :");
		lblNewLabel_2_1_1_2.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel_2_1_1_2.setFont(new Font("Tahoma", Font.BOLD, 13));
		lblNewLabel_2_1_1_2.setBounds(10, 236, 302, 13);
		contentPane.add(lblNewLabel_2_1_1_2);
		
		JButton btnLogin = new JButton("Login");
		btnLogin.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new Cust_Login();
				dispose();
			}
		});
		btnLogin.setFont(new Font("Tahoma", Font.BOLD, 12));
		btnLogin.setBounds(322, 232, 96, 21);
		contentPane.add(btnLogin);
		
		setVisible(true);
	}
	
}
