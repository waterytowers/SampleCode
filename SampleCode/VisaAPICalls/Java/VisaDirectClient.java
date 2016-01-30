package com.visa.vdp.api.client;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.SSLContext;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

/**
 * Visa Direct uses the push payment capabilities of Visa’s global payment system to enable new ways for clients and consumers to send money to over one billion eligible Visa accounts.   Visa Direct can be used to create both consumer and business services including person-to-person money transfers and credit card payments as well as corporate and merchant disbursements. The services could be offered over any channel including personal computers, mobile phones, local bank branches, or ATMs and can be funded from many sources, including bank accounts, payment cards, or cash.   Merchants, government entities, or corporations can push payments to Visa accounts to disburse such things as tax refunds, insurance claims, rebates, affiliate and contractor payouts, or expense reimbursements.
 * @author VISA
 *
 */
public class VisaDirectClient extends AbstractClient{

    private static String USER_ID = "{put your user id here}"; // Set user ID for App from VDP Portal
    private static String PASSWORD = "{put your password here}"; // Set password for App from VDP Portal

    // Key store settings
    private static String KEY_STORE_PATH = "{put the path to the keystore file here}";
    private static String KEY_STORE_PASSWORD = "{put the keystore password}";
    private static String PRIVATE_KEY_PASSWORD = "{put the private key password here}"; // The password to decrypt the client's private key

    public static void main(String args[]) throws Exception {
        String baseUri = "visadirect/";
        String resourcePath = "fundstransfer/v1/pushfundstransactions/";
        String url = "https://sandbox.api.visa.com/" + baseUri + resourcePath;

        // Load client certificate into key store
       SSLContext sslcontext = SSLContexts.custom()
                .loadKeyMaterial(new File(KEY_STORE_PATH), KEY_STORE_PASSWORD.toCharArray(),
                        PRIVATE_KEY_PASSWORD.toCharArray())
                .build();

        // Allow TLSv1 protocol only
        SSLConnectionSocketFactory sslSocketFactory = new SSLConnectionSocketFactory(sslcontext, new String[] { "TLSv1" }, null,
                SSLConnectionSocketFactory.getDefaultHostnameVerifier());

        CloseableHttpClient httpClient = HttpClients.custom()
                    .setSSLSocketFactory(sslSocketFactory)
                    .build();

        // Create the POST request object
        HttpPost httpPost = new HttpPost(url);

        httpPost.setHeader(HttpHeaders.CONTENT_TYPE, "application/json");
        httpPost.setHeader(HttpHeaders.ACCEPT, "application/json");
        httpPost.setHeader(HttpHeaders.AUTHORIZATION, getBasicAuthHeader(USER_ID, PASSWORD));
        //httpPost.setHeader("wsi_client_cert", "MIIDxzCCAq+gAwIBAgIIZIpp8sxwFokwDQYJKoZIhvcNAQELBQAwMTEOMAwGA1UEAwwFVkRQQ0ExEjAQBgNVBAoMCVZEUFZJU0FDQTELMAkGA1UEBhMCVVMwHhcNMTYwMTE1MDEwMjIxWhcNMTgwMTE0MDEwMjIxWjCBlDE0MDIGCgmSJomT8ixkAQEMJGVhZThkODJhLTFhN2EtNGI2Mi1iZTBiLWE4YjFlOTk5MWQ5YzEPMA0GA1UEAwwGTWFoZXNoMQwwCgYDVQQLDANWRFAxDTALBgNVBAoMBFZJU0ExFDASBgNVBAcMC0ZPU1RFUiBDSVRZMQswCQYDVQQIDAJDQTELMAkGA1UEBhMCVVMwggEiMA0GCSqGSIb3DQEBAQUAA4IBDwAwggEKAoIBAQCPlzDQOhd66/ILxEnEQWC9oRPm5WlfjXeaBb1pJ5/YsjrqTwumI8yBgJs6LsmJBs3SWjJJVXcZDzxZVkJPJ8VQ/HtU9E2yDvqOxNpw2AtDng/47MoiFhAmQV31mj9pbwV6pEij4yX5VxpveX0uaWDxw1LCnlVddU80b+sm+u5n0TGSWCsvo7ZN22Qz70yu7a2SCL4+3ToH/YIc/ntnjkxqhsMIwUAGb9jLZJgOK4enWFQeI21FunaBiGC+pLw9cG39G+2yA07eOrGzke8ExOrmEalG9TTfINwv1yxj5n3Ns5wGa8QIOQZTgYYC4fLGPiohmZKNg0OPE2X3C8ELBiqdAgMBAAGjfzB9MB0GA1UdDgQWBBQ4xUBfyKNgsbC2z3J/L7A0ZfaAQjAMBgNVHRMBAf8EAjAAMB8GA1UdIwQYMBaAFN/BvFGRbTRD2Ilgwfz8mUhz0gctMA4GA1UdDwEB/wQEAwIF4DAdBgNVHSUEFjAUBggrBgEFBQcDAgYIKwYBBQUHAwQwDQYJKoZIhvcNAQELBQADggEBAIcyea6vW3VUk201tR7D7Xe+jX0JNcnud6wbeIZq6hvqx8GoOqsFCeW3ZgBoDdD9NsK6DV2YkpFXKhPtZ18EdC+6cC2XGTgfMq0Ls4ftC1CEYhW6Z9l3cUdWDRfwYLXsDzsmD9Am8piIU9xxLuq7DdV+dpgF9d4sQa3tMyryVMnOttopz0dLRz5OJXbbOdCPidV6DNbQh0kSuqKMYBGGiMF8sH9crXHh60/2Ten682bp7hUG+4u2MMRnBBbNOa1WBknukzs81e4XylIL8U7jl4JVQV4OqAVSzRMGt1L0e/QF9bw4bjcbm8aNy5Ghlv0/QVhxuzkh/pC2y/vmRh8blVA=");

        // Load the body for the post request
        String body = "{\"systemsTraceAuditNumber\":350420,\"retrievalReferenceNumber\":\"401010350420\",\"localTransactionDateTime\":\"2021-10-26T21:32:52\",\"acquiringBin\":409999,\"acquirerCountryCode\":\"101\",\"senderAccountNumber\":\"1234567890123456\",\"senderCountryCode\":\"USA\",\"transactionCurrencyCode\":\"USD\",\"senderName\":\"John Smith\",\"senderAddress\":\"44 Market St.\",\"senderCity\":\"San Francisco\",\"senderStateCode\":\"CA\",\"recipientName\":\"Adam Smith\",\"recipientPrimaryAccountNumber\":\"4957030420210454\",\"amount\":\"112.00\",\"businessApplicationId\":\"AA\",\"transactionId\":234234322342343,\"merchantCategoryCode\":6012,\"sourceOfFundsCode\":\"03\",\"cardAcceptor\":{\"name\":\"John Smith\",\"terminalId\":\"13655392\",\"idCode\":\"VMT200911026070\",\"address\":{\"state\":\"CA\",\"county\":\"081\",\"country\":\"USA\",\"zipCode\":\"94105\"}},\"feeProgramIndicator\":\"123\"}";
        StringEntity postBodyEntity = new StringEntity(body);
        httpPost.setEntity(postBodyEntity);

        // Make the call
        System.out.println("Executing request " + httpPost.getRequestLine());
        CloseableHttpResponse response = httpClient.execute(httpPost);

        // Get the response headers
        Header[] h = response.getAllHeaders();

        // Get the response json object
        BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
        StringBuffer result = new StringBuffer();
        String line;
        while ((line = rd.readLine()) != null) {
            result.append(line);
        }

        // Print the response details
        System.out.println("----------------------------------------");
        HttpEntity entity = response.getEntity();
        System.out.println(response.getStatusLine());

        System.out.println("Response Headers:");
        System.out.println("----------------------------------------");
        for (int i = 0; i < h.length; i++)
            System.out.println(h[i].getName() + ":" + h[i].getValue());
        System.out.println("----------------------------------------");
        System.out.println("Response Body:");
        System.out.println(result);

        // Clean up
        EntityUtils.consume(entity);
        response.close();
        httpClient.close();
    }

    private static String getBasicAuthHeader(String userId, String password) {
        return "Basic " + base64Encode(userId + ":" + password);
    }

    public static String base64Encode(String token) {
        byte[] encodedBytes = Base64.encodeBase64(token.getBytes());
        return new String(encodedBytes, Charset.forName("UTF-8"));
    }
}
