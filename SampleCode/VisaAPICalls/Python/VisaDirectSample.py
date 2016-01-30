import requests

# Sample post body payload
body='''{
  "businessApplicationId": "AA",
  "merchantCategoryCode": 6012,
  "pointOfServiceCapability": {
    "posTerminalType": "4",
    "posTerminalEntryCapability": "2"
  },
  "feeProgramIndicator": "123",
  "systemsTraceAuditNumber": 300259,
  "retrievalReferenceNumber": "407509300259",
  "foreignExchangeFeeTransaction": "10.00",
  "cardAcceptor": {
    "name": "Acceptor 1",
    "terminalId": "365539",
    "idCode": "VMT200911026070",
    "address": {
      "state": "CA",
      "county": "081",
      "country": "USA",
      "zipCode": "94404"
    }
  },
  "magneticStripeData": {
    "track1Data": "1010101010101010101010101010"
  },
  "senderPrimaryAccountNumber": "4005520000011126",
  "senderCurrencyCode": "USD",
  "surcharge": "2.00",
  "localTransactionDateTime": "2021-10-26T21:32:52",
  "senderCardExpiryDate": "2013-03",
  "pinData": {
    "pinDataBlock": "1cd948f2b961b682",
    "securityRelatedControlInfo": {
      "pinBlockFormatCode": 1,
      "zoneKeyIndex": 1
    }
  },
  "cavv": "0000010926000071934977253000000000000000",
  "pointOfServiceData": {
    "panEntryMode": "90",
    "posConditionCode": "0",
    "motoECIIndicator": "0"
  },
  "acquiringBin": 409999,
  "acquirerCountryCode": "101",
  "amount": "112.00"
}'''

user_id = 'put your user id for the app from VDP Portal here'
password = 'put your password for the app from VDP Portal here'
base_uri = 'visadirect/'
resource_path = 'fundstransfer/v1/pullfundstransactions/'
url = 'https://sandbox.api.visa.com/' + base_uri + resource_path
headers = {'content-type': 'application/json', 'accept':'application/json'}

# Make the call
r = requests.post(url, 
  cert = ('put the client certificate pem file path here','put the private key pem file path here'),
  headers = headers,
  auth = (user_id, password),
  data = body)

# Print the response
print r.status_code
print r.content