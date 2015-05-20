package myaplication.tfg.org.myapplication;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.view.Menu;
import android.view.MenuItem;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.KvmSerializable;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class soapTest extends ActionBarActivity implements Serializable{
   private class DownLoad extends AsyncTask<String,Float,String> {
        ProgressDialog pDialog;
        SoapObject r;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.d("Hi", "Download Commencing");

            pDialog = new ProgressDialog(soapTest.this);

            String message= "Esperando...";
            SpannableString ss2 =  new SpannableString(message);
            ss2.setSpan(new RelativeSizeSpan(2f), 0, ss2.length(), 0);
            ss2.setSpan(new ForegroundColorSpan(Color.BLACK), 0, ss2.length(), 0);
            pDialog.setMessage(ss2);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {

            //INSERT YOUR FUNCTION CALL HERE
             String NAMESPACE = "urn:Magento";
             String URL = "http://gonegocio.es/index.php/api/v2_soap/";


                //making call to get list of customers


            String sessionId = "";

//HashMap<string , String> params = new HashMap</string><string , String>();
//params.put("apiUser", "developer");
//params.put("apiKey", "magento123");


return "Ok";
}





       protected void onProgressUpdate (Float... valores) {
           int p = Math.round(100*valores[0]);
           pDialog.setProgress(p);
       }
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Log.d("Hi", "Done Downloading.");

            pDialog.dismiss();

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_soap_test);
        DownLoad downLoad = new DownLoad();
        downLoad.execute();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_soap_test, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
