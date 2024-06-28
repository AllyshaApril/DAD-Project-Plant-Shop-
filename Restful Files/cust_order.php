<?php
require_once("dbConn.php");

header("Content-Type: application/json");

try {
    // Get the raw POST data
    $postData = file_get_contents("php://input");
    $data = json_decode($postData, true);

    // Validate incoming data
    if (isset($data['c_id']) && isset($data['orders'])) {
        $c_id = $data['c_id'];
        $orders = $data['orders'];
        $status = "Unpaid";

        // Start a transaction
        $dbPDO->beginTransaction();

        try {
            // Insert into purchase table
            $purchaseDate = date('Y-m-d'); // Use ISO 8601 format for dates
            $sqlInsertPurchase = "INSERT INTO purchase (o_date, status, c_id) VALUES (:o_date, :status, :c_id)";
            $stmt = $dbPDO->prepare($sqlInsertPurchase);
            $stmt->bindParam(':o_date', $purchaseDate);
            $stmt->bindParam(':status', $status);
            $stmt->bindParam(':c_id', $c_id, PDO::PARAM_INT);
            $stmt->execute();

            // Retrieve purchase_id
            $purchaseId = $dbPDO->lastInsertId();

            // Prepare statement for updating plant stock
            $sqlUpdateStock = "UPDATE plants SET p_stock = p_stock - :qty WHERE p_name = :p_name";

            // Insert into order table and update plant stock for each item
            foreach ($orders as $order) {
                $p_name = $order['p_name'];
                $qty = $order['qty'];

                // Retrieve p_id based on p_name
                $sqlSelectPlantId = "SELECT p_id FROM plants WHERE p_name = :p_name";
                $stmt = $dbPDO->prepare($sqlSelectPlantId);
                $stmt->bindParam(':p_name', $p_name);
                $stmt->execute();
                $result = $stmt->fetch(PDO::FETCH_ASSOC);
                $p_id = $result['p_id'];

                // Insert into order table
                $sqlInsertOrder = "INSERT INTO `orders` (qty, purchase_id, p_id) VALUES (:qty, :purchase_id, :p_id)";
                $stmt = $dbPDO->prepare($sqlInsertOrder);
                $stmt->bindParam(':qty', $qty, PDO::PARAM_INT);
                $stmt->bindParam(':purchase_id', $purchaseId, PDO::PARAM_INT);
                $stmt->bindParam(':p_id', $p_id, PDO::PARAM_INT);
                $stmt->execute();

                // Update plant stock
                $stmt = $dbPDO->prepare($sqlUpdateStock);
                $stmt->bindParam(':qty', $qty, PDO::PARAM_INT);
                $stmt->bindParam(':p_name', $p_name);
                $stmt->execute();
            }

            // Commit transaction
            $dbPDO->commit();

            echo json_encode(["message" => "Order processed successfully"]);
        } catch (PDOException $e) {
            // Rollback transaction on failure
            $dbPDO->rollback();
            echo json_encode(["message" => "Error processing order: " . $e->getMessage()]);
        }
    } else {
        echo json_encode(["message" => "Invalid input"]);
    }
} catch (PDOException $e) {
    echo json_encode(["message" => "Error: " . $e->getMessage()]);
}
?>
