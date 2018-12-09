package net.ossfree.launcher4.Structures;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class AppsByCatagory {
	
	public static String CATEGORY = "<span itemprop=\"genre\">";
	public static String SPAN = "</span>";	
	public static String AUTHOR   = "<span itemprop=\"name\">";
	
	public  static String getCategory(String  packName)  {
		StringBuffer sb = new StringBuffer("");
		char[] charArray = new char[1024];

		try {
			String webPage = "https://play.google.com/store/apps/details?id=" + packName.trim();
			URL url = new URL(webPage);
			HttpURLConnection urlConnection = (HttpURLConnection)url.openConnection();
			urlConnection.setRequestProperty("User-agent","Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1; .NET CLR 1.1.4322; InfoPath.1; .NET CLR 2.0.50727)");
			urlConnection.setRequestMethod("GET");
			urlConnection.setDoOutput(true);
			urlConnection.setReadTimeout(4000);
			urlConnection.connect();	          
			InputStream is = urlConnection.getInputStream();
			InputStreamReader isr = new InputStreamReader(is);
			int numCharsRead;
			while ((numCharsRead = isr.read(charArray)) > 0) {
				sb.append(charArray, 0, numCharsRead);
			}

			isr.close();
			is.close();
			
		} catch (Exception e) {}

		int s = sb.indexOf(CATEGORY);
		int e = sb.indexOf(SPAN, s);
		
		if(e > s ) 
			return sb.substring(s+CATEGORY.length(), e).replace("&amp;", " ");
		else 
			return "other";

	}

}
