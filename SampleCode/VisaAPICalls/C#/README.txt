=====================================================
Running the Cybersource and Visa Checkout C# sample code
===================================================== 
1. Log on to visa developer portal and click on your app name
2. Copy the APIKey and Shared secret to any text editor
3. Download the C# sample code Cybersourcesample.cs or VisaCheckoutsample.cs
4. Replace "put your api key here" with the APIKey from step 2
5. Replace "put your shared secret here" with the Shared Secret from step 2
6. Compile and run the sample code
7. You should see response from the respective API calls

=====================================================
Running the VisaDirect C# sample code
===================================================== 
1. Create a new Visa Direct Application. Portal will prompt you to upload CSR (Certificate Signing Request)
2. Generate CSR as mentioned below using the openssl tool:
	a). Generate a 2048-bit RSA public/private key pair using the below command 
			openssl genrsa -out  example-key.pem 2048
	b). Generate a Certificate Signing Request using the below command. The command will prompt you for Country name, State, Locality Name, Organization name, Organization unit name, hostname, email address
			openssl req -new -key example-key.pem -out example.csr
	Find more details about CSR at https://vdp.visa.com/vdpguide#csr 
3. Upload the CSR to Visa Direct App
4. Click on your Visa Direct App name
5. Copy the userid and password from the Keys/APIs tab to any text editor
6. Download your app certificate from Certificates section
7. Generate P12 file using the below command
			openssl pkcs12 -export -out p12certfile.p12 -inkey example-key.pem -in cert.pem
			NOTE : Use example-key.pem as private key which you have generated at step 2a
				   Use cert.pem as certificate which you have downloaded at step 6
				   The above command will prompt for export password. You will need this password for invoking API
8. Download the C# sample code VisaDirectSample.cs
9. Replace "{put your user id here}" with the user id from step 5
10. Replace "{put your password here}" with the password from step 5
11. Replace "{put the path to the P12 file here}" with the path to the P12 file (p12certfile.p12) you have generated at step 7
12. Replace "{put the P12 file password}" with the pass P12 file password that you have set for P12 file at step 7
13. Compile and run the sample code
14. You should see response from the VisaDirect API calls









