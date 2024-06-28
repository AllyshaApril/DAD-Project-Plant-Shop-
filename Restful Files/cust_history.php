<?php
require_once("dbConn.php");

header("Content-Type: application/json");

try {
    // Get the raw POST data
    $postData = file_get_contents("php://input");
    $data = json_decode($postData, true);

    // Validate incoming data
    if (isset($data['c_id'])) {
        $c_id = $data['c_id'];
        $purchase_id = isset($data['purchase_id']) ? $data['purchase_id'] : null;

        if ($purchase_id) {
            // Fetch details for a specific purchase
            $sql = "
                SELECT 
                    p.purchase_id, p.o_date, pl.p_name, o.qty, pl.p_price
                FROM 
                    purchase p
                INNER JOIN 
                    orders o ON p.purchase_id = o.purchase_id
                INNER JOIN 
                    plants pl ON o.p_id = pl.p_id
                WHERE 
                    p.c_id = :c_id AND p.purchase_id = :purchase_id
                ORDER BY 
                    p.o_date DESC, p.purchase_id ASC";

            $stmt = $dbPDO->prepare($sql);
            $stmt->bindParam(':c_id', $c_id, PDO::PARAM_INT);
            $stmt->bindParam(':purchase_id', $purchase_id, PDO::PARAM_INT);
            $stmt->execute();

            $result = $stmt->fetchAll(PDO::FETCH_ASSOC);

            // Initialize total price for the purchase
            $totalPurchasePrice = 0;

            // Structure the response
            $order = [
                'purchase_id' => $purchase_id,
                'o_date' => $result[0]['o_date'],
                'total' => 0, // Initialize total to 0
                'items' => []
            ];

            foreach ($result as $row) {
                $plantName = $row['p_name'];
                $quantity = $row['qty'];
                $price = $row['p_price'];
                $total_price = $price * $quantity;

                // Add to total purchase price
                $totalPurchasePrice += $total_price;

                $order['items'][] = [
                    'p_name' => $plantName,
                    'qty' => $quantity,
                    'p_price' => $price,
                    'total_price' => $total_price
                ];
            }

            // Add the total price of the whole purchase to the response
            $order['total'] = $totalPurchasePrice;

            echo json_encode($order);
        } else {
            // Fetch all purchases for the customer
            $sql = "
                SELECT 
                    p.purchase_id, p.o_date, pl.p_name, o.qty, pl.p_price
                FROM 
                    purchase p
                INNER JOIN 
                    orders o ON p.purchase_id = o.purchase_id
                INNER JOIN 
                    plants pl ON o.p_id = pl.p_id
                WHERE 
                    p.c_id = :c_id
                ORDER BY 
                    p.o_date DESC, p.purchase_id ASC";

            $stmt = $dbPDO->prepare($sql);
            $stmt->bindParam(':c_id', $c_id, PDO::PARAM_INT);
            $stmt->execute();

            $result = $stmt->fetchAll(PDO::FETCH_ASSOC);

            // Group the results by purchase_id and o_date
            $orders = [];
            foreach ($result as $row) {
                $purchaseId = $row['purchase_id'];
                $oDate = $row['o_date'];
                $plantName = $row['p_name'];
                $quantity = $row['qty'];
                $price = $row['p_price'];
                $total_price = $price * $quantity;

                if (!isset($orders[$purchaseId])) {
                    $orders[$purchaseId] = [
                        'purchase_id' => $purchaseId,
                        'o_date' => $oDate,
                        'total' => 0, // Initialize total to 0
                        'items' => []
                    ];
                }

                // Add the item's total price to the purchase's total
                $orders[$purchaseId]['total'] += $total_price;

                // Add the item to the items array
                $orders[$purchaseId]['items'][] = [
                    'p_name' => $plantName,
                    'qty' => $quantity,
                    'p_price' => $price,
                    'total_price' => $total_price // Add total_price for consistency
                ];
            }

            // Re-index the orders array
            $orders = array_values($orders);

            echo json_encode($orders);
        }
    } else {
        echo json_encode(["message" => "Invalid input"]);
    }
} catch (PDOException $e) {
    echo json_encode(["message" => "Error: " . $e->getMessage()]);
}
?>
