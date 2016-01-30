package com.visa.vdp.api.client;

import java.util.Map;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.visa.vdp.util.XPayTokenGeneration;

/**
 * 
 * @author VISA
 *
 */
public class CybersourceClient extends AbstractClient{

	final static Logger logger = Logger.getLogger(VisaTokenServiceClient.class);
	
    static final String API_KEY = "{put your api key here}";
    static final String SHARED_SECRET = "{put your shared secret here}";


    static final String API_URI = "https://sandbox.api.visa.com/";
    static final String BASE_URI = "cybersource/";
    
    public static void main(String args[]) throws Exception {
    	
    	creditTransactions();
    	authorizationTransactions();
    	saleTransactions();
    }
    

    @SuppressWarnings("unchecked")
	private static void saleTransactions() throws Exception{
    	
    	// Load the body for the post request
        String body = "{\"amount\":\"1\",\"currency\":\"USD\",\"payment\":{\"cardExpirationMonth\":\""+getCurrentMonth()+"\",\"cardExpirationYear\":\""+getNextYear()+"\",\"cardNumber\":\"4111111111111111\"}}";
        String xPayToken = XPayTokenGeneration.generateXpaytoken(body, AUTHORIZE_CREDIT, API_KEY, SHARED_SECRET) ;
        String url = "";
        String responseBody = "";
        String crId = RandomStringUtils.random(15, true, true);
        
        // Create a Sales Transaction
        logger.info("===============================================================");
        logger.info("             Create a Sales Transaction                  "); 
        logger.info("===============================================================");
        url =  API_URI + BASE_URI + CREATE_SALES + "?apikey=" + API_KEY;
        responseBody = getResponseForXPayToken(url, xPayToken, body, "POST", crId);
        ObjectMapper mapper = getObectMapperInstance();
		Map<String,Object> enrollPanRes = mapper.readValue(responseBody, Map.class);
		String sales_id = (String)enrollPanRes.get("id");
		
		//Retrieve All Sales
		logger.info("===============================================");
        logger.info("             Retrieve All Sales                  ");
        logger.info("===============================================");
        crId = RandomStringUtils.random(15, true, true);
        url =  API_URI + BASE_URI + RETRIEVE_SALES + "?apikey=" + API_KEY;
        xPayToken = XPayTokenGeneration.generateXpaytoken("", RETRIEVE_SALES, API_KEY, SHARED_SECRET) ;
        getResponseForXPayToken(url, xPayToken, "", "GET", crId);
        
        // Refund A Sale
        logger.info("===============================================");
        logger.info("             Refund A Sale                  ");
        logger.info("===============================================");
        crId = RandomStringUtils.random(15, true, true);
        body = "{  \"amount\": \"1\",  \"currency\": \"USD\"}";
        REFUND_SALE  = StringUtils.replace(REFUND_SALE, "{sales-id}", sales_id);
        url =  API_URI + BASE_URI + REFUND_SALE + "?apikey=" + API_KEY;
        xPayToken = XPayTokenGeneration.generateXpaytoken("", REFUND_SALE, API_KEY, SHARED_SECRET) ;
        responseBody = getResponseForXPayToken(url, xPayToken, body, "POST", crId);
        
        // Retrieve A Refund
        
	}



	@SuppressWarnings("unchecked")
    static void authorizationTransactions() throws Exception{
    	
    	// Load the body for the post request
        String body = "{\"amount\":\"1\",\"currency\":\"USD\",\"payment\":{\"cardExpirationMonth\":\""+getCurrentMonth()+"\",\"cardExpirationYear\":\""+getNextYear()+"\",\"cardNumber\":\"4111111111111111\"}}";
        String xPayToken = XPayTokenGeneration.generateXpaytoken(body, AUTHORIZE_CREDIT, API_KEY, SHARED_SECRET) ;
        String url = "";
        String responseBody = "";
        String crId = RandomStringUtils.random(15, true, true);
		
        // authorize credit
        logger.info("===============================================================");
        logger.info("             authorize credit                  "); 
        logger.info("===============================================================");
        url =  API_URI + BASE_URI + AUTHORIZE_CREDIT + "?apikey=" + API_KEY;
        responseBody = getResponseForXPayToken(url, xPayToken, body, "POST", crId);
        ObjectMapper mapper = getObectMapperInstance();
		Map<String,Object> enrollPanRes = mapper.readValue(responseBody, Map.class);
		String authorization_id = (String)enrollPanRes.get("id");
		
		//Retrieve a Payment Authorization
		logger.info("===============================================================");
		logger.info("             Retrieve a Payment Authorization                  ");
		logger.info("===============================================================");
		crId = RandomStringUtils.random(15, true, true);
		RETRIEVE_PAYMENT_AUTHORIZATION = StringUtils.replace(RETRIEVE_PAYMENT_AUTHORIZATION, "{authorization-id}", authorization_id);
        url =  API_URI + BASE_URI + RETRIEVE_PAYMENT_AUTHORIZATION + "?apikey=" + API_KEY;
        xPayToken = XPayTokenGeneration.generateXpaytoken("", RETRIEVE_PAYMENT_AUTHORIZATION, API_KEY, SHARED_SECRET) ;
        getResponseForXPayToken(url, xPayToken, "", "GET", crId);
        
		
		//Retrieve All Authorizations
        logger.info("===============================================================");
        logger.info("             Retrieve All Authorizations                  ");
        logger.info("===============================================================");
        crId = RandomStringUtils.random(15, true, true);
        url =  API_URI + BASE_URI + RETRIEVE_ALL_AUTHORIZATION + "?apikey=" + API_KEY;
        xPayToken = XPayTokenGeneration.generateXpaytoken("", RETRIEVE_ALL_AUTHORIZATION, API_KEY, SHARED_SECRET) ;
        getResponseForXPayToken(url, xPayToken, "", "GET", crId);
        
        // Capture Funds for an Authorized Amount
        logger.info("===============================================================");
        logger.info("             Capture Funds for an Authorized Amount                  ");
        logger.info("===============================================================");
        body = "{  \"amount\": \"1\",  \"currency\": \"USD\",  \"referenceId\": \""+RandomStringUtils.random(15, false, true)+"\"}";
        crId = RandomStringUtils.random(15, true, true);
        CAPTURE_FUNDS = StringUtils.replace(CAPTURE_FUNDS, "{authorization-id}", authorization_id);
        url =  API_URI + BASE_URI + CAPTURE_FUNDS + "?apikey=" + API_KEY;
        xPayToken = XPayTokenGeneration.generateXpaytoken(body, CAPTURE_FUNDS, API_KEY, SHARED_SECRET) ;
        responseBody = getResponseForXPayToken(url, xPayToken, body, "POST", crId);
        
    	// Retrieve Capture By Authorization Id
        logger.info("===============================================================");
        logger.info("             Retrieve Capture By Authorization Id                  ");
        logger.info("===============================================================");
        crId = RandomStringUtils.random(15, true, true);
        RETRIEVE_CAPTURE_AUTHZ_ID = StringUtils.replace(RETRIEVE_CAPTURE_AUTHZ_ID, "{authorization-id}", authorization_id);
        url =  API_URI + BASE_URI + RETRIEVE_CAPTURE_AUTHZ_ID + "?apikey=" + API_KEY;
        xPayToken = XPayTokenGeneration.generateXpaytoken("", RETRIEVE_CAPTURE_AUTHZ_ID, API_KEY, SHARED_SECRET) ;
        getResponseForXPayToken(url, xPayToken, "", "GET", crId);
        
        // Retrieve a Capture
        logger.info("===============================================================");
        logger.info("             Retrieve a Capture                  ");
        logger.info("===============================================================");
        crId = RandomStringUtils.random(15, true, true);
        RETRIEVE_CAPTURE = StringUtils.replace(RETRIEVE_CAPTURE, "{capture-id}", authorization_id);
        url =  API_URI + BASE_URI + RETRIEVE_CAPTURE + "?apikey=" + API_KEY;
        xPayToken = XPayTokenGeneration.generateXpaytoken("", RETRIEVE_CAPTURE, API_KEY, SHARED_SECRET) ;
        getResponseForXPayToken(url, xPayToken, "", "GET", crId);
        
        
        //Retrieve All Captures 
        logger.info("===============================================================");
        logger.info("             Retrieve All Captures                  ");
        logger.info("===============================================================");
        crId = RandomStringUtils.random(15, true, true);
        url =  API_URI + BASE_URI + RETRIEVE_ALL_CAPTURE + "?apikey=" + API_KEY;
        xPayToken = XPayTokenGeneration.generateXpaytoken("", RETRIEVE_ALL_CAPTURE, API_KEY, SHARED_SECRET) ;
        responseBody = getResponseForXPayToken(url, xPayToken, "", "GET", crId);
        
        
	}



	@SuppressWarnings("unchecked")
	static void creditTransactions() throws Exception {
    	
    	// Load the body for the post request
        String body = "{\"amount\": \"5000\", \"currency\": \"USD\", \"payment\": { \"cardNumber\": \""+"4556687086979345"+"\", \"cardExpirationMonth\": \""+getCurrentMonth()+"\", \"cardExpirationYear\": \""+getNextYear()+"\" }}";
        String xPayToken = XPayTokenGeneration.generateXpaytoken(body, CREATE_CREDIT, API_KEY, SHARED_SECRET) ;
        String url = "";
        String responseBody = "";
        String crId = RandomStringUtils.random(15, true, true);
        
        // Create Credit
        logger.info("===============================================================");
        logger.info("             Create Credit                  ");
        logger.info("===============================================================");
        url =  API_URI + BASE_URI + CREATE_CREDIT + "?apikey=" + API_KEY;
        responseBody = getResponseForXPayToken(url, xPayToken, body, "POST", crId);
        ObjectMapper mapper = getObectMapperInstance();
		Map<String,Object> enrollPanRes = mapper.readValue(responseBody, Map.class);
		String cardId = (String)enrollPanRes.get("id");
		logger.info("CardId "+cardId);
		
       // Retrieve A Credit
		logger.info("===============================================================");
		logger.info("             Create Credit                  ");
		logger.info("===============================================================");
		crId = RandomStringUtils.random(15, true, true);
		RETRIEVE_CREDIT = StringUtils.replace(RETRIEVE_CREDIT, "{credit-id}", cardId);
        url =  API_URI + BASE_URI + RETRIEVE_CREDIT + "?apikey=" + API_KEY;
        xPayToken = XPayTokenGeneration.generateXpaytoken("", RETRIEVE_CREDIT, API_KEY, SHARED_SECRET) ;
        getResponseForXPayToken(url, xPayToken, "", "GET", crId);
        
        
        // Retrieve All Credit
        logger.info("===============================================================");
        logger.info("             Retrieve All Credit                  ");
        logger.info("===============================================================");
        crId = RandomStringUtils.random(15, true, true);
        url =  API_URI + BASE_URI + RETRIEVE_ALL_CREDIT + "?apikey=" + API_KEY;
        xPayToken = XPayTokenGeneration.generateXpaytoken("", RETRIEVE_ALL_CREDIT, API_KEY, SHARED_SECRET) ;
        getResponseForXPayToken(url, xPayToken, "", "GET", crId);
        
        
        // Void A Credit
        logger.info("===============================================================");
        logger.info("             Void A Credit                 ");
        logger.info("===============================================================");
        VOID_CREDIT = StringUtils.replace(VOID_CREDIT, "{credit-id}", cardId);
        url =  API_URI + BASE_URI + VOID_CREDIT + "?apikey=" + API_KEY;
        body = "{\"referenceId\": \""+RandomStringUtils.random(15, false, true)+"\"}";
        getResponseForXPayToken(url, xPayToken, body, "POST", crId);
        
    }
    
    static  String CREATE_CREDIT = "payments/v1/credits";
    static  String RETRIEVE_CREDIT = "payments/v1/credits/{credit-id}";
    static  String RETRIEVE_ALL_CREDIT = "payments/v1/credits";
    
    static  String AUTHORIZE_CREDIT = "payments/v1/authorizations";
    static  String RETRIEVE_PAYMENT_AUTHORIZATION = "payments/v1/authorizations/{authorization-id}";
    static  String RETRIEVE_ALL_AUTHORIZATION = "payments/v1/authorizations";
    
    static  String CAPTURE_FUNDS = "payments/v1/authorizations/{authorization-id}/captures";
    static  String RETRIEVE_CAPTURE_AUTHZ_ID = "payments/v1/authorizations/{authorization-id}/captures";
    static  String RETRIEVE_CAPTURE = "payments/v1/captures/{capture-id}";
    static  String REFUND_CAPTURE = "payments/v1/captures/{capture-id}/refunds";
    static  String RETRIEVE_REFUND_CAPTURE_ID = "payments/v1/captures/{capture-id}/refunds";
    static  String RETRIEVE_ALL_CAPTURE = "payments/v1/captures";
    
    static  String CREATE_SALES = "payments/v1/sales";
    static  String RETRIEVE_SALES = "payments/v1/sales";
    static  String REFUND_SALE = "payments/v1/sales/{sales-id}/refunds";
    static  String RETRIEVE_REFUND_ID = "payments/v1/refunds/{refunds-id}";
    static  String RETRIEVE_ALL_REFUNDS = "payments/v1/refunds";
    static  String RETRIEVE_REFUND_SALE_ID = "payments/v1/sales/{sales-id}/refunds";
    static  String SEARCH_SALE_ID = "payments/v1/sales/{sales-id}";
    
    static  String PAYMENT_SEARCH = "payments/v1/search";
    static  String PAYMENT_SEARCH_ID = "payments/v1/search/{id}";
    
    static  String VOID_CREDIT = "payments/v1/credits/{credit-id}/voids";
    static  String VOID_CAPTURE = "payments/v1/captures/{capture-id}/voids";
    static  String VOID_SALE = "payments/v1/sales/{sales-id}/voids";
    static  String VOID_REFUND = "payments/v1/refunds/{refunds-id}/voids";
    static  String RETRIEVE_VOID_TRANSACTION = "payments/v1/voids/{void-id}";
    
    static  String ERROR_RESOURCE = "payments/v1/errors/{correlation-id}";
    

}
