package ShopOwner;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import java.awt.*;
import java.awt.event.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class Dashboard extends JFrame {

    private JPanel contentPane;
    private Receiver receiver;

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                Dashboard frame = new Dashboard();
                frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public Dashboard() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("A.K.A.A Admin");
        setBounds(100, 100, 450, 300);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(null);

        JLabel lblNewLabel = new JLabel("A.K.A.A Flowers Admin");
        lblNewLabel.setFont(new Font("Tahoma", Font.BOLD, 14));
        lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
        lblNewLabel.setBounds(10, 10, 416, 13);
        contentPane.add(lblNewLabel);

        JButton plantBtn = new JButton("Plant Inventory");
        plantBtn.addActionListener(e -> {
            new PlantInventory();
            dispose();
        });
        plantBtn.setFont(new Font("Tahoma", Font.BOLD, 14));
        plantBtn.setBounds(41, 85, 360, 39);
        contentPane.add(plantBtn);

        JButton orderBtn = new JButton("Orders");
        orderBtn.addActionListener(e -> {
            new Order();
            dispose();
        });
        orderBtn.setFont(new Font("Tahoma", Font.BOLD, 14));
        orderBtn.setBounds(41, 134, 360, 39);
        contentPane.add(orderBtn);

        // Initialize Receiver instance
        receiver = new Receiver();
        receiver.startReceiver(); // Start listening for incoming connections

        setVisible(true);
    }
}
