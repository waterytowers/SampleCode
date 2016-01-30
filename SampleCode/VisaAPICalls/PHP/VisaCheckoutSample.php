<?php

$time = time();

$apikey = 'put your api key here';
$secret = 'put your shared secret here';
$callid = 'put your call id here';

$query_string = "apikey=".$apikey;
$resource = "payment/data/".$callid;

//Hash for x-pay-token

$token = $secret.$time.$resource.$query_string;
$hashtoken = "x:".$time.":".hash('sha256',$token);
echo "<strong>X-PAY-TOKEN:</strong><br>".$hashtoken. "<br><br>";
$url = "https://sandbox.api.visa.com/wallet-services-web/payment/data/5644711508239260801?".$query_string;
echo "<strong>URL:</strong><br>".$url. "<br><br>";
//Header
$header = (array("X-PAY-TOKEN: ".$hashtoken, "Accept: application/json", "Content-Type: application/json"));
      
$requestBodyString = "{
  \"amount\": \"20.99\",
  \"currency\": \"USD\",
  \"payment\": {
    \"cardNumber\": \"4111111111111110\",
    \"cardExpirationMonth\": \"10\",
    \"cardExpirationYear\": \"2016\"
  }
}";
     
$ch = curl_init();
curl_setopt($ch, CURLOPT_URL, $url);
curl_setopt($ch, CURLOPT_HTTPHEADER, $header);
curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);

//getting response from server
$response = curl_exec($ch);
echo "<strong>HTTP Status:</strong> <br>".curl_getinfo($ch, CURLINFO_HTTP_CODE)."<br><br>";
curl_close($ch);

$json = json_decode($response);
$json = json_encode($json, JSON_PRETTY_PRINT);
printf("<pre>%s</pre>", $json);

exit();
?>