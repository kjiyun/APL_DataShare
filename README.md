## 🔒 Share Filtering Data in Docker Containers

**Data filtering and sharing** are essential processes in environments where data privacy and security are crucial. <br>
This document outlines the complete process of performing data filtering in a Docker container environment. 
It covers the secure data transfer between the **User Data Container (UDC)** and the **Object Data Container (ODC)**, including encryption and utilization of the filtered data.

## ⚒️ Steps
### 1. Authenticate UDC and Get JWT Token
   **Request:**
   ```
   POST /user/signin
   ```
   **Body:**
   ```
   {
    "userName": "APL",
    "password": "123",
    "totp": "054852"
   }
   ```

### 2. Retrieve ODC Data Attributes
   **Request:**
   ```
   GET /patients/get-attributes
   ```
   **Response Example:**
   <img src="https://github.com/user-attachments/assets/2e865fda-cbd9-409e-9ba5-3a881b1647dc" width="791">

### 3. Filter Data
   **Process Overview:**

   1. UDC requests ODC data using a JWT token.

   2. The request includes:

       - URL containing Rule and Frame files

       - ODC attribute information

       - Attribute mapping details

   3. ODC downloads the Rule and Frame files.

   4. The filtered data is encrypted and uploaded to the server.

   5. The server returns a time-limited URL for UDC to access the data.

   **Request:**
   ```
   POST /api/v1
   ```
   **Body:**
   ```
   {
     "ruleUrl" : " ... ",
     "frameUrl" : " ... "
   }
   ```
   **Response:**
  
   <img width="791" alt="스크린샷 2025-03-14 오후 1 43 00" src="https://github.com/user-attachments/assets/2745d660-9278-4221-851d-c1f1bbd1ee9c" />

### 4. Get Filtered Data
- UDC receives the URL of the filtered, encrypted data.
- The data is downloaded and decrypted using a generated key.
- The decrypted data is formatted for model training.

### 5. Data Use and Termination
1. UDC: Use the encrypted data for model training.
   
   Upon completion:
    - Delete the local data.
    - Send a request to ODC to delete uploaded files.
3. ODC: Remove the uploaded files from the server.
