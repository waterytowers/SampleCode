$api_key = "put your api key here"
$shared_secret = "put your shared secret here"
$base_url = "https://sandbox.api.visa.com/wallet-services-web/"
$get_url = "payment/data/"

def get_payment_data(call_id)
  resource_path = $get_url + call_id
  query_string = "apiKey=" + $api_key + "&dataLevel=FULL"
  request_body = ""
  xpay_token = get_xpay_token(resource_path, query_string, request_body)
  require 'restclient'
  full_request_url = $base_url + resource_path + "?" + query_string
  puts "Making Get Payment Data at " + full_request_url
  begin
    response = RestClient::Request.execute(:url => full_request_url,
      :method => :get,
      :headers => {"accept" => "application/json" ,
        "content-type" => "application/json",
        'x-pay-token' => xpay_token})
  rescue RestClient::ExceptionWithResponse => e
    response = e.response
  end
  return response
end

def get_xpay_token(resource_path, query_string, request_body)
  require 'digest'
  timestamp = Time.now.getutc.to_i.to_s
  hash_input = $shared_secret + timestamp + resource_path + query_string + request_body
  hash_output = Digest::SHA256.hexdigest(hash_input)
  return "x:" + timestamp + ":" + hash_output
end

puts get_payment_data("put your call id here")
