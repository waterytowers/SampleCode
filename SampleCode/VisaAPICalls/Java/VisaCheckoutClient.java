package com.visa.vdp.api.client;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;

import com.visa.vdp.util.XPayTokenGeneration;


/**
 * 
 * @author VISA
 *
 */
public class VisaCheckoutClient extends AbstractClient{

    private static String API_KEY = "{put your api key here}";
    private static String SHARED_SECRET = "{put your shared secret here}";
    
    // Refer to https://developer.visa.com/vme/merchant/documents/Getting_Started_With_Visa_Checkout/Quick_Start_Tutorial.html
    private static String CALL_ID = "{put your call id here}";

    static final String API_URI = "https://sandbox.secure.checkout.visa.com/";
    static final String BASE_URI = "wallet-services-web/";
    
    
    // Shows a sample call to get Visa Checkout "get payment data" API
    public static void main(String args[]) throws Exception {
    	
    	 String body = "{\"orderInfo\":{\"total\":\"101\",\"currencyCode\":\"USD\",\"subtotal\":\"80.1\",\"shippingHandling\":\"5.1\",\"tax\":\"7.1\",\"discount\":\"5.25\",\"giftWrap\":\"10.1\",\"misc\":\"3.2\",\"eventType\":\"Confirm\",\"orderId\":\"testorderID\",\"promoCode\":\"testPromoCode\",\"reason\":\"Order Successfully Created\"}}";
         String xPayToken = ""; 
         String url = "";
         String responseBody = "";
         String crId = RandomStringUtils.random(15, true, true);

         // Get Payment Info
         GET_PAYMENT_DATA = StringUtils.replace(GET_PAYMENT_DATA, "{callId}", CALL_ID);
         xPayToken = XPayTokenGeneration.generateXpaytoken("", GET_PAYMENT_DATA, API_KEY, SHARED_SECRET) ;
         url =  API_URI + BASE_URI + GET_PAYMENT_DATA + "?apikey=" + API_KEY;
         logRequestBody("", url, xPayToken, crId);
         responseBody = getResponseForXPayToken(url, xPayToken, "", "GET", crId);
         logResponseBody(responseBody);
         
         // Update Payment Info
         crId = RandomStringUtils.random(15, true, true);
         UPDATE_PAYMENT_INFO = StringUtils.replace(UPDATE_PAYMENT_INFO, "{callId}", CALL_ID);
         xPayToken = XPayTokenGeneration.generateXpaytoken(body, UPDATE_PAYMENT_INFO, API_KEY, SHARED_SECRET) ;
         url =  API_URI + BASE_URI + UPDATE_PAYMENT_INFO + "?apikey=" + API_KEY;
         logRequestBody(body, url, xPayToken, crId);
         responseBody = getResponseForXPayToken(url, xPayToken, body, "PUT", crId);
         logResponseBody(responseBody);
 		
    	
    }

    
    static String UPDATE_PAYMENT_INFO = "payment/info/{callId}";
    static String GET_PAYMENT_DATA = "payment/data/{callId}";
}
