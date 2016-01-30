import requests
import calendar
import time
import datetime
from hashlib import sha256

def generate_timestamp():
    d = datetime.datetime.utcnow()
    timestamp = calendar.timegm(d.timetuple())
    return str(timestamp)

def generate_x_pay_token(shared_secret, resource_path, query_params, body):
    timestamp = generate_timestamp()
    pre_hash_string = shared_secret + timestamp + resource_path + query_params + body
    hash_string = sha256(pre_hash_string.rstrip()).hexdigest()
    return 'x:' + timestamp + ':' + hash_string

def authorize_credit_card(shared_secret, api_key):
    base_uri = 'cybersource/'
    resource_path = 'payments/v1/authorizations'
    query_params = 'apikey=' + api_key
    body = '''{
        "amount": "0",
        "currency": "USD",
        "payment": {
        "cardNumber": "4111111111111111",
        "cardExpirationMonth": "10",
        "cardExpirationYear": "2016"
        }
    }'''
    x_pay_token = generate_x_pay_token(shared_secret, resource_path, query_params, body)	
    headers = {'content-type': 'application/json', 'x-pay-token': x_pay_token }
    r = requests.post('https://sandbox.api.visa.com/' + base_uri + resource_path + '?' + query_params, 
        headers = headers,
        data = body)
    print r.status_code
    print r.content

def main():
    api_key = 'put your api key here'
    shared_secret = 'put your shared secret here'	
    authorize_credit_card(shared_secret, api_key)

if __name__ == '__main__':
    main()