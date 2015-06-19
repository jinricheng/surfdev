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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.pedant.SweetAlert.SweetAlertDialog;
import myaplication.tfg.org.models.Customer;


public class CustomerInfo extends ActionBarActivity {
    private EditText fName;
    private EditText lName;
    private EditText email;
    private Button continueButton;
    private SoapSerializationEnvelope env;
    private HttpTransportSE androidHttpTransport;
    private SoapObject request;
    private SoapObject r;
    private String sessionId;
    private Customer customer;
    private boolean ok =false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_info);
        getCustomizedActionBar();
        continueButton = (Button)findViewById(R.id.continueShipment);
        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createCustomerInfo();

            }
        });

    }

    private void createCustomerInfo() {
        fName = (EditText)findViewById(R.id.firstName);
        lName =(EditText)findViewById(R.id.lastName);
        email = (EditText)findViewById(R.id.email);
        String firstName = fName.getText().toString();
        String lastName =  lName.getText().toString();
        String emailAdress = email.getText().toString();
        if(!checkEmailFormat(emailAdress)){
            new SweetAlertDialog(CustomerInfo.this, SweetAlertDialog.WARNING_TYPE)
                    .setTitleText("Incorrect format Email")
                    .setContentText("check your email adress's format please")
                    .setConfirmText("Ok")
                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sDialog) {
                            sDialog.dismiss();
                        }
                    })
                    .show();
        }
        else{
            customer = new Customer(firstName,lastName,emailAdress);
            new addCustomerInfo().execute();
        }

    }

    private boolean checkEmailFormat(String emailAdress) {
        boolean isValid = false;

        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        CharSequence inputStr = emailAdress;

        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);
        if (matcher.matches()) {
            isValid = true;
        }
        return isValid;
    }

    private class addCustomerInfo extends AsyncTask<String,Float,String>{
        ProgressDialog pDialog;
        String NAMESPACE = "urn:Magento";
        String URL = "http://gonegocio.es/index.php/api/v2_soap/";


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.d("Hi", "Download Commencing");
            pDialog = new ProgressDialog(CustomerInfo.this);
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
                setupSessionLogin();
                addAction();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            }
            return "execute";
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            pDialog.dismiss();
        }

        private void addAction() throws IOException, XmlPullParserException {
            SoapObject customerInfo = new SoapObject(NAMESPACE, "shoppingCartCustomerEntity");
            PropertyInfo pi = new PropertyInfo();

            pi.setName("mode");
            pi.setValue("guest");
            pi.setType(String.class);
            customerInfo.addProperty(pi);

            pi = new PropertyInfo();
            pi.setName("email");
            pi.setValue(customer.getEmailAdress());
            pi.setType(String.class);
            customerInfo.addProperty(pi);

            pi = new PropertyInfo();
            pi.setName("firstname");
            pi.setValue(customer.getFirstName());
            pi.setType(String.class);
            customerInfo.addProperty(pi);

            pi = new PropertyInfo();
            pi.setName("lastname");
            pi.setValue(customer.getLastName());
            pi.setType(String.class);
            customerInfo.addProperty(pi);

            request = new SoapObject(NAMESPACE, "shoppingCartCustomerSet");
            request.addProperty("sessionId", sessionId);
            request.addProperty("quoteId", DataHolder.getCartId());
            request.addProperty("customer", customerInfo);
            env.setOutputSoapObject(request);
            androidHttpTransport.call("", env);
            ok = (boolean) env.getResponse();

            if (ok == true) {
                System.out.println("add customer correctly");
                DataHolder.getCart().setCustomer(customer);
                Intent intent = new Intent(CustomerInfo.this,AddressInfo.class);
                startActivity(intent);
            }
            else{
                new SweetAlertDialog(CustomerInfo.this, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Invalid Email Address")
                        .setContentText("Can not add the customer info")
                        .setConfirmText("Ok")
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                sDialog.dismiss();
                            }
                        })
                        .show();
            }
        }


        private void setupSessionLogin() throws IOException, XmlPullParserException {
            env = new SoapSerializationEnvelope(SoapEnvelope.VER11);
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
            sessionId = (String) result;
        }



    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_customer_info, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            finish();
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
