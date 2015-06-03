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
    private Cart cart;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address_info);
        getCustomizedActionBar();
        cart =DataHolder.getCart();
        initialVariable();
        initialAdapters();
        continuePayment = (Button)findViewById(R.id.continueShipment);
        continuePayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createAddress();
                new addCustomerAddress().execute();
                Intent intent = new Intent(AddressInfo.this,ShippingAndPaymentMethod.class);
                startActivity(intent);
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
        address = new Address();
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
        address.setStreetName(street.getText().toString());
        address.setCountryName(countryName);
        address.setCityName(cityName);
        address.setCode(postcode.getText().toString());
        address.setTelefon(telefonNumber.getText().toString());
    }


    private class addCustomerAddress extends AsyncTask<String,Float,String>{
        ProgressDialog pDialog;
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
                cart.addAddress(address);

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
