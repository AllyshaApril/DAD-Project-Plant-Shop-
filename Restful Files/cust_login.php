<?php
    // Include the database connection file
    require_once("dbConn.php");

    // Check if the POST request has been made
    if ($_SERVER['REQUEST_METHOD'] == 'POST') {
        // Retrieve the email, password, and access from the POST request
        $email = $_POST['email'];
        $password = $_POST['password'];
        $access = $_POST['access'];

        // Check if email already exists
        $query = "SELECT * FROM account WHERE email = :email";
        $stmt = $dbPDO->prepare($query);
        $stmt->execute(array(':email' => $email));
        $result = $stmt->fetch(PDO::FETCH_ASSOC);

        if ($result) {
            // Check password
            if ($result['password'] == $password) {
                // Password correct, check access
                if ($result['access'] == $access) {
                    // Fetch c_id from customer table using email
                    $query = "SELECT c_id FROM customer WHERE email = :email";
                    $stmt = $dbPDO->prepare($query);
                    $stmt->execute(array(':email' => $email));
                    $customer = $stmt->fetch(PDO::FETCH_ASSOC);

                    if ($customer) {
                        // Successful login
                        $response = array('status' => 'success', 'c_id' => $customer['c_id']);
                    } else {
                        // No corresponding customer found
                        $response = array('status' => 'error', 'message' => 'Customer ID not found');
                    }
                } else {
                    // Incorrect access
                    $response = array('status' => 'error', 'message' => 'Incorrect access');
                }
            } else {
                // Incorrect password
                $response = array('status' => 'error', 'message' => 'Incorrect password');
            }
        } else {
            // Email not found
            $response = array('status' => 'error', 'message' => 'Email not found');
        }

        // Return the JSON response
        echo json_encode($response);
    } else {
        // If the request method is not POST, return an error
        $response = array('status' => 'error', 'message' => 'Invalid request method');
        echo json_encode($response);
    }
?>
