package myaplication.tfg.org.myapplication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import myaplication.tfg.org.ApiMethod.Cart;
import myaplication.tfg.org.models.shippingMethod;


public class ShippingMethodInfo extends ActionBarActivity {

    private String sessionId;
    private SoapSerializationEnvelope env;
    private HttpTransportSE androidHttpTransport;
    private SoapObject request;
    private SoapObject shippingmethod;
    private SoapObject amount;
    private HashMap<String,shippingMethod> shippingMethods;
    private RadioGroup shippingGrup;
    private RadioButton radioButton;
    private Cart cart;
    private String shipCost;
    private String shipMethodCode;
    private TextView totalAmount;
    private TextView subTotal;
    private TextView shippingCost;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shipping_method);
        shippingMethods = new HashMap<>();
        cart = DataHolder.getCart();
        totalAmount = (TextView)findViewById(R.id.Total);
        subTotal = (TextView)findViewById(R.id.subTotal);
        shippingCost=(TextView)findViewById(R.id.shippingCost);
        getCustomizedActionBar();
        new shippingAndPaymentList().execute();
        Button payment = (Button)findViewById(R.id.continuePayment);
        payment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ShippingMethodInfo.this,PaymentMethodInfo.class);
                startActivity(intent);
            }
        });
    }



    private class shippingAndPaymentList extends AsyncTask<String,Float,String> {

        ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.d("Hi", "Download Commencing");
            pDialog = new ProgressDialog(ShippingMethodInfo.this);
            String message = "Waiting...";
            SpannableString ss2 = new SpannableString(message);
            ss2.setSpan(new RelativeSizeSpan(2f), 0, ss2.length(), 0);
            ss2.setSpan(new ForegroundColorSpan(Color.BLACK), 0, ss2.length(), 0);
            pDialog.setMessage(ss2);
            pDialog.setCancelable(false);
            pDialog.show();
        }


        @Override
        protected String doInBackground(String... strings) {

            try {
                shippingmethod = cart.getShippingment();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            }
            return null;
        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            pDialog.dismiss();
            getShipping();

        }


        private void getShipping() {
            for (int i = 0; i < shippingmethod.getPropertyCount(); i++) {
                SoapObject child = (SoapObject) shippingmethod.getProperty(i);
                shippingMethod ship = new shippingMethod();
                ship.setCode((String) child.getProperty("code"));
                ship.setTitle((String) child.getProperty("carrier_title"));
                ship.setPrice((child.getProperty("price").toString()));
                shippingMethods.put(ship.getTitle()+"   "+ship.getPrice()+"\u20AC",ship);
            }
            shippingGrup = (RadioGroup) findViewById(R.id.shipping_group);
            int margin = 15;
            RadioGroup.LayoutParams params = new RadioGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(margin, margin, margin, margin);
            final List<shippingMethod>shippingList = new ArrayList<>(shippingMethods.values());
            for (int i = 0; i < shippingList.size(); i++) {
                shippingMethod s = shippingList.get(i);
                String info = s.getTitle() + "   " + s.getPrice() +"\u20AC";
                RadioButton tempButton = new RadioButton(ShippingMethodInfo.this);
                tempButton.setText(info);
                tempButton.setTextSize(22);
                tempButton.setId(i);
                tempButton.setTag(i);

                shippingGrup.addView(tempButton, params);
            }
            if (radioButton != null) {
                shippingGrup.check(radioButton.getId());
            }


            shippingGrup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    RadioButton radio =(RadioButton)group.getChildAt(checkedId);
                    String result = radio.getText().toString();
                    shippingMethod ship = (shippingMethod)shippingMethods.get(result);
                    shipCost = ship.getPrice();
                    shipMethodCode = ship.getCode();
                    DataHolder.getCart().setShippingMethod(ship);
                    new updateTotalAmount().execute();
                }
            });

        }
    }


        private class updateTotalAmount extends AsyncTask<String,Float,String>{

            ProgressDialog pDialog;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                Log.d("Hi", "Download Commencing");
                pDialog = new ProgressDialog(ShippingMethodInfo.this);
                String message = "Waiting...";
                SpannableString ss2 = new SpannableString(message);
                ss2.setSpan(new RelativeSizeSpan(2f), 0, ss2.length(), 0);
                ss2.setSpan(new ForegroundColorSpan(Color.BLACK), 0, ss2.length(), 0);
                pDialog.setMessage(ss2);
                pDialog.setCancelable(false);
                pDialog.show();
            }


            @Override
            protected String doInBackground(String... strings) {

                try {
                    cart.addShippingMethod(shipMethodCode);
                    amount = cart.getTotalAmountt();

                } catch (IOException e) {
                    e.printStackTrace();
                } catch (XmlPullParserException e) {
                    e.printStackTrace();
                }
                return null;
            }


            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);
                pDialog.dismiss();
                showAmount();

            }

            private void showAmount() {
                SoapObject s = (SoapObject)amount.getProperty(0);
                SoapObject cost = (SoapObject)amount.getProperty(1);
                SoapObject t = (SoapObject)amount.getProperty(2);
                DecimalFormat formatter = new DecimalFormat("#,##0.00");
                String subprice = s.getProperty("amount").toString();
                String sub = s.getProperty("title")+": "+formatter.format(Double.valueOf(subprice))+"\u20AC";
                String shipcost = "Shipping Cost: "+formatter.format(Double.valueOf(cost.getProperty("amount").toString()))+"\u20AC";
                String total = t.getProperty("title")+": "+formatter.format(Double.valueOf(t.getProperty("amount").toString()))+"\u20AC";

                subTotal.setText(sub);
                shippingCost.setText(shipcost);
                totalAmount.setText(total);
            }

        }






    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_shipping_and_payment_method, menu);
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

    public void getCustomizedActionBar(){
        android.support.v7.app.ActionBar actionBar=getSupportActionBar();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        LayoutInflater mInflater = LayoutInflater.from(this);
        View mCustomView = mInflater.inflate(R.layout.customactionbar, null);
        ImageView cart = (ImageView)mCustomView.findViewById(R.id.shopCartButton);
        cart.setVisibility(View.GONE);
        TextView listNumber = (TextView)mCustomView.findViewById(R.id.number);
        listNumber.setVisibility(View.GONE);
        actionBar.setCustomView(mCustomView);
        actionBar.setDisplayShowCustomEnabled(true);
    }
}
