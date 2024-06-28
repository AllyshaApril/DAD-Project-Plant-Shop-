package Customer;

import java.awt.Component;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;
import javax.swing.AbstractCellEditor;
import javax.swing.JButton;
import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import org.json.JSONException;
import org.json.JSONObject;

public class Cust_Plant extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private JTable table;
    public static DefaultTableModel tableModel;
    private DefaultListModel<String> cartModel;
    private HashMap<String, Integer> cartItems;
    private HashMap<String, Double> plantPrices;
    private double totalPrice;
    private ServerSocket serverSocket;

    public Cust_Plant(int c_id) {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("A.K.A.A Flowers");
        setBounds(100, 100, 809, 334); // Increased width to accommodate the cart list
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(null);

        JLabel homeBtn = new JLabel("Welcome to A.K.A.A Flower Shop");
        homeBtn.setFont(new Font("Tahoma", Font.BOLD, 14));
        homeBtn.setHorizontalAlignment(SwingConstants.CENTER);
        homeBtn.setBounds(10, 10, 785, 19);
        contentPane.add(homeBtn);

        JButton btnNewButton = new JButton("Home");
        btnNewButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new Cust_Dashboard(c_id);
                dispose();
            }
        });
        btnNewButton.setFont(new Font("Tahoma", Font.BOLD, 12));
        btnNewButton.setBounds(10, 8, 92, 21);
        contentPane.add(btnNewButton);

        // Initialize table model
        tableModel = new DefaultTableModel(new Object[]{"No.", "Plant Name", "In Stock", "Price (RM)", "Action"}, 0);

        // Create table and set the model
        table = new JTable(tableModel);

        // Set column sizes
        TableColumn columnNo = table.getColumnModel().getColumn(0);
        TableColumn columnPlantName = table.getColumnModel().getColumn(1);
        TableColumn columnInStock = table.getColumnModel().getColumn(2);
        TableColumn columnPrice = table.getColumnModel().getColumn(3);
        TableColumn columnAction = table.getColumnModel().getColumn(4);

        columnNo.setPreferredWidth(40);
        columnPlantName.setPreferredWidth(250);
        columnInStock.setPreferredWidth(80);
        columnPrice.setPreferredWidth(80);
        columnAction.setPreferredWidth(150);

        // Add custom cell renderer and editor for action column
        table.getColumn("Action").setCellRenderer(new ButtonRenderer());
        table.getColumn("Action").setCellEditor(new ButtonEditor());

        // Add the table to a scroll pane
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBounds(25, 50, 524, 200);
        contentPane.add(scrollPane);

        JButton purchaseBtn = new JButton("Check Out");
        purchaseBtn.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		sendPurchaseData(c_id);
        	}
        });
        purchaseBtn.setFont(new Font("Tahoma", Font.BOLD, 12));
        purchaseBtn.setBounds(160, 260, 236, 21);
        contentPane.add(purchaseBtn);

        // Shopping cart label
        JLabel cartLabel = new JLabel("Shopping Cart");
        cartLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        cartLabel.setFont(new Font("Tahoma", Font.BOLD, 14));
        cartLabel.setBounds(575, 27, 200, 19);
        contentPane.add(cartLabel);        

        // Shopping cart list model and list
        cartModel = new DefaultListModel<>();
        JList<String> cartList = new JList<>(cartModel);
        JScrollPane cartScrollPane = new JScrollPane(cartList);
        cartScrollPane.setBounds(575, 50, 200, 200);
        contentPane.add(cartScrollPane);
        cartModel.addElement("Total Price: RM0.00");
        
        JButton clearBtn = new JButton("Clear Cart");
        clearBtn.setFont(new Font("Tahoma", Font.BOLD, 12));
        clearBtn.setBounds(648, 260, 127, 21);
        clearBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                clearCart();
            }
        });
        contentPane.add(clearBtn);

        // HashMaps to keep track of cart items and their quantities and prices
        cartItems = new HashMap<>();
        plantPrices = new HashMap<>();
        
        tableData();
        fetchData();
        setVisible(true);
    }
    
    private void fetchData() {
        Runnable run = new Runnable() {
            @Override
            public void run() {
                try {
                    Socket s = new Socket("10.200.109.19", 8080);
                    DataOutputStream out = new DataOutputStream(s.getOutputStream());

                    out.writeUTF("Cust_Purchase");
                    System.out.println("Cust_Purchase request sent");

                    out.close();
                    s.close();
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(Cust_Plant.this, "Failed to send data: Unknown host", "Error", JOptionPane.ERROR_MESSAGE);
                } catch (IOException e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(Cust_Plant.this, "Failed to send data: I/O error", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        };

        Thread thr1 = new Thread(run);
        thr1.start();
    }

    
    public void tableData() {
        System.out.println("tableData Called");
        Thread thrUDP = new Thread(() -> {
            try {
                serverSocket = new ServerSocket(7000); // Initialize serverSocket here
                System.out.println("Server started on port 7000");
                while (true) {
                    Socket clientSocket = serverSocket.accept();
                    System.out.println("Client connected: " + clientSocket.getInetAddress());

                    try (DataInputStream in = new DataInputStream(clientSocket.getInputStream())) {
                        String jsonData = in.readUTF();
                        //System.out.println("Data received: " + jsonData);

                        JSONObject dataObject = new JSONObject(jsonData);

                        int id = dataObject.getInt("id");
                        String p_name = dataObject.getString("p_name");
                        int p_stock = dataObject.getInt("p_stock");
                        double p_price = dataObject.getDouble("p_price");

                        // Add received data to the tableModel
                        SwingUtilities.invokeLater(() -> {
                            tableModel.addRow(new Object[]{id, p_name, p_stock, p_price, "Purchase"});
                        });

                    } catch (IOException | JSONException e) {
                        e.printStackTrace();
                    } finally {
                        clientSocket.close();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        thrUDP.start();
    }
    
    class ButtonRenderer extends JButton implements TableCellRenderer {
        public ButtonRenderer() {
            setOpaque(true);
        }

        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            setText((value == null) ? "" : value.toString());
            return this;
        }
    }

    class ButtonEditor extends AbstractCellEditor implements TableCellEditor {
        private JButton button;
        private String label;
        private boolean isPushed;

        public ButtonEditor() {
            button = new JButton();
            button.setOpaque(true);
            button.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    fireEditingStopped();
                    int selectedRow = table.getSelectedRow();
                    String plantName = (String) table.getValueAt(selectedRow, 1);
                    double plantPrice = (double) table.getValueAt(selectedRow, 3);

                    // Update the shopping cart
                    cartItems.put(plantName, cartItems.getOrDefault(plantName, 0) + 1);
                    plantPrices.put(plantName, plantPrice);
                    updateCart();
                }
            });
        }

        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            label = (value == null) ? "" : value.toString();
            button.setText(label);
            isPushed = true;
            return button;
        }

        public Object getCellEditorValue() {
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

    private void updateCart() {
        cartModel.clear();
        totalPrice = 0;
        for (String item : cartItems.keySet()) {
            int quantity = cartItems.get(item);
            double price = plantPrices.get(item);
            double itemTotal = quantity * price;
            cartModel.addElement(item + " - Qty: " + quantity + " - Price: RM" + price );
            totalPrice += itemTotal;
        }
        cartModel.addElement("\n\nTotal Price: RM" + totalPrice);
    }

    private void clearCart() {
        cartItems.clear();
        plantPrices.clear();
        cartModel.clear();
        totalPrice = 0;
        cartModel.addElement("Total Price: RM0.00");
    }
    
    public void sendPurchaseData(int c_id) {
        try {
            Socket s = new Socket("10.200.109.19", 8080); // Change if necessary
            DataOutputStream out = new DataOutputStream(s.getOutputStream());

            out.writeUTF("Check Out");
            out.writeUTF(String.valueOf(c_id));

            // Send cart items data
            out.writeInt(cartItems.size()); // Number of items in cart
            for (String item : cartItems.keySet()) {
                out.writeUTF(item); // Plant name
                out.writeInt(cartItems.get(item)); // Quantity
            }

            out.close();
            s.close();
            
            clearCart();
        } catch (UnknownHostException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(Cust_Plant.this, "Failed to send data: Unknown host", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (IOException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(Cust_Plant.this, "Failed to send data: I/O error", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
