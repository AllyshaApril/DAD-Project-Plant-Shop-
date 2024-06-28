package ShopOwner;

import java.awt.Component;
import java.awt.EventQueue;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;

import javax.swing.AbstractCellEditor;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import org.json.JSONArray;
import org.json.JSONObject;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class PlantInventory extends JFrame {

	private Receiver receiver;
    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private JTable table;
    private DefaultTableModel tableModel;
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
                    PlantInventory frame = new PlantInventory();
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
    public PlantInventory() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("Plant Inventory");
        setBounds(100, 100, 586, 300);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(null);

        JLabel homeBtn = new JLabel("A.K.A.A Plant Inventory");
        homeBtn.setFont(new Font("Tahoma", Font.BOLD, 14));
        homeBtn.setHorizontalAlignment(SwingConstants.CENTER);
        homeBtn.setBounds(10, 10, 552, 13);
        contentPane.add(homeBtn);

        JButton btnNewButton = new JButton("Home");
        btnNewButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new Dashboard();
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
        scrollPane.setBounds(25, 50, 524, 169); // Set position and size of the scroll pane

        // Add the scroll pane to the frame
        contentPane.add(scrollPane);

        JButton addBtn = new JButton("Add Plant");
        addBtn.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		new AddPlant();
        	}
        });
        addBtn.setFont(new Font("Tahoma", Font.BOLD, 12));
        addBtn.setBounds(422, 229, 127, 21);
        contentPane.add(addBtn);

        // Fetch data from API and populate table
        fetchDataFromApi();

        // Initialize Receiver instance
        receiver = new Receiver();
        receiver.startReceiver(); // Start listening for incoming connections
        
        setVisible(true);
    }

    private void fetchDataFromApi() {
        try {
            String apiUrl = "http://localhost/DAD/plants.php"; 
            URL url = new URL(apiUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String inputLine;
            StringBuffer content = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();
            conn.disconnect();

            System.out.println("API Response: " + content.toString());

            JSONArray plants = new JSONArray(content.toString());

            for (int i = 0; i < plants.length(); i++) {
                JSONObject plant = plants.getJSONObject(i);
                int id = i+1;
                String name = plant.getString("p_name");
                int stock = plant.getInt("p_stock");
                double price = plant.getDouble("p_price");
                tableModel.addRow(new Object[]{id, name, stock, price, "Edit"});
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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
                    int stock = (int) table.getValueAt(selectedRow, 2);
                    double price = (double) table.getValueAt(selectedRow, 3);
                    
                    // Add your edit and delete actions here
                    if (label.equals("Edit")) {
                        System.out.println("Edit button clicked");
                        //JOptionPane.showMessageDialog(null, "Edit button clicked");
                        //EditPlant editPlant = new EditPlant(plantName, stock, price);
                        //editPlant.setVisible(true);
                        
                    }
                    
                    EventQueue.invokeLater(new Runnable() {
                        public void run() {
                            try {
                            	//JOptionPane.showMessageDialog(null, "Edit button clicked");
                                EditPlant editPlant = new EditPlant(plantName, stock, price);
                                editPlant.setVisible(true);
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        }
                    });
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
