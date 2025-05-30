import requests
import hashlib
import datetime
import random

# Credentials and required information
username = 'testa'
accountno = '250160000011'  # The account number
partnerpassword = '+$J<wtZktTDs&-Mk("h5=<PH#Jf769P5/Z<*xbR~'

# Current timestamp in yyyymmddHHMMSS format
timestamp = datetime.datetime.now().strftime('%Y%m%d%H%M%S')

# Step 1: Concatenate username, account number, partner password, and timestamp
raw_string = f"{username}{accountno}{partnerpassword}{timestamp}"

# Step 2: Encrypt the concatenated string using SHA256
password = hashlib.sha256(raw_string.encode()).hexdigest()

# Generate a unique transaction ID
# Using timestamp + random number to ensure uniqueness
transaction_id = int(timestamp + str(random.randint(1000, 9999)))

# Step 3: Collect user input values for mobilephone, amount, etc.
mobilephone = input("Enter mobile phone number (without spaces): ")
amount = float(input("Enter the amount: "))
withdrawcharge = 1.0  # Fixed withdraw charge
reason = "deposit"
sid = "1"

print(f"Generated Transaction ID: {transaction_id}")

# API request data with generated password
data = {
    'username': username,
    'timestamp': timestamp,
    'amount': amount,
    'withdrawcharge': withdrawcharge,
    'reason': reason,
    'sid': sid,
    'password': password,  # Use the generated password
    'mobilephone': mobilephone,
    'requesttransactionid': transaction_id  # Automatically generated unique transaction ID
}

# Step 4: Make the POST request to the API
response = requests.post('https://www.intouchpay.co.rw/api/requestdeposit/', data=data)

# Step 5: Print the API response
print(response.text)
