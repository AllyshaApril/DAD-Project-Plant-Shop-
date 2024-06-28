package ShopOwner;

import java.awt.Component;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import Customer.ViewHistory;
import Customer.ViewHistory.Item;

public class Order extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private JTable table;
    private DefaultTableModel tableModel;

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    Order frame = new Order();
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
    public Order() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 765, 500);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(null);

        JLabel lblNewLabel = new JLabel("A.K.A.A Plant Orders");
        lblNewLabel.setFont(new Font("Tahoma", Font.BOLD, 14));
        lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
        lblNewLabel.setBounds(10, 10, 741, 13);
        contentPane.add(lblNewLabel);

        JButton homeBtn = new JButton("Home");
        homeBtn.addActionListener(e -> {
            new Dashboard();
            dispose();
        });
        homeBtn.setFont(new Font("Tahoma", Font.BOLD, 14));
        homeBtn.setBounds(10, 8, 92, 21);
        contentPane.add(homeBtn);

        // Initialize table model
        tableModel = new DefaultTableModel(
                new Object[]{"No.", "Customer ID", "Customer Name", "Order Date", "Purchase ID", "Action"}, 0);

        // Create table and set the model
        table = new JTable(tableModel);

        // Set column sizes
        table.getColumnModel().getColumn(0).setPreferredWidth(40);
        table.getColumnModel().getColumn(1).setPreferredWidth(80);
        table.getColumnModel().getColumn(2).setPreferredWidth(250);
        table.getColumnModel().getColumn(3).setPreferredWidth(150);
        table.getColumnModel().getColumn(4).setPreferredWidth(100);
        table.getColumnModel().getColumn(5).setPreferredWidth(80);

        // Add custom cell renderer and editor for action column
        table.getColumn("Action").setCellRenderer(new ButtonRenderer());
        table.getColumn("Action").setCellEditor(new ButtonEditor());

        // Add the table to a scroll pane
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBounds(25, 50, 703, 390); // Adjust size and position as needed

        // Add the scroll pane to the frame
        contentPane.add(scrollPane);

        // Fetch data from API and populate table
        fetchDataFromApi();

        setVisible(true);
    }

    private void fetchDataFromApi() {
        try {
            String apiUrl = "http://localhost/DAD/s_orders.php";
            URL url = new URL(apiUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            // No need to send any specific data in this case

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder content = new StringBuilder();
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();
            conn.disconnect();

            JSONArray customersOrders = new JSONArray(content.toString());

            for (int i = 0; i < customersOrders.length(); i++) {
                JSONObject customer = customersOrders.getJSONObject(i);
                int customerId = customer.getInt("c_id");
                String customerName = customer.getString("c_name");
                String orderDate = customer.getString("o_date");
                int purchaseId = customer.getInt("purchase_id");

                // Add row to table
                tableModel.addRow(new Object[]{(i + 1), customerId, customerName, orderDate, purchaseId, "View"});
            }

        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }

    class ButtonRenderer extends JButton implements TableCellRenderer {
        public ButtonRenderer() {
            setOpaque(true);
        }

        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                boolean hasFocus, int row, int column) {
            setText((value == null) ? "" : value.toString());
            return this;
        }
    }

    class ButtonEditor extends DefaultCellEditor {
        private JButton button;
        private String label;
        private boolean isPushed;
        private int customerId;
        private int purchaseId;

        public ButtonEditor() {
            super(new JTextField());

            button = new JButton();
            button.setOpaque(true);
            button.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    fireEditingStopped();

                    customerId = (int) table.getValueAt(table.getSelectedRow(), 1);
                    purchaseId = (int) table.getValueAt(table.getSelectedRow(), 4);

                    // Fetch detailed order data and display in ViewOrder frame
                    fetchOrderDetails(customerId,purchaseId);
                }
            });
        }

        private void fetchOrderDetails(int c_id, int purchase_id) {         
            try {
                System.out.println("Cust_View called");
                String apiUrl = "http://localhost/DAD/s_view.php";
                URL url = new URL(apiUrl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setDoOutput(true);

                JSONObject postData = new JSONObject();
                postData.put("c_id", c_id); // Replace with actual customer ID
                postData.put("purchase_id", purchase_id); // Replace with actual purchase ID

                try (OutputStream os = conn.getOutputStream()) {
                    byte[] input = postData.toString().getBytes(StandardCharsets.UTF_8);
                    os.write(input, 0, input.length);
                }

                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder content = new StringBuilder();
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    content.append(inputLine);
                }
                in.close();
                conn.disconnect();

                JSONObject order = new JSONObject(content.toString());

                SwingUtilities.invokeLater(() -> {
                    try {
                        List<ViewOrder.Item> items = new ArrayList<>();
                        double totalPrice = 0.0;

                        JSONArray orderItems = order.getJSONArray("items");

                        for (int j = 0; j < orderItems.length(); j++) {
                            JSONObject item = orderItems.getJSONObject(j);
                            String plantName = item.getString("p_name");
                            int quantity = item.getInt("qty");
                            double price = item.getDouble("total_price");
                            double itemTotalPrice = item.getDouble("total_price");
                            items.add(new ViewOrder.Item(plantName, quantity));

                            totalPrice += itemTotalPrice;
                        }

                        // Create and display the ViewHistory window
                        String orderDate = order.getString("o_date"); // Assuming all items have the same order date
                        String c_name = order.getString("c_name");
                        new ViewOrder(String.valueOf(purchase_id),String.valueOf(c_id), orderDate, c_name, String.format("%.2f", totalPrice), items);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                });
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            
        }

        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row,
                int column) {
            label = (value == null) ? "" : value.toString();
            button.setText(label);
            isPushed = true;
            return button;
        }

        public Object getCellEditorValue() {
            if (isPushed) {
                // Perform desired action on button click
            }
            isPushed = false;
            return label;
        }

        public boolean stopCellEditing() {
            isPushed = false;
            return super.stopCellEditing();
        }

        protected void fireEditingStopped() {
            super.fireEditingStopped();
        }
    }
}
