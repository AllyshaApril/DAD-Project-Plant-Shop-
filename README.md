**Team Members :** 
1. Allysha April Si Kim Choo (B032210435)
2. Kiruthikga A/P Asogan (B032210042)
3. Fatin Ainaa Syazwani Bt Mohd Refaee (B032210080)
4. Nur Amelia Asyqin Bt Mohd Ismailuzabihullah (B032210011)

**Project Name : Plant Ordering System**

- **How many apps involved**
  
 1. Shop owner
 2. Customer
   
- **Brief explanation each apps** 

**Shop owner**
    - Login 
       - Authentication 
    - Plant Inventory dashboard
    - Add Plant details 
    - View Plant details
    - Update Plant details
    - Manage Order status
      - Its receiver socket programming which receive every progress of customer

** Customer**
    - Sign up & Login
      - Authentication  
      - After login all of the progress will be send to shop owner via socket programming
    - Purchase Plant
    - View Order Status

- **Architecture/Layer diagram for each of the apps including the middleware**


![dad project  drawio](https://github.com/AllyshaApril/DAD-Project-Plant-Shop-/assets/129834240/38bc4405-e1e4-411a-8952-c38e76cc7561)






   
- **List of URL end points middleware RESTful/SOAP/Socket** 

  **Restful** 
 ** Customer (Receiver shop owner folder)**
    - Sign up 
        - http://localhost/DAD/cust_signup.php
    
    - Login 
        - http://localhost/DAD/cust_login.php

    - View list plant to purchase
        - http://localhost/DAD/plants.php
    
    - Process customer purchased
       - http://localhost/DAD/cust_order.php

    - View list customer purchase history
        - http://localhost/DAD/cust_history.php
    
    - View specific plants that customer purchase sorted by purchase date
        - http://localhost/DAD/cust_history.php

    - If customer successfully purchased
       - http://localhost/DAD/cust_order.php
  
 ** Shop Owner**
    - View all the plants from db
       - http://localhost/DAD/plants.php
    
    - Add new plant information
      - http://localhost/DAD/add_plant.php
    
    - Edit the plant details
      - http://localhost/DAD/updateplant.php

    - Delete plant 
      - http://localhost/DAD/deleteplant.php
    
    - View customer purchase list  
      - http://localhost/DAD/s_orders.php
    
    - View specific customer purchase list
      - http://localhost/DAD/s_view.php
    
    - Update the purchase status (paid/unpaid)
      - http://localhost/DAD/paid.php

    **Socket Programming **
       ** Shop owner : Receiver.java**

  	- Cust_SignUp & Cust_Login & Cust_Purchase & Check Out & Cust_Order & Cust_View (View specific purchase history) sends data
      		- Port: 8080
      		- IP Address: 10.200.109.19

        - View specific purchase details
               - Port : 10000
               - IP Address: 10.200.109.19

        **Customer : Receiver.java **
        - Customer login 
               - Port: 5000
      	       - IP Address: 10.200.109.19

        - Customer purchase (Cust_plant.java)
              - Port: 7000
              - IP Address: 10.200.109.19

        - View list of purchase order  
              - Port: 9999
              - IP Address: 10.200.109.19

- **Functions/Features in the middleware**

  - **Shop Owner**
      -  Socket Programming:
        	Middleware to establish a WebSocket connection for real-time updates.
                Receive and process customer progress updates.    


    - Login
    Authentication:
        - Middleware function to handle authentication logic.
        - Verify credentials and generate a session token or JWT for authenticated users.
 
    - Plant Inventory Dashboard
   
   - Add Plant Details
   Input Validation:
        Middleware to validate input data for adding new plants.
        Check for required fields and data types.

    Database Interaction:
        Middleware to handle the insertion of new plant details into the database.
        Ensure data consistency and handle any database errors.

   - View Plant Details
   Data Retrieval:
        Middleware to fetch plant details from the database based on plant ID.
       
   - Update Plant Details
   Input Validation:
        Middleware to validate the updated plant details.
      
   Database Interaction:
        Middleware to update plant details in the database.
       
   - Manage Order Status
   Order Status Update:
        Middleware to handle the updating of order status.


   - **Customer**
     - Sign Up & Login

    Authentication:
        Middleware to handle user registration and login.
        Validate input data  and create user accounts.
       
    Socket Programming:
        Middleware to establish connection upon successful login.
        Send real-time updates of customer activities to the shop owner.

   - Purchase Plant
     Cart Management:
        Middleware to handle adding plants to the cart.
        Validate stock availability and calculate total prices.

    Checkout Process:
        Middleware to handle the checkout process, including payment processing.
      
   - Order Creation:
        Middleware to create a new order in the database after a successful purchase.


- **The database and tables involve in the projects**

Database name : akaa

Table 1 : account 
attributes 1. email 
           2. password
	   3. access

Table 2 : customer
atrributes 1. c_id (customer id)
	   2. c_name (customer name)
           3. c_no (customer phone number)
           4. email (customer valid email)

Table 3 : orders
attributes 1. o_id (order id)
	   2. qty (quantity)
           3. p_id (plant id) (attribute refer to plant table)
           4. purchase_id (attribute refer to purchase table)

Table 4 : plants
attributes 1. p_id (plant id)
           2. p_name (plant name)
           3. p_stock (plant stock)
           4. p_price (plant price)

Table 5 : purchase 
attributes 1. purchase_id 
           2. o_date (order date)
           3. status 
           4. c_id (customer id) (attribute refer to customer id)
