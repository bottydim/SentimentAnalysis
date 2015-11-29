import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Communicator {
	private  String urlBinom = "http://www.fon.hum.uva.nl/Service/Statistics/Sign_Test.html";
	private  String urlZ = "http://www.fon.hum.uva.nl/Service/Statistics/Normal-Z_distribution.html";
	private  String charset = java.nio.charset.StandardCharsets.UTF_8.name();
	
	public  double getP(int cl1, int cl2){
		String param1 = String.valueOf(cl1);
		String param2 = String.valueOf(cl2);
		String query;
		try {
			query = String.format("positive=%s&negative=%s", 
				     URLEncoder.encode(param1, charset), 
				     URLEncoder.encode(param2, charset));
			HttpURLConnection connection = (HttpURLConnection) new URL(urlBinom).openConnection();
			connection.setDoOutput(true); // Triggers POST.
			connection.setRequestProperty("Accept-Charset", charset);
			connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=" + charset);

			try (OutputStream output = connection.getOutputStream()) {
			    output.write(query.getBytes(charset));
			}
			int responseCode = connection.getResponseCode();
	        System.out.println("GET Response Code :: " + responseCode);
	        if (responseCode == HttpURLConnection.HTTP_OK) {
			BufferedReader in = new BufferedReader(new InputStreamReader(
					connection.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();
 
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            System.out.println(response);
	        }
	        else
	        {
	        	System.out.println("Bad request");
	        	System.out.println(connection);
	        }
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return 0;
	}
	
	public  double getPforZ(double z){
		String param1 = String.valueOf(z);
		String query;
		try {
			query = String.format("Z=%s", 
				     URLEncoder.encode(param1, charset));
			HttpURLConnection connection = (HttpURLConnection) new URL(urlZ).openConnection();
			connection.setDoOutput(true); // Triggers POST.
			connection.setRequestProperty("Accept-Charset", charset);
			connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=" + charset);

			try (OutputStream output = connection.getOutputStream()) {
			    output.write(query.getBytes(charset));
			}
			int responseCode = connection.getResponseCode();
//	        System.out.println("GET Response Code :: " + responseCode);
	        if (responseCode == HttpURLConnection.HTTP_OK) {
			BufferedReader in = new BufferedReader(new InputStreamReader(
					connection.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();
 
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
//            System.out.println(response);
            return extractVal("z",response.toString());
	        }
	        else
	        {
	        	System.out.println("Bad request");
	        	System.out.println(connection);
	        }
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return 0;
	}
	
	private double extractVal(String val, String response)
	{
		String pattStr = "Prob.*<=\\s+(\\d)(?:\\.(\\d+))?"; 
		Pattern pattern = Pattern.compile(pattStr);
		Matcher matcher = pattern.matcher(response);
		if (matcher.find())
		{
			
			String decimal = matcher.group(2) !=null ? matcher.group(2): "0";
			String p = matcher.group(1)+"."+decimal;
//		    System.out.println("P:"+p);
		    return Double.valueOf(p);
		}
		System.err.println("Pattern not found");
		System.err.println(response);
		return 0;
	}
	
}
