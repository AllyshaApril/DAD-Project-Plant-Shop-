package Customer;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.SwingConstants;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class Cust_Dashboard extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;

	/**
	 * Launch the application.
	 */
	/*public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Cust_Dashboard frame = new Cust_Dashboard(c_id);
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}*/

	/**
	 * Create the frame.
	 */
	public Cust_Dashboard(int c_id) {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel lblNewLabel = new JLabel("Welcome to A.K.A.A Flower Shop");
		lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel.setFont(new Font("Tahoma", Font.BOLD, 14));
		lblNewLabel.setBounds(10, 10, 416, 22);
		contentPane.add(lblNewLabel);
		
		JButton btnFlowersForSale = new JButton("Flowers for Sale");
		btnFlowersForSale.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new Cust_Plant(c_id);
				dispose();
			}
		});
		btnFlowersForSale.setFont(new Font("Tahoma", Font.BOLD, 14));
		btnFlowersForSale.setBounds(40, 89, 360, 39);
		contentPane.add(btnFlowersForSale);
		
		JButton btnOrderStatus = new JButton("Order History");
		btnOrderStatus.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new Order_Status(c_id);
				dispose();
			}
		});
		btnOrderStatus.setFont(new Font("Tahoma", Font.BOLD, 14));
		btnOrderStatus.setBounds(40, 138, 360, 39);
		contentPane.add(btnOrderStatus);
		
		setVisible(true);
	}
}
