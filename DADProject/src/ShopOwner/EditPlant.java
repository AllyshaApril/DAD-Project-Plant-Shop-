package ShopOwner;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class EditPlant extends JFrame {

	private Receiver receiver;
    private JLabel lblPlantName;
    private JLabel lblStock;
    private JLabel lblPrice;
    private JTextField txtPlantName;
    private JTextField txtStock;
    private JTextField txtPrice;
    private JButton btnSave;
    private JButton btnDelete;

    // Instance variables to store plant data
    private String plantName;
    private int stock;
    private double price;
    private JPanel panel_1;
    private JLabel lblNewLabel;

    public EditPlant(String plantName, int stock, double price) {
        // Store data passed from PlantInventory
        this.plantName = plantName;
        this.stock = stock;
        this.price = price;

        setTitle("Plant Information");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(400, 250);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel panel = new JPanel();
        getContentPane().add(panel, BorderLayout.CENTER);

        lblPlantName = new JLabel("Plant Name:");
        lblPlantName.setBounds(0, 21, 136, 31);
        lblPlantName.setHorizontalAlignment(SwingConstants.RIGHT);
        txtPlantName = new JTextField(plantName);
        txtPlantName.setBounds(198, 21, 161, 31);
        panel.setLayout(null);
        panel.add(lblPlantName);
        panel.add(txtPlantName);

        lblStock = new JLabel("In Stock:");
        lblStock.setBounds(0, 62, 136, 31);
        lblStock.setHorizontalAlignment(SwingConstants.RIGHT);
        txtStock = new JTextField(String.valueOf(stock));
        txtStock.setBounds(198, 62, 161, 31);
        panel.add(lblStock);
        panel.add(txtStock);

        lblPrice = new JLabel("Price (RM):");
        lblPrice.setBounds(0, 103, 136, 31);
        lblPrice.setHorizontalAlignment(SwingConstants.RIGHT);
        txtPrice = new JTextField(String.valueOf(price));
        txtPrice.setBounds(198, 103, 161, 31);
        panel.add(lblPrice);
        panel.add(txtPrice);

        JPanel buttonPanel = new JPanel();
        getContentPane().add(buttonPanel, BorderLayout.SOUTH);

        btnSave = new JButton("Save");
        btnSave.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Implement save action
                String updatedPlantName = txtPlantName.getText();
                int updatedStock = Integer.parseInt(txtStock.getText());
                double updatedPrice = Double.parseDouble(txtPrice.getText());

                updatePlant(updatedPlantName,updatedStock,updatedPrice);

                //JOptionPane.showMessageDialog(null, "Plant updated successfully!");
                //dispose(); // Close the EditPlant window after saving
            }
        });
        buttonPanel.add(btnSave);

        btnDelete = new JButton("Delete");
        btnDelete.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Implement delete action
                int confirm = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete this plant?",
                        "Confirm Delete", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                	deletePlant(plantName);

                    JOptionPane.showMessageDialog(null, "Plant deleted successfully!");
                    dispose(); // Close the EditPlant window after deleting
                }
            }
        });
        buttonPanel.add(btnDelete);
        
        panel_1 = new JPanel();
        getContentPane().add(panel_1, BorderLayout.NORTH);
        
        lblNewLabel = new JLabel("A.K.A.A Plant Information");
        lblNewLabel.setFont(new Font("Tahoma", Font.BOLD, 14));
        panel_1.add(lblNewLabel);

        // Initialize Receiver instance
        receiver = new Receiver();
        receiver.startReceiver(); // Start listening for incoming connections
        
        setVisible(true);
    }

    // Getter methods to access plant data
    public String getPlantName() {
        return plantName;
    }

    public int getStock() {
        return stock;
    }

    public double getPrice() {
        return price;
    }
    
    private void updatePlant(String updatedPlantName, int updatedStock, double updatedPrice) {
        try {
            String apiUrl = "http://localhost/DAD/updateplant.php";
            URL url = new URL(apiUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("PUT");
            conn.setRequestProperty("Content-Type", "application/json; utf-8");
            conn.setDoOutput(true);

            String jsonInputString = String.format("{\"p_name\": \"%s\", \"p_stock\": %d, \"p_price\": %.2f}",
                    updatedPlantName, updatedStock, updatedPrice);

            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonInputString.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                JOptionPane.showMessageDialog(null, "Plant updated successfully!");
                dispose(); // Close the EditPlant window after saving
            } else {
                JOptionPane.showMessageDialog(null, "Failed to update plant. HTTP error code: " + responseCode);
            }

            conn.disconnect();
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "An error occurred while updating the plant.");
        }
    }

    private void deletePlant(String plantName) {
        try {
            String apiUrl = "http://localhost/DAD/deleteplant.php";
            URL url = new URL(apiUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("DELETE");
            conn.setRequestProperty("Content-Type", "application/json; utf-8");
            conn.setDoOutput(true);

            String jsonInputString = String.format("{\"p_name\": \"%s\"}", plantName);

            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonInputString.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                JOptionPane.showMessageDialog(null, "Plant deleted successfully!");
                dispose(); // Close the EditPlant window after deleting
            } else {
                JOptionPane.showMessageDialog(null, "Failed to delete plant. HTTP error code: " + responseCode);
            }

            conn.disconnect();
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "An error occurred while deleting the plant.");
        }
    }
}
