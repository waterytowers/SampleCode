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

def get_payment_info(shared_secret, api_key, call_id):
    base_uri = 'wallet-services-web/'
    resource_path = 'payment/data/' + call_id
    data_level = 'FULL'
    query_params = 'apikey=' + api_key + '&dataLevel=' + data_level
    body = ''
    x_pay_token = generate_x_pay_token(shared_secret, resource_path, query_params, body)	
    headers = {'content-type': 'application/json', 'accept':'application/json', 'x-pay-token': x_pay_token}
    r = requests.get('https://sandbox.api.visa.com/' + base_uri + resource_path + '?' + query_params, headers = headers)
    print r.status_code
    print r.content

def main():
    call_id = 'put your call id here'
    api_key = 'put your api key here'
    shared_secret = 'put your shared secret here'	
    get_payment_info(shared_secret, api_key, call_id)

if __name__ == '__main__':
    main()