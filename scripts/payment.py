import requests
import hashlib
import datetime


# Credentials and required information from .env
username = "testa"
accountno = "250160000011"  # The account number
partnerpassword = '+$J<wtZktTDs&-Mk("h5=<PH#Jf769P5/Z<*xbR~'
callbackurl = "https://your_callback_url"  # Replace with your actual callback URL)

# Current timestamp in yyyymmddHHMMSS format
timestamp = datetime.datetime.now().strftime('%Y%m%d%H%M%S')

# Step 1: Concatenate username, account number, partner password, and timestamp
raw_string = f"{username}{accountno}{partnerpassword}{timestamp}"

# Step 2: Encrypt the concatenated string using SHA256
password = hashlib.sha256(raw_string.encode()).hexdigest()

# Take mobilephone and amount dynamically (e.g., from user input or request body)
mobilephone = input("Enter mobile phone number (without spaces): ")
amount = float(input("Enter the amount: "))

# Step 3: API request data with generated password
data = {
    'username': username,
    'timestamp': timestamp,
    'amount': amount,
    'password': password,  # Use the generated password
    'mobilephone': mobilephone,
    'requesttransactionid': 12223224567289,  # Replace with a unique transaction ID if needed
    'callbackurl': callbackurl  # Use the callback URL from .env
}

# Step 4: Make the POST request to the API
response = requests.post('https://www.intouchpay.co.rw/api/requestpayment/', data=data)

# Step 5: Print the API response
print(response.text)
