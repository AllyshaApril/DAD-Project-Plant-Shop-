package ShopOwner;

import javax.swing.*;
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.*;

import Customer.Cust_Dashboard;
import Customer.ViewHistory;

public class Receiver {

    public void startReceiver() {
        Thread thrUDP = new Thread(() -> {
            try (ServerSocket serverSocket = new ServerSocket(8080)) {
                while (true) {
                    Socket clientSocket = serverSocket.accept();
                    DataInputStream in = new DataInputStream(clientSocket.getInputStream());

                    String action = in.readUTF();
                    JFrame frame = new JFrame(action);

                    switch (action) {
                        case "Cust_SignUp":
                            String name = in.readUTF();
                            String num = in.readUTF();
                            String email = in.readUTF();
                            String pwd = in.readUTF();

                            cust_SignUp(name, num, email, pwd, frame);
                            break;
                        case "Cust_Login":
                            String loginEmail = in.readUTF();
                            String loginPwd = in.readUTF();
                            String access = in.readUTF();

                            cust_Login(loginEmail, loginPwd, access, frame);
                            break;
                        case "Cust_Purchase":
                            System.out.println("Cust_Purchase action received");
                            fetchDataFromApi("Cust_Purchase", "", "");
                            break;
                        case "Check Out":
                            String customerId = in.readUTF();
                            processPurchase(customerId, clientSocket);
                            break;
                        case "Cust_Order":
                            String customerIdOrder = in.readUTF();
                            System.out.println("Cust_Order action received");
                            fetchDataFromApi("Cust_Order", customerIdOrder, "");
                            break;
                        case "Cust_View":
                            String customerIdView = in.readUTF();
                            String purchaseId = in.readUTF();
                            System.out.println("Cust_View action received");
                            fetchDataFromApi("Cust_View", customerIdView, purchaseId);
                            break;
                        case "Hello":
                        	fetchDataFromApi("Hello", "", "");
                        	break;
                        default:
                            System.out.println("Unknown action: " + action);
                    }

                    in.close();
                    clientSocket.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        thrUDP.start();
    }

    private void cust_SignUp(String name, String num, String email, String pwd, JFrame frame) {
        try {
            String access = "Customer";
            URL url = new URL("http://localhost/DAD/cust_signup.php");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/json");

            String jsonInputString = "{\"c_name\": \"" + name + "\", \"c_no\": \"" + num + "\", \"email\": \"" + email + "\", \"password\": \"" + pwd + "\", \"access\": \"" + access + "\"}";

            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                System.out.println("Data successfully sent to API");
                JOptionPane.showMessageDialog(frame, "Successfully Created Account");
            } else {
                System.out.println("Failed to send data to API. Response code: " + responseCode);
                JOptionPane.showMessageDialog(frame, "Failed to send data to API. Response code: " + responseCode);
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, "An error occurred: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cust_Login(String email, String password, String access, JFrame frame) {
        try {
            List<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("email", email));
            params.add(new BasicNameValuePair("password", password));
            params.add(new BasicNameValuePair("access", access));

            String strUrl = "http://localhost/DAD/cust_login.php";
            JSONObject jsonResponse = makeHttpRequest(strUrl, "POST", params);

            if (jsonResponse != null) {
                if (jsonResponse.getString("status").equals("success")) {
                    int c_id = jsonResponse.getInt("c_id");

                    SwingUtilities.invokeLater(() -> {
                        //JOptionPane.showMessageDialog(frame, "Login successful!");

                        try {
                            Socket s = new Socket("10.200.109.19", 5000);
                            DataOutputStream out = new DataOutputStream(s.getOutputStream());
                            out.writeUTF("Login Successful");
                            out.writeUTF(String.valueOf(c_id));
                            out.close();
                            s.close();
                            System.out.println("Cust_Login action sent");
                        } catch (UnknownHostException e) {
                            e.printStackTrace();
                            JOptionPane.showMessageDialog(frame, "Failed to send data: Unknown host", "Error", JOptionPane.ERROR_MESSAGE);
                        } catch (IOException e) {
                            e.printStackTrace();
                            JOptionPane.showMessageDialog(frame, "Failed to send data: I/O error", "Error", JOptionPane.ERROR_MESSAGE);
                        }

                        //new Cust_Dashboard(c_id);
                    });
                } else {
                    String errorMessage = jsonResponse.getString("message");
                    SwingUtilities.invokeLater(() -> {
                        JOptionPane.showMessageDialog(frame, errorMessage, "Login Error", JOptionPane.ERROR_MESSAGE);
                    });
                }
            } else {
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(frame, "Unknown error occurred", "Login Error", JOptionPane.ERROR_MESSAGE);
                });
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            SwingUtilities.invokeLater(() -> {
                JOptionPane.showMessageDialog(frame, "An error occurred: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            });
        }
    }

    private JSONObject makeHttpRequest(String url, String method, List<NameValuePair> params) {
        InputStream is = null;
        String json = "";
        JSONObject jObj = null;

        try {
            URL urlObj = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) urlObj.openConnection();
            conn.setRequestMethod(method);
            conn.setDoOutput(true);

            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, StandardCharsets.UTF_8));
            writer.write(getQuery(params));
            writer.flush();
            writer.close();
            os.close();

            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = in.readLine()) != null) {
                    response.append(line);
                }
                in.close();
                json = response.toString();
            }

            jObj = new JSONObject(json);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return jObj;
    }

    private String getQuery(List<NameValuePair> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for (NameValuePair pair : params) {
            if (first) {
                first = false;
            } else {
                result.append("&");
            }
            result.append(URLEncoder.encode(pair.getName(), StandardCharsets.UTF_8));
            result.append("=");
            result.append(URLEncoder.encode(pair.getValue(), StandardCharsets.UTF_8));
        }
        return result.toString();
    }

    private void fetchDataFromApi(String purpose, String c_id, String purchase_id) {
        System.out.println("fetchDataFromApi called");
        System.out.println("Purchase ID: " + purchase_id);

        if ("Cust_Purchase".equals(purpose)) {
            try {
                String apiUrl = "http://localhost/DAD/plants.php";
                URL url = new URL(apiUrl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");

                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder content = new StringBuilder();
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    content.append(inputLine);
                }
                in.close();
                conn.disconnect();

                System.out.println("API Response: " + content.toString());

                JSONArray plants = new JSONArray(content.toString());
                
                for (int i = 0; i < plants.length(); i++) {
                    JSONObject plant = plants.getJSONObject(i);
                    int id = i + 1;
                    String name = plant.getString("p_name");
                    int stock = plant.getInt("p_stock");
                    double price = plant.getDouble("p_price");
                    System.out.println("Adding row: " + id + ", " + name + ", " + stock + ", " + price);
                    //Customer.Cust_Plant.tableModel.addRow(new Object[]{id, name, stock, price, "Purchase"});
                    
                    sendDataToSocket(id,name,stock,price);
                }
                
                /*SwingUtilities.invokeLater(() -> {
                    try {
                        for (int i = 0; i < plants.length(); i++) {
                            JSONObject plant = plants.getJSONObject(i);
                            int id = i + 1;
                            String name = plant.getString("p_name");
                            int stock = plant.getInt("p_stock");
                            double price = plant.getDouble("p_price");
                            System.out.println("Adding row: " + id + ", " + name + ", " + stock + ", " + price);
                            Customer.Cust_Plant.tableModel.addRow(new Object[]{id, name, stock, price, "Purchase"});
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                });*/
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }        	
        } else if ("Cust_Order".equals(purpose)) {            
        	try {
                System.out.println("Cust_Order called");
                String apiUrl = "http://localhost/DAD/cust_history.php";
                URL url = new URL(apiUrl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setDoOutput(true);

                JSONObject postData = new JSONObject();
                postData.put("c_id", c_id); // Replace with actual customer ID

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

                System.out.println("API Response: " + content.toString());

                JSONArray orders = new JSONArray(content.toString());

                // Send each order received to the socket
                for (int i = 0; i < orders.length(); i++) {
                    JSONObject order = orders.getJSONObject(i);
                    int id = i + 1;
                    int purchaseId = order.getInt("purchase_id");
                    String orderDate = order.getString("o_date");
                    double totalPrice = order.getDouble("total");

                    sendDataToSocket(id, purchaseId, orderDate, totalPrice);
                }

            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }

        } else if ("Cust_View".equals(purpose)) {
        	try {
                System.out.println("Cust_View called");
                String apiUrl = "http://localhost/DAD/cust_history.php";
                URL url = new URL(apiUrl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setDoOutput(true);

                JSONObject postData = new JSONObject();
                postData.put("c_id", c_id);
                postData.put("purchase_id", purchase_id);

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

                System.out.println("API Response: " + content.toString());

                // Parse JSON response
                JSONObject order = new JSONObject(content.toString());

                JSONArray items = order.getJSONArray("items");
                String orderDate = order.getString("o_date");
                double totalPrice = order.getDouble("total"); // Uncomment if 'total' is included

                JSONObject dataObject = new JSONObject();
                dataObject.put("purchaseId", purchase_id);
                dataObject.put("orderDate", orderDate);
                dataObject.put("totalPrice", totalPrice); // Uncomment if 'total' is included
                dataObject.put("items", items);

                sendDataToSocket(dataObject);
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
        }
    }
    
    private void sendDataToSocket(JSONObject dataObject) {
        Runnable run = new Runnable() {
            @Override
            public void run() {
                try {
                    Socket s = new Socket("10.200.109.19", 10000);
                    DataOutputStream out = new DataOutputStream(s.getOutputStream());

                    out.writeUTF(dataObject.toString());
                    System.out.println("Data sent to socket 10000");

                    out.close();
                    s.close();
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Failed to send data: Unknown host", "Error", JOptionPane.ERROR_MESSAGE);
                } catch (IOException e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Failed to send data: I/O error", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        };

        Thread thr = new Thread(run);
        thr.start();
    }
    
    private void sendDataToSocket(int id, String p_name, int p_stock, double p_price) {
        Runnable run = new Runnable() {
            @Override
            public void run() {
                try {
                    Socket s = new Socket("10.200.109.19", 7000);
                    DataOutputStream out = new DataOutputStream(s.getOutputStream());

                    JSONObject dataObject = new JSONObject();
                    dataObject.put("id", id);
                    dataObject.put("p_name", p_name);
                    dataObject.put("p_stock", p_stock);
                    dataObject.put("p_price", p_price);

                    out.writeUTF(dataObject.toString());
                    System.out.println("Data sent to socket 7000");

                    out.close();
                    s.close();
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Failed to send data: Unknown host", "Error", JOptionPane.ERROR_MESSAGE);
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Failed to send data: I/O error", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        };

        Thread thr = new Thread(run);
        thr.start();
    }
    
    private void sendDataToSocket(int id, int purchaseId, String orderDate, double totalPrice) {
        Runnable run = new Runnable() {
            @Override
            public void run() {
                try {
                    Socket s = new Socket("10.200.109.19", 9999);
                    DataOutputStream out = new DataOutputStream(s.getOutputStream());

                    JSONObject dataObject = new JSONObject();
                    dataObject.put("id", id);
                    dataObject.put("purchaseId", purchaseId);
                    dataObject.put("orderDate", orderDate);
                    dataObject.put("totalPrice", totalPrice);

                    out.writeUTF(dataObject.toString());
                    System.out.println("Data sent to socket 9999");

                    out.close();
                    s.close();
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Failed to send data: Unknown host", "Error", JOptionPane.ERROR_MESSAGE);
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Failed to send data: I/O error", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        };

        Thread thr = new Thread(run);
        thr.start();
    }

    private void processPurchase(String c_id, Socket clientSocket) {        
        try {
            DataInputStream in = new DataInputStream(clientSocket.getInputStream());

            // Read number of items in cart
            int itemCount = in.readInt();
            JSONArray ordersArray = new JSONArray();

            for (int i = 0; i < itemCount; i++) {
                String p_name = in.readUTF();
                int qty = in.readInt();

                JSONObject orderItem = new JSONObject();
                orderItem.put("p_name", p_name);
                orderItem.put("qty", qty);

                ordersArray.put(orderItem);
            }

            JSONObject postData = new JSONObject();
            postData.put("c_id", c_id);
            postData.put("orders", ordersArray);

            // Send data to backend endpoint
            URL url = new URL("http://localhost/DAD/cust_order.php");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = postData.toString().getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            // Check HTTP response code
            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"))) {
                    StringBuilder response = new StringBuilder();
                    String responseLine;
                    while ((responseLine = br.readLine()) != null) {
                        response.append(responseLine.trim());
                    }
                    System.out.println("API Response: " + response.toString());
                    // Handle API response if needed
                    SwingUtilities.invokeLater(() -> {
                        JOptionPane.showMessageDialog(null, "Purchase completed successfully");
                    });
                }
            } else {
                System.out.println("Failed to send data to API. Response code: " + responseCode);
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(null, "Failed to complete purchase");
                });
            }

            in.close();
            conn.disconnect();
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Receiver receiver = new Receiver();
        receiver.startReceiver();
    }
}
