<?php
require_once("dbConn.php");

header("Content-Type: application/json");

try {
    // Get the raw POST data
    $postData = file_get_contents("php://input");
    $data = json_decode($postData, true);

    // Validate incoming data
    if (isset($data['c_id']) && isset($data['purchase_id'])) {
        $c_id = $data['c_id'];
        $purchase_id = $data['purchase_id'];

        // Fetch details for a specific purchase including customer name
        $sql = "
            SELECT 
                p.purchase_id, p.o_date, c.c_name, pl.p_name, o.qty, pl.p_price
            FROM 
                purchase p
            INNER JOIN 
                orders o ON p.purchase_id = o.purchase_id
            INNER JOIN 
                plants pl ON o.p_id = pl.p_id
            INNER JOIN 
                customer c ON p.c_id = c.c_id
            WHERE 
                p.c_id = :c_id AND p.purchase_id = :purchase_id
            ORDER BY 
                p.o_date DESC, p.purchase_id ASC";

        $stmt = $dbPDO->prepare($sql);
        $stmt->bindParam(':c_id', $c_id, PDO::PARAM_INT);
        $stmt->bindParam(':purchase_id', $purchase_id, PDO::PARAM_INT);
        $stmt->execute();

        $result = $stmt->fetchAll(PDO::FETCH_ASSOC);

        if (count($result) > 0) {
            // Structure the response
            $order = [
                'purchase_id' => $purchase_id,
                'o_date' => $result[0]['o_date'],
                'c_name' => $result[0]['c_name'],
                'items' => []
            ];

            foreach ($result as $row) {
                $plantName = $row['p_name'];
                $quantity = $row['qty'];
                $price = $row['p_price'];
                $total_price = $price * $quantity;

                $order['items'][] = [
                    'p_name' => $plantName,
                    'qty' => $quantity,
                    'p_price' => $price,
                    'total_price' => $total_price
                ];
            }

            echo json_encode($order);
        } else {
            echo json_encode(["message" => "No records found for the given customer and purchase ID"]);
        }
    } else {
        echo json_encode(["message" => "Invalid input"]);
    }
} catch (PDOException $e) {
    echo json_encode(["message" => "Error: " . $e->getMessage()]);
}
?>
