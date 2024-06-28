package ShopOwner;

import java.awt.EventQueue;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import java.awt.Font;
import javax.swing.JTextField;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.nio.charset.StandardCharsets;
import java.awt.event.ActionEvent;

public class AddPlant extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private JTextField nameTxt;
    private JLabel lblNewLabel_2;
    private JTextField stockTxt;
    private JLabel lblNewLabel_3;
    private JTextField priceTxt;
    private JButton saveBtn;
    private JButton cancelBtn;

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    AddPlant frame = new AddPlant();
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
    public AddPlant() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("Add New Plant");
        setBounds(100, 100, 450, 300);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

        setContentPane(contentPane);
        contentPane.setLayout(null);

        JLabel lblNewLabel = new JLabel("A.K.A.A New Plant");
        lblNewLabel.setFont(new Font("Tahoma", Font.BOLD, 14));
        lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
        lblNewLabel.setBounds(10, 10, 426, 19);
        contentPane.add(lblNewLabel);

        JLabel lblNewLabel_1 = new JLabel("Plant Name :");
        lblNewLabel_1.setFont(new Font("Tahoma", Font.BOLD, 12));
        lblNewLabel_1.setHorizontalAlignment(SwingConstants.RIGHT);
        lblNewLabel_1.setBounds(10, 67, 134, 13);
        contentPane.add(lblNewLabel_1);

        nameTxt = new JTextField();
        nameTxt.setBounds(164, 65, 224, 19);
        contentPane.add(nameTxt);
        nameTxt.setColumns(10);

        lblNewLabel_2 = new JLabel("In Stock (Quantity) :");
        lblNewLabel_2.setHorizontalAlignment(SwingConstants.RIGHT);
        lblNewLabel_2.setFont(new Font("Tahoma", Font.BOLD, 12));
        lblNewLabel_2.setBounds(10, 103, 134, 13);
        contentPane.add(lblNewLabel_2);

        stockTxt = new JTextField();
        stockTxt.setColumns(10);
        stockTxt.setBounds(164, 101, 224, 19);
        contentPane.add(stockTxt);

        lblNewLabel_3 = new JLabel("Price (RM) :");
        lblNewLabel_3.setHorizontalAlignment(SwingConstants.RIGHT);
        lblNewLabel_3.setFont(new Font("Tahoma", Font.BOLD, 12));
        lblNewLabel_3.setBounds(10, 139, 134, 13);
        contentPane.add(lblNewLabel_3);

        priceTxt = new JTextField();
        priceTxt.setColumns(10);
        priceTxt.setBounds(164, 137, 224, 19);
        contentPane.add(priceTxt);

        saveBtn = new JButton("Save");
        saveBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String plantName = nameTxt.getText();
                String plantStock = stockTxt.getText();
                String plantPrice = priceTxt.getText();

                try {
                    // Create URL object
                    URL url = new URL("http://localhost/DAD/add_plant.php"); // Replace with your server URL

                    // Create connection object
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("POST");
                    connection.setRequestProperty("Content-Type", "application/json; utf-8");
                    connection.setRequestProperty("Accept", "application/json");
                    connection.setDoOutput(true);

                    // Create JSON string
                    String jsonInputString = String.format("{\"p_name\": \"%s\", \"p_stock\": \"%s\", \"p_price\": \"%s\"}",
                            plantName, plantStock, plantPrice);

                    // Write JSON input string to the output stream
                    try (OutputStream os = connection.getOutputStream()) {
                        byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
                        os.write(input, 0, input.length);
                    }

                    // Check for the response code
                    int responseCode = connection.getResponseCode();
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        System.out.println("Plant added successfully");
                        JOptionPane.showMessageDialog(null, "Plant added successfully");
                        dispose();
                        
                    } else {
                        System.out.println("Failed to add plant");
                    }

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
        saveBtn.setFont(new Font("Tahoma", Font.BOLD, 12));
        saveBtn.setBounds(122, 187, 85, 21);
        contentPane.add(saveBtn);

        cancelBtn = new JButton("Cancel");
        cancelBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        cancelBtn.setFont(new Font("Tahoma", Font.BOLD, 12));
        cancelBtn.setBounds(215, 188, 85, 21);
        contentPane.add(cancelBtn);

        setVisible(true);
    }
}
