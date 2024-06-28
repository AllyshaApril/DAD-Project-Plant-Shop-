<?php
    require_once("dbConn.php");

    header("Content-Type: application/json");

    try {
        // Get the raw POST data
        $postData = file_get_contents("php://input");
        $data = json_decode($postData, true);

        // Validate incoming data
        if (isset($data['p_name']) && isset($data['p_stock']) && isset($data['p_price'])) {
            $p_name = $data['p_name'];
            $p_stock = $data['p_stock'];
            $p_price = $data['p_price'];

            // Insert plant data into the database
            $sql = "INSERT INTO plants (p_name, p_stock, p_price) VALUES (:p_name, :p_stock, :p_price)";
            $stmt = $dbPDO->prepare($sql);
            $stmt->bindParam(':p_name', $p_name);
            $stmt->bindParam(':p_stock', $p_stock, PDO::PARAM_INT);
            $stmt->bindParam(':p_price', $p_price, PDO::PARAM_STR);

            if ($stmt->execute()) {
                echo json_encode(["message" => "Plant added successfully"]);
            } else {
                echo json_encode(["message" => "Failed to add plant"]);
            }
        } else {
            echo json_encode(["message" => "Invalid input"]);
        }
    } catch (PDOException $e) {
        echo json_encode(["message" => "Error: " . $e->getMessage()]);
    }
?>
