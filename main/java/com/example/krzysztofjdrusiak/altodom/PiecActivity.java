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
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import cz.msebera.android.httpclient.Header;


public class PiecActivity extends AppCompatActivity {

    TextView textViewStrona;
    TextView textViewTemperatura;
    TextView textViewTemperaturaOgrzewania;
    TextView textViewAuto;
    TextView textViewOgrzewanie;
    EditText editTextTemp;
    //String koment = "";

    final String httpOFF = "http://192.168.1.17/piecOFF";
    final String httpON = "http://192.168.1.17/piecON";
    final String auto = "http://192.168.1.17/auto?wartosc=";
    //--------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_piec);

        textViewStrona = (TextView)findViewById(R.id.textViewStrona);
        textViewTemperatura = (TextView)findViewById(R.id.textViewTemperatura);
        textViewTemperaturaOgrzewania = (TextView)findViewById(R.id.textViewTemperaturaOgrzewania);
        editTextTemp = (EditText)findViewById(R.id.editTextTemp);
        textViewAuto = (TextView)findViewById(R.id.textViewAuto);
        textViewOgrzewanie = (TextView)findViewById(R.id.textViewOgrzewanie);
        ask("");
    }

    //--------------sciaganie-zawartosci-strony--------------------------------------------------
    public void ask(String koment){

        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        String url ="http://192.168.1.17/x.html" + koment; //adres esp

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
        Intent intent = new Intent(this,PiecActivity.class);
        startActivity(intent);
    }
    //-----------------------------------------------------------------------------------------
    //--------BTN-OGRZEWANIE-ON----------------------------------------------------------------
    public void onNextClickBtnOgrzON(View view) {

        getRequest(httpON);
        Toast.makeText(this, "Ogrzewanie wlaczone", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this,PiecActivity.class);
        startActivity(intent);
    }
    //------------------------------------------------------------------------------------------
    //--------BTN-OGRZEWANIE-OFF----------------------------------------------------------------
    public void onNextClickBtnOgrzOFF(View view) {

        getRequest(httpOFF);
        Toast.makeText(this, "Ogrzewanie wylaczone", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this,PiecActivity.class);
        startActivity(intent);
    }
    //-----------------------------------------------------------------------------------------
    //--------BTN-OGRZEWANIE-OFF----------------------------------------------------------------

    public void onNextClickBtnWyslij(View view) {

        getRequest(auto+editTextTemp.getText());
        Intent intent = new Intent(this,PiecActivity.class);
        startActivity(intent);
    }
    //--------------------btn back----------------------------------------------------
    public void onNextClickBack(View view) {
        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);
    }
    //-----------------------------------------------------------------------------------------
    //--------------------PODAJ-TEMPERATURE----------------------------------------------------

        //Editable ptemp = editTextTemp.getText();



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
                Double tempIN = Double.parseDouble(result.substring(284,289));
                Double getTemp = Double.parseDouble(result.substring(339,343));
                textViewTemperatura.setText("Temperatura w pokoju: "+tempIN+"°C ");
                textViewTemperaturaOgrzewania.setText("Temperatura ogrzewania: "+getTemp+"°C ");
//                textViewTemperatura.setText("Temperatura w pokoju: "+result.substring(284,289)+"°C ");
//                textViewTemperaturaOgrzewania.setText("Temperatura ogrzewania: "+result.substring(339,343)+"°C ");
                textViewStrona.setText("połączono");
                textViewStrona.setTextColor(getResources().getColor(R.color.greenColor));

                if(result.contains("Stan Pieca: </font>3")){
                    textViewOgrzewanie.setText("ON");
                    textViewOgrzewanie.setTextColor(getResources().getColor(R.color.greenColor));
                    textViewAuto.setText("OFF");
                    textViewAuto.setTextColor(getResources().getColor(R.color.redColor));
                }
                else if(result.contains("Stan Pieca: </font>2")){
                    textViewOgrzewanie.setText("OFF");
                    textViewOgrzewanie.setTextColor(getResources().getColor(R.color.redColor));
                    textViewAuto.setText("OFF");
                    textViewAuto.setTextColor(getResources().getColor(R.color.redColor));
                }
                else if(result.contains("Stan Pieca: </font>1")&&(getTemp<tempIN)){
                    textViewOgrzewanie.setText("OFF");
                    textViewOgrzewanie.setTextColor(getResources().getColor(R.color.redColor));
                    textViewAuto.setText("ON");
                    textViewAuto.setTextColor(getResources().getColor(R.color.greenColor));
                }
                else if(result.contains("Stan Pieca: </font>1")&&(getTemp>tempIN)) {
                    textViewOgrzewanie.setText("ON");
                    textViewOgrzewanie.setTextColor(getResources().getColor(R.color.greenColor));
                    textViewAuto.setText("ON");
                    textViewAuto.setTextColor(getResources().getColor(R.color.greenColor));
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
//------------------------------------------------------------------------------------------------
}
