require 'restclient'
  
$api_key = "put your api key here"
$shared_secret = "put your shared secret here"
$base_uri = "cybersource/"
$resource_path = "payments/v1/authorizations"
$query_string = "apiKey=" + $api_key

def get_xpay_token(resource_path, query_string, request_body)
  require 'digest'
  timestamp = Time.now.getutc.to_i.to_s
  hash_input = $shared_secret + timestamp + resource_path + query_string + request_body
  hash_output = Digest::SHA256.hexdigest(hash_input)
  return "x:" + timestamp + ":" + hash_output
end

def authorize_credit_card(request_body)
  xpay_token = get_xpay_token($resource_path, $query_string, request_body)
  full_request_url = "https://sandbox.api.visa.com/" + $base_uri + $resource_path + "?" + $query_string
  begin
    response = RestClient::Request.execute(:url => full_request_url,
      :method => :post,
      :payload => request_body,
      :headers => {
        "content-type" => "application/json",
        "x-pay-token" => xpay_token
      }
  )
  rescue RestClient::ExceptionWithResponse => e
    response = e.response
  end
  return response
end

request_body = {
      "amount" => "0",
      "currency" => "USD",
      "payment" => {
      "cardNumber" => "4111111111111111",
      "cardExpirationMonth" => "10",
      "cardExpirationYear" =>  "2016"
      }
  }.to_json;
  
puts authorize_credit_card(request_body)
