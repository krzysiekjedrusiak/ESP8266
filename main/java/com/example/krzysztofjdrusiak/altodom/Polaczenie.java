package com.example.krzysztofjdrusiak.altodom;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Krzysztof Jędrusiak on 16.11.2016.
 */

public class Polaczenie {
    private String strona = null;
    public String GetEsp(String urlString){

        try{
            URL url = new URL (urlString);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

            if(httpURLConnection.getResponseCode()==200) {

                InputStream inputStream = new BufferedInputStream(httpURLConnection.getInputStream());

                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream,"UTF-8"));

                StringBuilder stringBuilder = new StringBuilder();

                String mystring;

                while ((mystring = bufferedReader.readLine()) != null) {
                    stringBuilder.append(mystring);
                }

                strona = stringBuilder.toString();

                httpURLConnection.disconnect();
            }

        }catch (IOException e) {
            return null;
        }
        return strona;
    }
}
