package myaplication.tfg.org.myapplication;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.view.Menu;
import android.view.MenuItem;
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
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.json.JSONException;
import org.ksoap2.serialization.SoapObject;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import cn.pedant.SweetAlert.SweetAlertDialog;
import myaplication.tfg.org.models.Address;
import myaplication.tfg.org.models.Cart;
import myaplication.tfg.org.models.CheckMoney;
import myaplication.tfg.org.models.CreditCard;
import myaplication.tfg.org.models.Customer;
import myaplication.tfg.org.models.PayPal;
import myaplication.tfg.org.models.Payment;
import myaplication.tfg.org.models.ProductSimple;

public class PaymentMethodInfo extends ActionBarActivity {

    private static final String TAG = "paymentExample";
    private static final String CONFIG_ENVIRONMENT = PayPalConfiguration.ENVIRONMENT_SANDBOX;
    private static final String CONFIG_CLIENT_ID = "ASEuZBYG5OB2GiNls9l58fK2DZNDOKQuacXxtbirapPB0xpPgLsVHDpfvYM9o289hSpbbZWx05I_aUl1";
    private static final int REQUEST_CODE_PAYMENT = 1;
    private static final int REQUEST_CODE_FUTURE_PAYMENT = 2;
    private static final int REQUEST_CODE_PROFILE_SHARING = 3;
    private static PayPalConfiguration config = new PayPalConfiguration()
            .environment(CONFIG_ENVIRONMENT)
            .clientId(CONFIG_CLIENT_ID)
            .merchantName("Example Merchant")
            .merchantPrivacyPolicyUri(Uri.parse("https://www.example.com/privacy"))
            .merchantUserAgreementUri(Uri.parse("https://www.example.com/legal"));

    private Button paypal;
    private Button checkMoney;
    private Button creditCard;
    private Cart cart;
    private Payment payment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_method_info);
        cart = DataHolder.getCart();
        Intent intent = new Intent(this, PayPalService.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
        startService(intent);
        checkMoney = (Button)findViewById(R.id.checkMoney);
        checkMoney.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(cart.getListItems().size()==0){
                    noItemsWarning();
                }
                else{
                    try {
                        checkMoneyListener();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (XmlPullParserException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        creditCard = (Button)findViewById(R.id.CreditCard);
        creditCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(cart.getListItems().size()==0){
                    noItemsWarning();
                }else {
                    try {
                        creditCardListener();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (XmlPullParserException e) {
                        e.printStackTrace();
                    }
                }
            }
        });


        paypal = (Button)findViewById(R.id.PayPal);
        paypal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if(cart.getListItems().size()==0){
                        noItemsWarning();
                    }else{
                        paypalButtonlistener();
                    }

                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    private void noItemsWarning() {
        new SweetAlertDialog(PaymentMethodInfo.this, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("No items")
                .setContentText("You can not pay because you do not have any items")
                .setConfirmText("Ok")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.dismiss();
                        Intent intent = new Intent(PaymentMethodInfo.this,FisrtPage.class);
                        startActivity(intent);
                    }
                })
                .show();

    }

    private void creditCardListener() throws IOException, XmlPullParserException {
        payment = new CreditCard();
        payment.setPayMethod("ccsave");
        new setPaymentToCart().execute();
        new createOrder().execute();
    }

    private void checkMoneyListener() throws IOException, XmlPullParserException {
        payment = new CheckMoney();
        payment.setPayMethod("checkmo");
        new setPaymentToCart().execute();
        new createOrder().execute();

    }


    private class setPaymentToCart extends AsyncTask<String,Float,String>{
        @Override
        protected String doInBackground(String... strings) {

            try {
              cart.setPayment(payment);

            } catch (IOException e) {
                e.printStackTrace();
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            }
            return "excuted";
        }

    }


    private class createOrder extends AsyncTask<String,Float,String>{
        ProgressDialog pDialog;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.d("Hi", "Download Commencing");
            pDialog = new ProgressDialog(PaymentMethodInfo.this);
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
                cart.createOrder();

            } catch (IOException e) {
                e.printStackTrace();
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            }
            return "excuted";
        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            pDialog.dismiss();
            try {
                toFirstPage();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            }
        }
    }

private void toFirstPage() throws IOException, XmlPullParserException {
    cart = new Cart();
    DataHolder.setCart(cart);
    DataHolder.setNumber(0);
    DataHolder.setListCartItems(new HashMap<String, ProductSimple>());
    AlertDialog.Builder builder = new AlertDialog.Builder(this);
    builder.setTitle("Payment received correctly!")
            .setIcon(R.drawable.ok)
            .setMessage("payment received,we will process the shipping soon.")
            .setPositiveButton("Return to first page", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    Intent intent = new Intent(PaymentMethodInfo.this,FisrtPage.class);
                    startActivity(intent);
                }
            });
    AlertDialog dialog = builder.create();
    dialog.setCanceledOnTouchOutside(false);
    dialog.show();

}




    private void paypalButtonlistener() throws ParseException {
        PayPalPayment thingToBuy = getStuffToBuy(PayPalPayment.PAYMENT_INTENT_SALE);
        addAppProvidedShippingAddress(thingToBuy);
        payment = new PayPal();
        payment.setPayMethod("banktransfer");
        new setPaymentToCart().execute();
        Intent intent = new Intent(PaymentMethodInfo.this, PaymentActivity.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
        intent.putExtra(PaymentActivity.EXTRA_PAYMENT, thingToBuy);
        startActivityForResult(intent, REQUEST_CODE_PAYMENT);
    }


    private PayPalPayment getStuffToBuy(String paymentIntent) throws ParseException {
        //--- include an item list, payment amount details

        List<PayPalItem> itemsss = new ArrayList<>();
        String pattern = "######0.00";
        DecimalFormat formatter = (DecimalFormat)NumberFormat.getNumberInstance(Locale.US);
        formatter.applyPattern(pattern);

        for(ProductSimple p : cart.getListItems()){
            String price = p.getPrice().replaceAll(",",".");
            Double result = Double.parseDouble(price);
            System.out.println("item price"+" "+result);
            itemsss.add(new PayPalItem(p.getTitle(),p.getItemNumber(),new BigDecimal(formatter.format(result)),"EUR",p.getSku()));
        }
        PayPalItem[] items = new PayPalItem[itemsss.size()];
        items = itemsss.toArray(items);


        BigDecimal subtotal = PayPalItem.getItemTotal(items);
        String shipPrice = cart.getShippingMethod().getPrice();
        Double ship = Double.parseDouble(shipPrice);

        System.out.println("ship Price"+" " +formatter.format(ship));
        BigDecimal shipping = new BigDecimal(formatter.format(ship));
        BigDecimal tax = new BigDecimal("0.00");
        PayPalPaymentDetails paymentDetails = new PayPalPaymentDetails(shipping, subtotal, tax);
        BigDecimal amount = subtotal.add(shipping).add(tax);
        String totalAmount = amount.toString();
        Double total = Double.parseDouble(totalAmount);
        PayPalPayment payment = new PayPalPayment(new BigDecimal(formatter.format(total)), "EUR", "All items", paymentIntent);
        payment.items(items).paymentDetails(paymentDetails);

        //--- set other optional fields like invoice_number, custom field, and soft_descriptor
        payment.custom("This is text that will be associated with the payment that the app can use.");

        return payment;
    }


    private void addAppProvidedShippingAddress(PayPalPayment paypalPayment) {
        Address address = cart.getAddressInfo();
        Customer customer = cart.getCustomerInfo();
        ShippingAddress shippingAddress =
                new ShippingAddress().recipientName(customer.getLastName()+" "+customer.getFirstName()).line1(address.getStreetName())
                        .city(address.getCityName()).state(address.getCountryName()).postalCode(address.getCode()).countryCode("ES").line2("Telephone Number: " + address.getTelefon());

        paypalPayment.providedShippingAddress(shippingAddress);
    }

    private PayPalOAuthScopes getOauthScopes() {
        /* create the set of required scopes
         * Note: see https://developer.paypal.com/docs/integration/direct/identity/attributes/ for mapping between the
         * attributes you select for this app in the PayPal developer portal and the scopes required here.
         */
        Set<String> scopes = new HashSet<String>(
                Arrays.asList(PayPalOAuthScopes.PAYPAL_SCOPE_EMAIL, PayPalOAuthScopes.PAYPAL_SCOPE_ADDRESS) );
        return new PayPalOAuthScopes(scopes);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_PAYMENT) {
            if (resultCode == Activity.RESULT_OK) {
                PaymentConfirmation confirm =
                        data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
                if (confirm != null) {
                    try {
                        Log.i(TAG, confirm.toJSONObject().toString(4));
                        Log.i(TAG, confirm.getPayment().toJSONObject().toString(4));
                        cart = DataHolder.getCart();
                        new createOrder().execute();
                    } catch (JSONException e) {
                        Log.e(TAG, "an extremely unlikely failure occurred: ", e);
                    }
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Log.i(TAG, "The user canceled.");
            } else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID) {
                Log.i(
                        TAG,
                        "An invalid Payment or PayPalConfiguration was submitted. Please see the docs.");
            }
        } else if (requestCode == REQUEST_CODE_FUTURE_PAYMENT) {
            if (resultCode == Activity.RESULT_OK) {
                PayPalAuthorization auth =
                        data.getParcelableExtra(PayPalFuturePaymentActivity.EXTRA_RESULT_AUTHORIZATION);
                if (auth != null) {
                    try {
                        Log.i("FuturePaymentExample", auth.toJSONObject().toString(4));

                        String authorization_code = auth.getAuthorizationCode();
                        Log.i("FuturePaymentExample", authorization_code);

                        sendAuthorizationToServer(auth);
                        Toast.makeText(
                                getApplicationContext(),
                                "Future Payment code received from PayPal", Toast.LENGTH_LONG)
                                .show();

                    } catch (JSONException e) {
                        Log.e("FuturePaymentExample", "an extremely unlikely failure occurred: ", e);
                    }
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Log.i("FuturePaymentExample", "The user canceled.");
            } else if (resultCode == PayPalFuturePaymentActivity.RESULT_EXTRAS_INVALID) {
                Log.i(
                        "FuturePaymentExample",
                        "Probably the attempt to previously start the PayPalService had an invalid PayPalConfiguration. Please see the docs.");
            }
        } else if (requestCode == REQUEST_CODE_PROFILE_SHARING) {
            if (resultCode == Activity.RESULT_OK) {
                PayPalAuthorization auth =
                        data.getParcelableExtra(PayPalProfileSharingActivity.EXTRA_RESULT_AUTHORIZATION);
                if (auth != null) {
                    try {
                        Log.i("ProfileSharingExample", auth.toJSONObject().toString(4));

                        String authorization_code = auth.getAuthorizationCode();
                        Log.i("ProfileSharingExample", authorization_code);

                        sendAuthorizationToServer(auth);
                        Toast.makeText(
                                getApplicationContext(),
                                "Profile Sharing code received from PayPal", Toast.LENGTH_LONG)
                                .show();

                    } catch (JSONException e) {
                        Log.e("ProfileSharingExample", "an extremely unlikely failure occurred: ", e);
                    }
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Log.i("ProfileSharingExample", "The user canceled.");
            } else if (resultCode == PayPalFuturePaymentActivity.RESULT_EXTRAS_INVALID) {
                Log.i(
                        "ProfileSharingExample",
                        "Probably the attempt to previously start the PayPalService had an invalid PayPalConfiguration. Please see the docs.");
            }
        }
    }

    private void sendAuthorizationToServer(PayPalAuthorization authorization) {
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_payment_method_info, menu);
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
