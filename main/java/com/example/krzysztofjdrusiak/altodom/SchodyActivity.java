package com.example.krzysztofjdrusiak.altodom;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import cz.msebera.android.httpclient.Header;

public class SchodyActivity extends AppCompatActivity {

    TextView textViewStrona;
    TextView textViewOswietlenie;
    TextView textViewAuto;
    final String httpOFF = "http://192.168.1.18/swiatloOFF";
    final String httpON = "http://192.168.1.18/swiatloON";
    final String auto = "http://192.168.1.18/auto";
    final String dziennoc = "http://192.168.1.18/dziennoc";

int abcd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schody);

        textViewStrona = (TextView)findViewById(R.id.textViewStrona);
        textViewOswietlenie = (TextView)findViewById(R.id.textViewOswietlenie);
        textViewAuto = (TextView)findViewById(R.id.textViewAuto);
        ask("");
    }
    //--------------------btn back----------------------------------------------------
    public void onNextClickBtnBackSch(View view) {
        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);
    }

    //--------------sciaganie-zawartosci-strony--------------------------------------------------
    public void ask(String koment){

        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        String url ="http://192.168.1.18/x.html" + koment; //adres esp

        if (networkInfo != null && networkInfo.isConnected()) {
            //----------------
            new DownloadWebpageTask().execute(url);
            //----------------
        } else {
            textViewStrona.setText("Brak polaczenia z siecią");
        }


    }
    //----btn-Odswiez-------------------------------------------------------------------------
    public void onNextClickOdswiez(View view) {
        Intent intent = new Intent(this,SchodyActivity.class);
        startActivity(intent);
    }
    //-----------------------------------------------------------------------------------------
    //-----------------------------------------------------------------------------------------
    //--------BTN-OGRZEWANIE-ON----------------------------------------------------------------
    public void onNextClickBtnOswON(View view) {

        getRequest(httpON);
        Toast.makeText(this, "ON", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this,SchodyActivity.class);
        startActivity(intent);
    }
    //-----------------------------------------------------------------------------------------
    //--------BTN-OGRZEWANIE-OFF----------------------------------------------------------------
    public void onNextClickBtnOswOFF(View view) {

        getRequest(httpOFF);
        Toast.makeText(this, "OFF", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this,SchodyActivity.class);
        startActivity(intent);
    }
    //-----------------------------------------------------------------------------------------
    //--------BTN-OGRZEWANIE-OFF----------------------------------------------------------------

    public void onNextClickBtnAuto(View view) {

        getRequest(auto);
        Toast.makeText(this, "AUTO", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this,SchodyActivity.class);
        startActivity(intent);
    }
    //--------BTN-dzien noc------------------------------------------------------------------
    public void onNextClickBtnDzienNoc(View view) {
        getRequest(dziennoc);
        Toast.makeText(this, "DZIEŃ / NOC", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this,SchodyActivity.class);
        startActivity(intent);

    }

    //------------------------------------------------------------------------------------------
    //------------async-task--------------------------------------------------------------------

    private class DownloadWebpageTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            Polaczenie connection = new Polaczenie();
            return connection.GetEsp(urls[0]);


        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {

            if(result != null){
                textViewStrona.setText("połączono");

                textViewStrona.setTextColor(getResources().getColor(R.color.greenColor));


                if(result.contains("Stan Oswietlenia: </font>1")&&(result.contains("tryb o: </font>3"))) {
                    textViewOswietlenie.setText("ON");
                    textViewOswietlenie.setTextColor(getResources().getColor(R.color.greenColor));

                }

                else if(result.contains("Stan Oswietlenia: </font>0")&&(result.contains("tryb o: </font>2"))) {
                    textViewOswietlenie.setText("OFF");
                    textViewOswietlenie.setTextColor(getResources().getColor(R.color.redColor));

                }
                else if(result.contains("tryb o: </font>1")) {
                    textViewOswietlenie.setText("CZUJNIK RUCHU");
                    textViewOswietlenie.setTextColor(getResources().getColor(R.color.greenColor));

                }
                else if(result.contains("tryb o: </font>4")) {
                    textViewOswietlenie.setText("DZIEŃ / NOC");
                    textViewOswietlenie.setTextColor(getResources().getColor(R.color.greenColor));

                }


            }else{
                textViewStrona.setText("Brak połączenia z ESP");
                textViewStrona.setTextColor(getResources().getColor(R.color.redColor));
            }}
    }
//------------------------------------------------------------------------------------------------

    //-----------------------WYSYLANIE-URL-ASYNC-TASKIEM----------------------------------------------
    private void getRequest (final String url) {
        try {
            AsyncHttpClient client = new AsyncHttpClient();
            RequestParams parms = new RequestParams();
            client.get(url, parms, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    Log.v("UWAGA", "LACZY z" + url);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    Log.v("UWAGA", "nie laczy z " + url);
                }

            });

        }catch(Exception e){e.printStackTrace();

        }}



}
