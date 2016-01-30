<?php

$time = time();

$apikey = 'put your api key here';
$secret = 'put your shared secret here';

$query_string = "apikey=" .$apikey;
$resource = "payments/v1/authorizations";
$body = "{
        \"amount\": \"0\",
        \"currency\": \"USD\",
        \"payment\": {
        \"cardNumber\": \"4111111111111111\",
        \"cardExpirationMonth\": \"10\",
        \"cardExpirationYear\": \"2016\"
        }
    }";

//Hash for x-pay-token

$token = $secret.$time.$resource.$query_string.$body;
$hashtoken = "x:".$time.":".hash('sha256',$token);
echo "<strong>X-PAY-TOKEN:</strong><br>".$hashtoken. "<br><br>";
$url = "https://sandbox.api.visa.com/cybersource/payments/v1/authorizations?".$query_string;
echo "<strong>URL:</strong><br>".$url. "<br><br>";
//Header
$header = (array("X-PAY-TOKEN: ".$hashtoken, "Content-Type: application/json"));
  
$ch = curl_init();
curl_setopt($ch, CURLOPT_URL, $url);
curl_setopt($ch, CURLOPT_HTTPHEADER, $header);
curl_setopt($ch, CURLOPT_POST, 1);
curl_setopt($ch, CURLOPT_POSTFIELDS, $body);
curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);

//getting response from server
$response = curl_exec($ch);
echo "<strong>HTTP Status:</strong> <br>".curl_getinfo($ch, CURLINFO_HTTP_CODE). "<br><br>";
curl_close($ch);

$json = json_decode($response);
$json = json_encode($json, JSON_PRETTY_PRINT);
printf("<pre>%s</pre>", $json);

exit();
?>