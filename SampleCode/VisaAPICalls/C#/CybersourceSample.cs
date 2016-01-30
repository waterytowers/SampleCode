using System;
using System.Linq;
using System.IO;
using System.Net;
using System.Text;
using System.Security.Cryptography;

namespace Visa
{
    public class CybersourceSample
    {
        private static string API_KEY = @"{put your api key here}";
		private static string SHARED_SECRET = @"{put your shared secret here}";

        public static void Main (string[] args) {
			string baseUri = @"cybersource/";
			string resourcePath = @"payments/v1/authorizations";
            string url = @"https://sandbox.api.visa.com/" + baseUri + resourcePath + "?apikey=" + API_KEY;

            // Create post request object
            HttpWebRequest request = WebRequest.Create (url) as HttpWebRequest;
			request.Method = "POST";
			
            // Load the body for the post request
			string body = "{\"amount\": \"0\", \"currency\": \"USD\", \"payment\": { \"cardNumber\": \"4111111111111111\", \"cardExpirationMonth\": \"10\", \"cardExpirationYear\": \"2016\" }}";        
			var requestStringBytes = System.Text.Encoding.UTF8.GetBytes (body);
			request.GetRequestStream ().Write (requestStringBytes, 0, requestStringBytes.Length);
            
            // Add headers
			request.ContentType = @"application/json";
            string xPayToken = getXPayToken(resourcePath, 
											"apikey="+API_KEY,
										    body);
            request.Headers.Add("x-pay-token", xPayToken);
            
            // Make the call
            string responseBody = "";
			try {
				using (HttpWebResponse response = request.GetResponse () as HttpWebResponse) {
					if (response.StatusCode != HttpStatusCode.Created)
						throw new Exception (String.Format (
							"Server error (HTTP {0}: {1}). body",
							response.StatusCode,
							response.StatusDescription));

                    Console.WriteLine("Response headers: \n" + response.Headers.ToString());
                    
					var encoding = ASCIIEncoding.ASCII;
					using (var reader = new StreamReader (response.GetResponseStream(), encoding)) {
						responseBody = reader.ReadToEnd ();
					}
				}
			} catch (WebException e) {
				Console.WriteLine (e.Message);
			}
            Console.WriteLine("Response body: \n" + responseBody);
		}
         
        private static string getXPayToken(string apiNameURI, string queryString, string requestBody) {
            string timestamp = getTimestamp();
            string sourceString = SHARED_SECRET + timestamp + apiNameURI + queryString + requestBody;
            string hash = getDigest(sourceString);
            string token = "x:" + timestamp + ":" + hash;
            return token;
        }
         
        private static string getTimestamp() {
            long timeStamp = ((long)DateTime.UtcNow.Subtract(new DateTime(1970,1,1,0,0,0,DateTimeKind.Utc)).TotalMilliseconds)/1000;
            return timeStamp.ToString();
        }
        
        private static string getDigest(string data) {
            SHA256Managed hashString = new SHA256Managed();
            string digest = String.Empty;
            var bytes = hashString.ComputeHash(Encoding.ASCII.GetBytes(data));
            foreach (byte b in bytes)
            {
                digest += b.ToString("x2");
            }
            return digest;
         }
         
    }
}