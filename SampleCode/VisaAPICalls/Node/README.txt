Running the Cybersource and Visa Checkout sample code
===================================================== 
1. Pre-requisite packages to be installed: npm install request
2. Log on to beta.developer.visa.com and click on your app name
3. Copy the APIKey and Shared secret to any text editor
4. Download the sample code Cybersourcesample.js or VisaCheckoutsample.js to your local folder
5. Replace "put your api key here" with the APIKey from step 3
6. Replace "put your shared secret here" with the Shared Secret from step 3
7. Compile and run the sample code
8. You should see response from the respective API calls

Running the VisaDirect sample code
=====================================================  
1. Pre-requisite:
    packages to be installed: npm install request 
    create a certificate using openSSL (steps below):
    Download and install openSSL in your window
    Execute the following command on the command prompt "openssl genrsa -out  example-key.pem 2048"
    Execute the following command on the command prompt "openssl req -new -key example-key.pem -out example.csr"
    Upload the csr to VDP portal
    Download the cert.pem from app details on VDP portal (should be visible when you click on the app name in VDP portal)
2. Log on to beta.developer.visa.com and click on your app name
3. Copy the userid and password from the Keys/Apps tab to any text editor
4. Download the sample code VisaDirectsample.js to your local folder
5. Replace "{put your user id here}" with the user id from step 3
6. Replace "{put your password here}" with the password from step 3
7. Replace "{put the private key pem file path here}" with the path to the exampled-key.pem
8. Replace "{put the client certificate pem file path here}" with the path to cert.pem
9. Run the sample code
10. You should see response from the VisaDirect API calls
