<?php

$time = time();

$url = "https://sandbox.api.visa.com/visadirect/fundstransfer/v1/pullfundstransactions";

$certificatePath = "put the client certificate pem file path here";
$privateKey = "put the private key pem file path here";

$userId = "put your user id for the app from VDP Portal here";
$password = "put your password for the app from VDP Portal here";

$requestBodyString = "{
  \"systemsTraceAuditNumber\": 300259,
  \"retrievalReferenceNumber\": \"407509300259\",
  \"localTransactionDateTime\": \"2021-10-26T21:32:52\",
  \"acquiringBin\": 409999,
  \"acquirerCountryCode\": \"101\",
  \"senderPrimaryAccountNumber\": \"4957030100009952\",
  \"senderCardExpiryDate\": \"2020-03\",
  \"senderCurrencyCode\": \"USD\",
  \"amount\": \"110\",
  \"surcharge\": \"2.00\",
  \"cavv\": \"0000010926000071934977253000000000000000\",
  \"foreignExchangeFeeTransaction\": \"10.00\",
  \"businessApplicationId\": \"AA\",
  \"merchantCategoryCode\": 6012,
  \"cardAcceptor\": {
    \"name\": \"Saranya\",
    \"terminalId\": \"365539\",
    \"idCode\": \"VMT200911026070\",
    \"address\": {
      \"state\": \"CA\",
      \"county\": \"081\",
      \"country\": \"USA\",
      \"zipCode\": \"94404\"
    }
  },
  \"magneticStripeData\": {
    \"track1Data\": \"1010101010101010101010101010\"
  },
  \"pointOfServiceData\": {
    \"panEntryMode\": \"90\",
    \"posConditionCode\": \"0\",
    \"motoECIIndicator\": \"0\"
  },
  \"pointOfServiceCapability\": {
    \"posTerminalType\": \"4\",
    \"posTerminalEntryCapability\": \"2\"
  },
  \"feeProgramIndicator\": \"123\"
}";


$authString = $userId.":".$password;
$authStringBytes = utf8_encode($authString);
$authloginString = base64_encode($authStringBytes);
$authHeader = "Authorization:Basic ".$authloginString;
echo "<strong>URL:</strong><br>".$url. "<br><br>";
$header = (array("Accept: application/json", "Content-Type: application/json", $authHeader));
       
$ch = curl_init();
curl_setopt($ch, CURLOPT_URL, $url);
curl_setopt($ch, CURLOPT_HTTPHEADER, $header);
curl_setopt($ch, CURLOPT_POST, 1);
curl_setopt($ch, CURLOPT_POSTFIELDS, $requestBodyString); 
curl_setopt($ch, CURLOPT_SSLCERT, $certificatePath);
curl_setopt($ch, CURLOPT_SSLKEY, $privateKey);
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