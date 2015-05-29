package myaplication.tfg.org.myapplication;

import android.app.ProgressDialog;
import android.content.Entity;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
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


public class AddressInfo extends ActionBarActivity {
    private EditText street;
    private EditText postcode;
    private EditText telefonNumber;
    private AutoCompleteTextView country;
    private AutoCompleteTextView city;
    private String[] countries;
    private String[] cities;
    private ArrayAdapter countryAdapter;
    private ArrayAdapter cityAdapter;
    private String streetName;
    private String code;
    private String telefon;
    private String countryName;
    private String cityName;
    private Address address;
    private Button continuePayment;
    private String sessionId;
    private SoapSerializationEnvelope env;
    private HttpTransportSE androidHttpTransport;
    private SoapObject request;
    private SoapObject r;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address_info);
        getCustomizedActionBar();
        initialVariable();
        initialAdapters();
        continuePayment = (Button)findViewById(R.id.continueShipment);
        continuePayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createAddress();
                new addCustomerAddress().execute();
            }
        });

    }

    private void initialVariable() {
        street =(EditText)findViewById(R.id.street);
        postcode = (EditText)findViewById(R.id.postCode);
        telefonNumber = (EditText)findViewById(R.id.telefon);
        country=(AutoCompleteTextView)findViewById(R.id.Country);
        city = (AutoCompleteTextView)findViewById(R.id.Cities);
        countries = getResources().getStringArray(R.array.countriesUE);
        cities =getResources().getStringArray(R.array.citiesSpain);
    }

    private void initialAdapters() {
        countryAdapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1,countries);
        cityAdapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1,cities);
        country.setAdapter(countryAdapter);
        city.setAdapter(cityAdapter);

        country.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                countryName = (String) adapterView.getItemAtPosition(i);
                if(!countryName.equals(address.getCountryName())){
                    address.setCountryName(countryName);
                }
                System.out.println(countryName);
            }
        });

        city.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                cityName = (String)adapterView.getItemAtPosition(i);
                if(!cityName.equals(address.getCityName())){
                    address.setCountryName(cityName);
                }
                System.out.println(cityName);
            }
        });
    }

    private void createAddress() {
        address = new Address();
        address.setStreetName(street.getText().toString());
        address.setCountryName(countryName);
        address.setCityName(cityName);
        address.setCode(postcode.getText().toString());
        address.setTelefon(telefonNumber.getText().toString());
    }


    private class addCustomerAddress extends AsyncTask<String,Float,String>{
        ProgressDialog pDialog;
        String NAMESPACE = "urn:Magento";
        String URL = "http://gonegocio.es/index.php/api/v2_soap/";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.d("Hi", "Download Commencing");
            pDialog = new ProgressDialog(AddressInfo.this);
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
                addAddress();

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

        }

        private void setupSessionLogin() throws IOException, XmlPullParserException {
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
            sessionId = (String) result;
        }

        private void addAddress() throws IOException, XmlPullParserException {
            SoapObject addressInfo = new SoapObject(NAMESPACE, "shoppingCartCustomerAddressEntity");
            Customer customer = DataHolder.getCustomer();
            PropertyInfo pi = new PropertyInfo();
            System.out.println(address.getCityName()+" "+address.getStreetName()+" "+address.getCountryName());
            System.out.println(customer.getFirstName()+" "+customer.getLastName()+" "+customer.getEmailAdress());
            pi.setName("mode");
            pi.setValue("shipping");
            pi.setType(String.class);
            addressInfo.addProperty(pi);

            pi = new PropertyInfo();
            pi.setName("firstname");
            pi.setValue(customer.getFirstName());
            pi.setType(String.class);
            addressInfo.addProperty(pi);

            pi = new PropertyInfo();
            pi.setName("lastname");
            pi.setValue(customer.getFirstName());
            pi.setType(String.class);
            addressInfo.addProperty(pi);

            pi = new PropertyInfo();
            pi.setName("street");
            pi.setValue(address.getStreetName());
            pi.setType(String.class);
            addressInfo.addProperty(pi);

            pi = new PropertyInfo();
            pi.setName("city");
            pi.setValue(address.getCityName());
            pi.setType(String.class);
            addressInfo.addProperty(pi);

            pi = new PropertyInfo();
            pi.setName("postcode");
            pi.setValue(address.getCode());
            pi.setType(String.class);
            addressInfo.addProperty(pi);

            pi = new PropertyInfo();
            pi.setName("country_id");
            pi.setValue(address.getCountryName());
            pi.setType(String.class);
            addressInfo.addProperty(pi);

            pi = new PropertyInfo();
            pi.setName("telephone");
            pi.setValue(address.getTelefon());
            pi.setType(String.class);
            addressInfo.addProperty(pi);

            pi = new PropertyInfo();
            pi.setName("is_default_billing");
            pi.setValue(0);
            pi.setType(Integer.class);
            addressInfo.addProperty(pi);

            pi = new PropertyInfo();
            pi.setName("is_default_shipping");
            pi.setValue(0);
            pi.setType(Integer.class);
            addressInfo.addProperty(pi);

            SoapObject EntityArray = new SoapObject(NAMESPACE, "shoppingCartProductEntityArray");
            EntityArray.addProperty("customer", addressInfo);
            request = new SoapObject(NAMESPACE, "shoppingCartCustomerAddresses");
            request.addProperty("sessionId", sessionId);
            request.addProperty("quoteId", DataHolder.getCartId());
            request.addProperty("customer", EntityArray);

            env.setOutputSoapObject(request);
            androidHttpTransport.call("", env);
            boolean ok = (boolean) env.getResponse();
            if (ok == true) {
                System.out.println("add address correctly");
            }
        }


    }






    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_address_info, menu);
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
        TextView title = (TextView)mCustomView.findViewById(R.id.title_text);
        title.setVisibility(View.GONE);
        cart.setVisibility(View.GONE);
        TextView listNumber = (TextView)mCustomView.findViewById(R.id.number);
        listNumber.setVisibility(View.GONE);
        actionBar.setCustomView(mCustomView);
        actionBar.setDisplayShowCustomEnabled(true);
    }
}
