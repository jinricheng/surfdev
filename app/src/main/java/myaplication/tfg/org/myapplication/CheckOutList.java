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
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.gitonway.lee.niftymodaldialogeffects.lib.Effectstype;
import com.gitonway.lee.niftymodaldialogeffects.lib.NiftyDialogBuilder;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;


public class CheckOutList extends ActionBarActivity {
    String NAMESPACE = "urn:Magento";
    String URL = "http://gonegocio.es/index.php/api/v2_soap/";
    private List<ProductSimple> checkOutItemsList;
    private SoapSerializationEnvelope env;
    private HttpTransportSE androidHttpTransport;
    private SoapObject request;
    private SoapObject r;
    private String sessionId;
    private int cartId;
    private ListView listItems;
    private CheckOutItemsAdapter adapter;
    private TextView total;
    private String totalAmount;
    private Button continueButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_out_list);
        initCusmizedActionBar();
        total = (TextView)findViewById(R.id.total_Amount);
        continueButton = (Button)findViewById(R.id.continuebutton);

        obtainCheckOutItemList();
        continueListener();



    }

    private void continueListener() {

        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new addItems().execute();
            }
        });
    }

    private void obtainCheckOutItemList() {

        checkOutItemsList = new ArrayList<ProductSimple>(DataHolder.getListCartItems().values());
        if(checkOutItemsList.size()==0){
            noItemWarning();
        }
        new shopCart().execute();
        listItems = (ListView)findViewById(R.id.check_out_list);
        adapter = new CheckOutItemsAdapter(this,checkOutItemsList,R.layout.check_outl_list_items);
        listItems.setAdapter(adapter);
        totalAmount = getTotalAmount();
        total.setText(totalAmount);
        listenItems();
    }

    private String getTotalAmount() {
        Double totalAmount = 0.0;
        DecimalFormat formatter = new DecimalFormat("#,###.00");
        for(ProductSimple p : checkOutItemsList){
            String priceIndividual = p.getPrice();
            priceIndividual = priceIndividual.replaceAll(",",".");
            totalAmount = totalAmount+Double.parseDouble(priceIndividual)*p.getItemNumber();
        }
        return "Total: "+formatter.format(totalAmount)+" EUR";
    }

    private void listenItems() {
       listItems.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
           @Override
           public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
               final int position = i;
               new SweetAlertDialog(CheckOutList.this, SweetAlertDialog.WARNING_TYPE)
                       .setTitleText("Delete Warning !!")
                       .setContentText("Do you really want to delete this item ?")
                       .setConfirmText("Yes")
                       .setCancelText("No")
                       .showCancelButton(true)
                       .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                           @Override
                           public void onClick(SweetAlertDialog sDialog) {
                               removeDataHolderItem(position);
                               checkOutItemsList.remove(position);
                               totalAmount = getTotalAmount();
                               total.setText(totalAmount);
                               adapter.notifyDataSetChanged();
                               sDialog.dismiss();
                           }
                       })
                       .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                           @Override
                           public void onClick(SweetAlertDialog sweetAlertDialog) {
                               sweetAlertDialog.dismiss();
                           }
                       })
                       .show();
               return false;
           }
       });
    }

    /*remove the item in the shop cart*/
    private void removeDataFromShoppingCart(ProductSimple productSimple) throws IOException, XmlPullParserException {
        SoapObject SingleProduct = new SoapObject(NAMESPACE, "shoppingCartProductEntity");
        PropertyInfo pi = new PropertyInfo();
        pi.setName("product_id");
        pi.setValue(Integer.parseInt(productSimple.getProduct_id()));
        pi.setType(Integer.class);
        SingleProduct.addProperty(pi);

        pi = new PropertyInfo();
        pi.setName("sku");
        pi.setValue(productSimple.getSku());
        pi.setType(String.class);
        SingleProduct.addProperty(pi);

        pi = new PropertyInfo();
        pi.setName("qty");
        pi.setValue(productSimple.getQuantity());
        pi.setType(Double.class);
        SingleProduct.addProperty(pi);
        SoapObject EntityArray = new SoapObject(NAMESPACE, "shoppingCartProductEntityArray");
        EntityArray.addProperty("products", SingleProduct);

        request = new SoapObject(NAMESPACE, "shoppingCartProductRemove");
        request.addProperty("sessionId", sessionId);
        request.addProperty("quoteId", cartId);
        request.addProperty("products", EntityArray);
        env.setOutputSoapObject(request);
        androidHttpTransport.call("", env);
        boolean ok = (boolean) env.getResponse();

        if (ok == true) {
            System.out.println("remove product correctly");
        }
        getRetriveInformation();
    }


    private void removeDataHolderItem(int position) {
        DataHolder.setNumber(DataHolder.getNumber() - 1);
        ProductSimple p = checkOutItemsList.get(position);
        DataHolder.updateCanceledProductSimpleQuantity(p.getProduct_id(),p.getItemNumber());
    }

    private void noItemWarning() {
        NiftyDialogBuilder dialogBuilder=NiftyDialogBuilder.getInstance(this);
        Effectstype effectstype = Effectstype.Fadein;
        dialogBuilder
                .withTitle("No items !")
                .withMessage("You do not have anything in the cart")
                .withEffect(effectstype)
                .withTitleColor("#FFFFFF")
                .withDialogColor("#FE2E64")
                .show();

    }

    /*class of create a shopcart to magento using api*/
    private class shopCart extends AsyncTask<String, Float, String>{
        ProgressDialog pDialog;
        String NAMESPACE = "urn:Magento";
        String URL = "http://gonegocio.es/index.php/api/v2_soap/";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.d("Hi", "Download Commencing");
            pDialog = new ProgressDialog(CheckOutList.this);
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
                if(DataHolder.getCartId() ==0){
                createShopCart();}
                else{
                    cartId = DataHolder.getCartId();
                }

            } catch (IOException e) {
                e.printStackTrace();
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            }

            return "executed";
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

        private void createShopCart() throws IOException, XmlPullParserException {
             request = new SoapObject(NAMESPACE,"shoppingCartCreate");
             request.addProperty("sessionId", sessionId);
             env.setOutputSoapObject(request);
             androidHttpTransport.call("", env);
             cartId = (Integer)env.getResponse();
             DataHolder.setCartId(cartId);
        }
    }


    /*add the selected items to the created shop cart of magento using api*/
    private class addItems extends AsyncTask<String, Float, String> {
        ProgressDialog pDialog;
        String NAMESPACE = "urn:Magento";
        String URL = "http://gonegocio.es/index.php/api/v2_soap/";


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.d("Hi", "Download Commencing");
            pDialog = new ProgressDialog(CheckOutList.this);
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
                addToCart();

            } catch (IOException e) {
                e.printStackTrace();
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            }
            return "executed";
        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            pDialog.dismiss();
            if(checkOutItemsList.size()==0){
                new SweetAlertDialog(CheckOutList.this, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Empty Cart")
                        .setContentText("add some items please XD..")
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
                Intent intent = new Intent(CheckOutList.this,CustomerInfo.class);
                startActivity(intent);}
        }

        private void addToCart() throws IOException, XmlPullParserException {
            for(ProductSimple p : checkOutItemsList) {
                SoapObject SingleProduct = new SoapObject(NAMESPACE, "shoppingCartProductEntity");
                PropertyInfo pi = new PropertyInfo();
                pi.setName("product_id");
                pi.setValue(Integer.parseInt(p.getProduct_id()));
                pi.setType(Integer.class);
                SingleProduct.addProperty(pi);

                pi = new PropertyInfo();
                pi.setName("sku");
                pi.setValue(p.getSku());
                pi.setType(String.class);
                SingleProduct.addProperty(pi);

                pi = new PropertyInfo();
                pi.setName("qty");
                pi.setValue(p.getQuantity());
                pi.setType(Double.class);
                SingleProduct.addProperty(pi);
                SoapObject EntityArray = new SoapObject(NAMESPACE, "shoppingCartProductEntityArray");
                EntityArray.addProperty("products", SingleProduct);

                request = new SoapObject(NAMESPACE, "shoppingCartProductAdd");
                request.addProperty("sessionId", sessionId);
                request.addProperty("quoteId", cartId);
                request.addProperty("products", EntityArray);
                env.setOutputSoapObject(request);
                androidHttpTransport.call("", env);
                boolean ok = (boolean) env.getResponse();

                if (ok == true) {
                    System.out.println("add product correctly");
                }
            }
            getRetriveInformation();
        }


    }
    private void getRetriveInformation() throws IOException, XmlPullParserException {
        request = new SoapObject(NAMESPACE,"shoppingCartProductList");
        request.addProperty("sessionId", sessionId);
        request.addProperty("quoteId", cartId);
        env.setOutputSoapObject(request);
        androidHttpTransport.call("", env);
        SoapObject r  = (SoapObject)env.getResponse();
        Log.d("retriveList",r.toString());

    }
    private void initCusmizedActionBar() {
        getCustomizedActionBar();

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
}
