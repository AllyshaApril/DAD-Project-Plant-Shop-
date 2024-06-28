package ShopOwner;

import java.awt.EventQueue;
import java.awt.Font;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import org.json.JSONException;
import org.json.JSONObject;

import Customer.ViewHistory.Item;
import javax.swing.SwingConstants;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.awt.event.ActionEvent;

public class ViewOrder extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTable table;
    private DefaultTableModel tableModel;
	private String c_id;
    
	/**
	 * Launch the application.
	 */
	/*public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ViewOrder frame = new ViewOrder();
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
    public ViewOrder(String purchase_id, String c_id, String orderDate, String c_name, String total_price, List<Item> items) {
        this.c_id = c_id;
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setBounds(100, 100, 450, 320);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(null);

        JLabel lblOrderDetails = new JLabel("Order Details for Customer: " + c_name + " (" + c_id + ")");
        lblOrderDetails.setFont(new Font("Tahoma", Font.BOLD, 14));
        lblOrderDetails.setBounds(10, 10, 426, 25);
        contentPane.add(lblOrderDetails);

        JLabel lblOrderDate = new JLabel("Order Date: " + orderDate);
        lblOrderDate.setFont(new Font("Tahoma", Font.BOLD, 12));
        lblOrderDate.setBounds(10, 40, 400, 25);
        contentPane.add(lblOrderDate);

        tableModel = new DefaultTableModel(new Object[]{"No.", "Plant Name", "Quantity"}, 0);
        table = new JTable(tableModel);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBounds(10, 70, 410, 149);
        contentPane.add(scrollPane);

        int num = 1;
        for (Item item : items) {
            tableModel.addRow(new Object[]{num, item.getPlantName(), item.getQuantity()});
            num++;
        }

        JLabel totalPriceTxt = new JLabel("Total Price: RM" + total_price);
        totalPriceTxt.setHorizontalAlignment(SwingConstants.RIGHT);
        totalPriceTxt.setFont(new Font("Tahoma", Font.BOLD, 12));
        totalPriceTxt.setBounds(212, 229, 208, 13);
        contentPane.add(totalPriceTxt);

        JButton btnClose = new JButton("Paid");
        btnClose.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Call method to update status to "Paid"
                updatePurchaseStatus(c_id, purchase_id);

                // Show payment accepted message
                JOptionPane.showMessageDialog(null, "Payment Accepted");

                // Close the JFrame
                dispose();
            }
        });
        btnClose.setFont(new Font("Tahoma", Font.BOLD, 12));
        btnClose.setBounds(170, 252, 85, 21);
        contentPane.add(btnClose);

        setVisible(true);
    }

    private void updatePurchaseStatus(String c_id, String purchase_id) {
    	 try {
             String apiUrl = "http://localhost/DAD/paid.php";
             JSONObject postData = new JSONObject();
             try {
				postData.put("c_id", c_id);
				postData.put("purchase_id", purchase_id);
			} catch (JSONException e) {				
				e.printStackTrace();
			}
             

             HttpURLConnection conn = (HttpURLConnection) new URL(apiUrl).openConnection();
             conn.setRequestMethod("POST");
             conn.setRequestProperty("Content-Type", "application/json");
             conn.setDoOutput(true);

             try (OutputStream os = conn.getOutputStream()) {
                 os.write(postData.toString().getBytes());
                 os.flush();
             }

             if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                 throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
             }

             BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
             String output;
             while ((output = br.readLine()) != null) {
                 System.out.println(output);
             }

             conn.disconnect();
         } catch (MalformedURLException e) {
             e.printStackTrace();
         } catch (IOException e) {
             e.printStackTrace();
         }
    }

    public static class Item {
        private String plantName;
        private int quantity;

        public Item(String plantName, int quantity) {
            this.plantName = plantName;
            this.quantity = quantity;
        }

        public String getPlantName() {
            return plantName;
        }

        public int getQuantity() {
            return quantity;
        }

    }

}
