package com.alquimista.android.asktheoracle;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.HttpGet;

import android.util.Log;

public class Utils {

	private static final String TAG = "AskTheOracle.Utils";
	private static final boolean DEBUG = true;

	private static final int HTTP_STATUS_OK = 200;


    /**
    *
    * @param url
    * @return
    * @throws IOException
    */
   public static InputStream getUrlContent(String url)
   		throws IOException
   {

   	if (DEBUG) Log.d( TAG, "getUrlContent("+url+")");

		// Create client and set our specific user-agent string
//		HttpClient client = new DefaultHttpClient();
//		HttpGet request = new HttpGet(url);
//		request.setHeader("User-Agent", sUserAgent);

//		HttpResponse response = client.execute(request);
   	HttpGet request = new HttpGet(url);
   	HttpResponse response = HttpManager.execute(request);

		// Check if server response is valid
		StatusLine status = response.getStatusLine();
		if (status.getStatusCode() != HTTP_STATUS_OK) {
			throw new RuntimeException( new HttpException("Invalid response from server: " + status.toString()));
		}

		// Pull content stream from response
		HttpEntity entity = response.getEntity();
		return entity.getContent();
	}


	public static String convertStreamToString(InputStream is) {
        /*
         * To convert the InputStream to String we use the BufferedReader.readLine()
         * method. We iterate until the BufferedReader return null which means
         * there's no more data to read. Each line will appended to a StringBuilder
         * and returned as String.
         */
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }
}
