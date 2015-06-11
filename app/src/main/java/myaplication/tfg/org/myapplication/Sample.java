package myaplication.tfg.org.myapplication;

import com.paypal.android.sdk.payments.PayPalAuthorization;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalFuturePaymentActivity;
import com.paypal.android.sdk.payments.PayPalItem;
import com.paypal.android.sdk.payments.PayPalOAuthScopes;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalPaymentDetails;
import com.paypal.android.sdk.payments.PayPalProfileSharingActivity;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;
import com.paypal.android.sdk.payments.ShippingAddress;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import org.json.JSONException;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.jar.Attributes;

/**
 * Basic sample using the SDK to make a payment or consent to future payments.
 *
 * For sample mobile backend interactions, see
 * https://github.com/paypal/rest-api-sdk-python/tree/master/samples/mobile_backend
 */
public class Sample extends Activity {
    private SoapSerializationEnvelope env;
    private HttpTransportSE androidHttpTransport;
    private SoapObject request;
    private SoapObject r;
    private List<SoapObject> listAllItems;
    private String sessionId;
    String NAMESPACE = "urn:Magento";
    String URL = "http://gonegocio.es/index.php/api/v2_soap/";
    HashMap<String,HashMap<String,Integer>> navigationList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new DownLoad().execute();
    }


    private class DownLoad extends AsyncTask<String, Float, String> {

        @Override
        protected String doInBackground(String... strings) {
            try {
                setupLogin();
                allproducts();
                relatedProducts();

            } catch (IOException e) {
                e.printStackTrace();
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            }
            return null;
        }



        private void allproducts() throws IOException, XmlPullParserException {
            request = new SoapObject(NAMESPACE, "catalogCategoryAssignedProducts");
            request.addProperty("sessionId", sessionId);
            request.addProperty("categoryId", "5");
            env.setOutputSoapObject(request);
            androidHttpTransport.call("", env);
            r = (SoapObject) env.getResponse();
            for(int i=0;i<r.getPropertyCount();i++ ){
                SoapObject child = (SoapObject)r.getProperty(i);
            System.out.println(child.toString());
            }

        }
        private void relatedProducts() throws IOException, XmlPullParserException {
            request = new SoapObject(NAMESPACE, "catalogProductLinkList");
            request.addProperty("sessionId", sessionId);
            request.addProperty("type","up_sell");
            request.addProperty("product","21");
            env.setOutputSoapObject(request);
            androidHttpTransport.call("", env);
            r = (SoapObject) env.getResponse();
            System.out.println(r.toString());

        }
        private HashMap<String, Integer> getSecondLevelInfo(SoapObject child) {
            SoapObject levelTwo = (SoapObject) child.getProperty("children");
            HashMap<String,Integer> r = new HashMap<>();
                  for(int i =0;i<levelTwo.getPropertyCount();i++) {
                      SoapObject result = (SoapObject)levelTwo.getProperty(i);
                      r.put(result.getProperty("name").toString(), (Integer) result.getProperty("category_id"));
                  }
            Log.d("map size",Integer.toString(r.size()));
            return r;
        }

        private SoapObject rearchFisrtLevel(SoapObject r) {
            SoapObject result;
            result = (SoapObject)r.getProperty("children");
            result = (SoapObject)result.getProperty("item");
            result = (SoapObject)result.getProperty("children");
            return result;
        }

        private void setupLogin() throws IOException, XmlPullParserException {
            env = new SoapSerializationEnvelope(
                    SoapEnvelope.VER11);
            env.dotNet = false;
            env.xsd = SoapSerializationEnvelope.XSD;
            env.enc = SoapSerializationEnvelope.ENC;
            request = new SoapObject(NAMESPACE, "login");
            request.addProperty("username", "jin");
            request.addProperty("apiKey", "1234567890");
            env.setOutputSoapObject(request);
            androidHttpTransport = new HttpTransportSE(URL);
            androidHttpTransport.call("", env);
            Object result = env.getResponse();
            sessionId = (String)result;
        }


    }
}