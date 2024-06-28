package Customer;

import java.awt.Component;
import java.awt.EventQueue;
import javax.swing.AbstractCellEditor;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import Customer.ViewHistory.Item;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

public class Order_Status extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private JTable table;
    public static DefaultTableModel tableModel;
    private JButton btnNewButton;
    private ServerSocket serverSocket;

    /**
     * Create the frame.
     */
    public Order_Status(int c_id) {
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Use DISPOSE_ON_CLOSE instead of EXIT_ON_CLOSE
        setBounds(100, 100, 509, 300);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(null);

        JLabel lblNewLabel = new JLabel("Order History");
        lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
        lblNewLabel.setFont(new Font("Tahoma", Font.BOLD, 14));
        lblNewLabel.setBounds(10, 10, 485, 17);
        contentPane.add(lblNewLabel);

        // Initialize table model
        tableModel = new DefaultTableModel(new Object[]{"No.", "Purchase ID", "Order Date", "Total Price (RM)", "View"}, 0);

        // Create table and set the model
        table = new JTable(tableModel);

        // Set column sizes
        TableColumn columnNo = table.getColumnModel().getColumn(0);
        TableColumn columnpurID = table.getColumnModel().getColumn(1);
        TableColumn columnOrderDate = table.getColumnModel().getColumn(2);
        TableColumn columnTotalPrice = table.getColumnModel().getColumn(3);
        TableColumn columnView = table.getColumnModel().getColumn(4);

        columnNo.setPreferredWidth(40);
        columnpurID.setPreferredWidth(100);
        columnOrderDate.setPreferredWidth(100);
        columnTotalPrice.setPreferredWidth(100);
        columnView.setPreferredWidth(100);

        // Add custom cell renderer and editor for action column
        columnView.setCellRenderer(new ButtonRenderer());
        columnView.setCellEditor(new ButtonEditor(c_id));

        // Add the table to a scroll pane
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBounds(25, 50, 452, 169); // Set position and size of the scroll pane

        // Add the scroll pane to the frame
        contentPane.add(scrollPane);

        btnNewButton = new JButton("Back");
        btnNewButton.setFont(new Font("Tahoma", Font.BOLD, 12));
        btnNewButton.setBounds(213, 229, 85, 21);
        contentPane.add(btnNewButton);

        btnNewButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new Cust_Dashboard(c_id);
                dispose();
            }
        });
        hello();
        // Call tableData method after initializing the frame components
        tableData();
        
        fetchData(c_id);  
        setVisible(true);
    }
    
    public void hello() {
    	System.out.println("\n\nHELLO WORLD\n\n");
    	System.out.flush();
    }
    
    public void fetchData(int c_id) {
        Runnable run = new Runnable() {
            @Override
            public void run() {
                try {
                    Socket s = new Socket("10.200.109.19", 8080);
                    DataOutputStream out = new DataOutputStream(s.getOutputStream());

                    out.writeUTF("Cust_Order");
                    out.writeUTF(String.valueOf(c_id));
                    System.out.println("Cust_Order request sent");  // Ensure this line is reached

                    out.close();
                    s.close();

                    System.out.println("\nSOMETHING SOMETHONG\n");  // Ensure this line is reached

                } catch (UnknownHostException e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(Order_Status.this, "Failed to send data: Unknown host", "Error", JOptionPane.ERROR_MESSAGE);
                } catch (IOException e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(Order_Status.this, "Failed to send data: I/O error", "Error", JOptionPane.ERROR_MESSAGE);
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
                serverSocket = new ServerSocket(9999); // Initialize serverSocket here
                System.out.println("Server started on port 9999");
                while (true) {
                    Socket clientSocket = serverSocket.accept();
                    System.out.println("Client connected: " + clientSocket.getInetAddress());

                    try (DataInputStream in = new DataInputStream(clientSocket.getInputStream())) {
                        String jsonData = in.readUTF();
                        //System.out.println("Data received: " + jsonData);

                        JSONObject dataObject = new JSONObject(jsonData);

                        int id = dataObject.getInt("id");
                        int purchaseId = dataObject.getInt("purchaseId");
                        String orderDate = dataObject.getString("orderDate");
                        double totalPrice = dataObject.getDouble("totalPrice");

                        // Add received data to the tableModel
                        SwingUtilities.invokeLater(() -> {
                            tableModel.addRow(new Object[]{id, purchaseId, orderDate, totalPrice, "View"});
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

    @Override
    public void dispose() {
        super.dispose();
        if (serverSocket != null && !serverSocket.isClosed()) {
            try {
                serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    class ButtonRenderer extends JButton implements TableCellRenderer {
        public ButtonRenderer() {
            setOpaque(true);
        }

        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            setText((value == null) ? "View" : value.toString());
            return this;
        }
    }

    public class ButtonEditor extends AbstractCellEditor implements TableCellEditor {
        private JButton button;
        private String label;
        private boolean isPushed;
        private int c_id;

        public ButtonEditor(int c_id) {
            this.c_id = c_id;
            button = new JButton();
            button.setOpaque(true);
            button.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    fireEditingStopped();
                    int selectedRow = table.getSelectedRow();
                    int purchase_id = (int) table.getValueAt(selectedRow, 1);
                    //Object purchaseIdObj = table.getValueAt(selectedRow, 1);
                    //String purchase_id = (purchaseIdObj instanceof Integer) ? String.valueOf(purchaseIdObj) : (String) purchaseIdObj;
                    //int p_id = Integer.valueOf(purchase_id);
                    
                    Runnable run = new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Socket s = new Socket("10.200.109.19", 8080);
                                DataOutputStream out = new DataOutputStream(s.getOutputStream());

                                out.writeUTF("Cust_View");
                                out.writeUTF(String.valueOf(c_id));
                                out.writeUTF(String.valueOf(purchase_id));
                                System.out.println("Cust_View request sent");

                                out.close();
                                s.close();
                                
                                serverSocket = new ServerSocket(10000); // Initialize serverSocket here
                                while (true) {
                                    Socket clientSocket = serverSocket.accept();
                                    DataInputStream in = new DataInputStream(clientSocket.getInputStream());

                                    String jsonData = in.readUTF();
                                    in.close();
                                    clientSocket.close();

                                    System.out.println("Data received: " + jsonData);

                                    JSONObject dataObject = new JSONObject(jsonData);
                                    String orderDate = dataObject.getString("orderDate");
                                    double totalPrice = dataObject.getDouble("totalPrice");
                                    JSONArray itemsArray = dataObject.getJSONArray("items");
                                    
                                    List<ViewHistory.Item> items = new ArrayList<>();
                                    for (int i = 0; i < itemsArray.length(); i++) {
                                        JSONObject itemObject = itemsArray.getJSONObject(i);
                                        String plantName = itemObject.getString("p_name");
                                        int quantity = itemObject.getInt("qty");
                                        double price = itemObject.getDouble("p_price");

                                        ViewHistory.Item item = new ViewHistory.Item(plantName, quantity, price);
                                        items.add(item);
                                    }

                                    SwingUtilities.invokeLater(() -> {
                                    	new ViewHistory(purchase_id, orderDate, totalPrice,items);
                                        //new ViewHistory(Integer.parseInt(purchase_id), orderDate, totalPrice, items);
                                    });
                                                                
                                //Socket viewSocket = new Socket("10.200.109.19", 10000);
                                //DataInputStream in = new DataInputStream(viewSocket.getInputStream());
                                
                                //String orderDate = in.readUTF();
                                //String totalPrice = in.readUTF();
                                /*String jsonData = in.readUTF();
                                in.close();
                                viewSocket.close();

                                System.out.println("Data received: " + jsonData);

                                JSONObject dataObject = new JSONObject(jsonData);
                                String orderDate = dataObject.getString("orderDate");
                                double totalPrice = dataObject.getDouble("totalPrice");
                                JSONArray itemsArray = dataObject.getJSONArray("items");
                                
                                List<ViewHistory.Item> items = new ArrayList<>();
                                for (int i = 0; i < itemsArray.length(); i++) {
                                    JSONObject itemObject = itemsArray.getJSONObject(i);
                                    String plantName = itemObject.getString("p_name");
                                    int quantity = itemObject.getInt("qty");
                                    double price = itemObject.getDouble("p_price");

                                    ViewHistory.Item item = new ViewHistory.Item(plantName, quantity, price);
                                    items.add(item);
                                }

                                SwingUtilities.invokeLater(() -> {
                                	new ViewHistory(purchase_id, orderDate, totalPrice,items);
                                    //new ViewHistory(Integer.parseInt(purchase_id), orderDate, totalPrice, items);
                                });*/
                            }} catch (UnknownHostException ex) {
                                ex.printStackTrace();
                                JOptionPane.showMessageDialog(Order_Status.this, "Failed to connect to server: Unknown host", "Error", JOptionPane.ERROR_MESSAGE);
                            } catch (IOException ex) {
                                ex.printStackTrace();
                                JOptionPane.showMessageDialog(Order_Status.this, "Failed to connect to server: I/O error", "Error", JOptionPane.ERROR_MESSAGE);
                            } catch (JSONException ex) {
                                ex.printStackTrace();
                                JOptionPane.showMessageDialog(Order_Status.this, "Failed to parse JSON data", "Error", JOptionPane.ERROR_MESSAGE);
                            }
                        }
                    };

                    Thread thr2 = new Thread(run);
                    thr2.start();
                }
            });
        }

        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            label = (value == null) ? "View" : value.toString();
            button.setText(label);
            isPushed = true;
            return button;
        }

        public Object getCellEditorValue() {
            if (isPushed) {
                // Add your edit and delete actions here
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
