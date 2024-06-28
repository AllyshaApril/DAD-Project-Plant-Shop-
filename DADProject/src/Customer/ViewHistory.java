package Customer;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ViewHistory extends JFrame {
    private JPanel contentPane;
    private JTable table;
    public static DefaultTableModel tableModel;

	public ViewHistory(int purchaseId, String orderDate, double totalPrice, List<Item> items) {
		setTitle("Order Details");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setBounds(100, 100, 450, 320);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(null);

        JLabel lblOrderDetails = new JLabel("Order Details for Purchase ID: " + purchaseId);
        lblOrderDetails.setFont(new Font("Tahoma", Font.BOLD, 14));
        lblOrderDetails.setBounds(10, 10, 400, 25);
        contentPane.add(lblOrderDetails);

        JLabel lblOrderDate = new JLabel("Order Date: " + orderDate);
        lblOrderDate.setFont(new Font("Tahoma", Font.BOLD, 12));
        lblOrderDate.setBounds(10, 40, 400, 25);
        contentPane.add(lblOrderDate);

        tableModel = new DefaultTableModel(new Object[]{"Plant Name", "Quantity", "Price"}, 0);
        table = new JTable(tableModel);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBounds(10, 70, 410, 149);
        contentPane.add(scrollPane);

        for (Item item : items) {
            tableModel.addRow(new Object[]{item.getPlantName(), item.getQuantity(), item.getPrice()});
        }
        

        JButton btnClose = new JButton("Close");
        btnClose.setFont(new Font("Tahoma", Font.BOLD, 12));
        btnClose.setBounds(170, 252, 85, 21);
        btnClose.addActionListener(e -> dispose());
        contentPane.add(btnClose);
        
        JLabel totalPriceTxt = new JLabel("Total Price: RM"+totalPrice);
        totalPriceTxt.setHorizontalAlignment(SwingConstants.RIGHT);
        totalPriceTxt.setFont(new Font("Tahoma", Font.BOLD, 12));
        totalPriceTxt.setBounds(197, 229, 223, 13);
        contentPane.add(totalPriceTxt);

        setVisible(true);
	}
	
	public static class Item {
        private String plantName;
        private int quantity;
        private double price;
        //private double totalPrice;

        public Item(String plantName, int quantity, double price) {
            this.plantName = plantName;
            this.quantity = quantity;
            this.price = price;
            //this.totalPrice = totalPrice;
        }

        public String getPlantName() {
            return plantName;
        }

        public int getQuantity() {
            return quantity;
        }

        public double getPrice() {
            return price;
        }
    }
}
