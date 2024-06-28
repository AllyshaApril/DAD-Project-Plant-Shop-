<?php
    // Include your database connection file
    require_once("dbConn.php");

    // Set the content type for JSON response
    header("Content-Type: application/json");

    try {
        // Get the raw PUT data
        $putData = file_get_contents("php://input");
        error_log("Received data: " . $putData); // Log received data for debugging

        // Decode JSON data
        $data = json_decode($putData, true);
        
        // Check if JSON decoding was successful
        if ($data === null && json_last_error() !== JSON_ERROR_NONE) {
            throw new Exception("Error decoding JSON: " . json_last_error_msg());
        }

        // Extract data from JSON
        $c_name = $data['c_name'];
        $c_no = $data['c_no'];
        $email = $data['email'];
        $password = $data['password'];
        $access = $data['access'];

        // Check if all required fields are present
        if (isset($c_name, $c_no, $email, $password, $access)) {
            // Check if email already exists
            $query = "SELECT COUNT(email) AS count FROM account WHERE email = :email";
            $stmt = $dbPDO->prepare($query);
            $stmt->execute(array(':email' => $email));
        
            // Fetch the result
            $result = $stmt->fetch(PDO::FETCH_ASSOC);

            if ($result['count'] > 0) {
                // Email found
                $response = array('status' => 'error', 'message' => 'Account already exists');
            } else {
                // Insert into account table
                $sql = "INSERT INTO account(email, password, access) VALUES (:email, :password, :access)";
                $stmt = $dbPDO->prepare($sql);
                $stmt->bindParam(':email', $email);
                $stmt->bindParam(':password', $password);
                $stmt->bindParam(':access', $access);

                if ($stmt->execute()) {
                    // Insert into customer table
                    $sql = "INSERT INTO customer(c_name, c_no, email) VALUES (:c_name, :c_no, :email)";
                    $stmt = $dbPDO->prepare($sql);
                    $stmt->bindParam(':c_name', $c_name);
                    $stmt->bindParam(':c_no', $c_no);
                    $stmt->bindParam(':email', $email);

                    if ($stmt->execute()) {
                        echo json_encode(["message" => "Account Created"]);
                    } else {
                        echo json_encode(["message" => "Failed to create customer"]);
                    }
                } else {
                    echo json_encode(["message" => "Failed to create account"]);
                }
            }
        } else {
            echo json_encode(["message" => "Invalid input"]);
        }
    } catch (PDOException $e) {
        echo json_encode(["message" => "Database Error: " . $e->getMessage()]);
    } catch (Exception $e) {
        echo json_encode(["message" => "Error: " . $e->getMessage()]);
    }
?>
