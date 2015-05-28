package myaplication.tfg.org.myapplication;

import android.app.ActionBar;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.ActionBarDrawerToggle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import cn.pedant.SweetAlert.SweetAlertDialog;

public class IndividualItemInfo extends ActionBarActivity {
    private ImageButton imageButton;
    private ImageButton plusbutton;
    private ImageButton minusbutton;
    private TextView quantity;
    private LinearLayout layoutSize;             //查看图片
    private LinearLayout layoutCart;
    private LinearLayout layoutQuantity;
    private PopupWindow popSize;
    private PopupWindow popQuantity;
    private PopupWindow popCart;
    private HashMap<String,String> sizeAndProduct;
    private HashMap<String,String> allSize;
    private int quantityNumber;
    private String size;
    private RadioGroup radioGroup;
    private RadioButton radioButton;
    private String sessionId;
    private SoapSerializationEnvelope env;
    private HttpTransportSE androidHttpTransport;
    private SoapObject request;
    private SoapObject r;
    private ProductConfigurable p;
    private List<String> simpleProductsIds;
    private List<String> sizeAvailable;
    private TextView listNumber;
    private int totalQuantity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_individual_item_info);
        Bundle bundle = getIntent().getBundleExtra("bundle");
        p =(ProductConfigurable)bundle.getSerializable("key");
        initVariables();
        initCusmizedActionBar();
        new DownLoadSimpleProduct().execute();
        quantityNumber = 0;

        //  setQuantityButton();
    }

    private void initVariables() {
        simpleProductsIds = new ArrayList<>();
        sizeAndProduct = new HashMap<>();
        sizeAvailable = new ArrayList<>();
        allSize = new HashMap<>();
        size = "";
    }


    private void getItemInfo() {
        TextView title = (TextView)findViewById(R.id.detail_title);
        TextView price = (TextView)findViewById(R.id.detail_price);
        TextView description = (TextView)findViewById(R.id.detail_description);
        ImageView image = (ImageView)findViewById(R.id.detail_image);
        TextView d = (TextView)findViewById(R.id.d);
        String fullprice = "Price: " + p.getPrice();
        title.setText(p.getTitle());
        UrlImageViewHelper.setUrlDrawable(image, p.getImage());
        description.setText(p.getDescription());
        price.setText(fullprice);
        d.setText("Description");
    }

    private class DownLoadSimpleProduct extends AsyncTask<String, Float, String> {
        ProgressDialog pDialog;
        String NAMESPACE = "urn:Magento";
        String URL = "http://gonegocio.es/index.php/api/v2_soap/";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.d("Hi", "Download Commencing");
            pDialog = new ProgressDialog(IndividualItemInfo.this);
            String message = "Waiting...";
            SpannableString ss2 = new SpannableString(message);
            ss2.setSpan(new RelativeSizeSpan(2f), 0, ss2.length(), 0);
            ss2.setSpan(new ForegroundColorSpan(Color.BLACK), 0, ss2.length(), 0);
            pDialog.setMessage(ss2);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                setupSessionLogin();
                getAllSize();
                getAllSimpleProduct();


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
            getItemInfo();
            bottomMenu();
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

        private void getAllSize() throws IOException, XmlPullParserException {
            request = new SoapObject(NAMESPACE, "catalogProductAttributeInfo");
            request.addProperty("sessionId", sessionId);
            request.addProperty("attribute", "size");
            env.setOutputSoapObject(request);
            androidHttpTransport.call("", env);
            r = (SoapObject) env.getResponse();
            SoapObject sizeOptions = (SoapObject) r.getProperty("options");
            for (int i = 0; i < sizeOptions.getPropertyCount(); i++) {
                SoapObject item = (SoapObject) sizeOptions.getProperty(i);
                allSize.put((String) item.getProperty("value"), (String) item.getProperty("label"));
            }
        }

        private String getAdditionalAttributeValue(String productId) throws IOException, XmlPullParserException {
            SoapObject attributes = new SoapObject(NAMESPACE,"attributes");
            SoapObject additional = new SoapObject(NAMESPACE,"additional_attributes");
            additional.addProperty("attribute","size");
            SoapObject requestAtributtes = new SoapObject(NAMESPACE,"catalogProductRequestAttributes");
            requestAtributtes.addProperty("attributes",attributes);
            requestAtributtes.addProperty("additional_attributes",additional);
            request = new SoapObject(NAMESPACE, "catalogProductInfo");
            request.addProperty("sessionId", sessionId);
            request.addProperty("productId", productId);
            request.addProperty("attributes",requestAtributtes);
            env.setOutputSoapObject(request);
            androidHttpTransport.call("", env);
            r = (SoapObject) env.getResponse();
            SoapObject result = (SoapObject)r.getProperty(r.getPropertyCount()-1);
            result= (SoapObject)result.getProperty("item");
            String value = (String)result.getProperty("value");

            return value;
        }

        private void getAllSimpleProduct() throws IOException, XmlPullParserException {
            simpleProductsIds = p.getSimpleProductId();
            getIndividualProductInfo();
            getSimpleProductsStock();
        }


        private void getSimpleProductsStock() throws XmlPullParserException, IOException {
            request = new SoapObject(NAMESPACE,"catalogInventoryStockItemList");
            StringArraySerializer stringArray = new StringArraySerializer();
            for(int i=0;i<simpleProductsIds.size();i++) {
                stringArray.add(simpleProductsIds.get(i));
            }
            PropertyInfo stringArrayProperty = new PropertyInfo();
            stringArrayProperty.setName("products");
            stringArrayProperty.setValue(stringArray);
            stringArrayProperty.setType(stringArray.getClass());
            request.addProperty("sessionId", sessionId);
            request.addProperty(stringArrayProperty);
            env.setOutputSoapObject(request);
            androidHttpTransport.call("", env);
            r = (SoapObject) env.getResponse();
            for(int i =0;i<r.getPropertyCount();i++){
                SoapObject child =(SoapObject)r.getProperty(i);
                DataHolder.setSimpleProductQuantity((String) child.getProperty("product_id"), (String) child.getProperty("qty"));
                System.out.println("productQuantity:"+(String) child.getProperty("qty"));
            }

        }


        public void getIndividualProductInfo() throws IOException, XmlPullParserException {
            for (int i = 0; i < simpleProductsIds.size(); i++) {
                String productId = simpleProductsIds.get(i);
                if (!DataHolder.getListProductSimple().containsKey(productId)) {
                    request = new SoapObject(NAMESPACE, "catalogProductInfo");
                    request.addProperty("sessionId", sessionId);
                    request.addProperty("productId", productId);
                    env.setOutputSoapObject(request);
                    androidHttpTransport.call("", env);
                    r = (SoapObject) env.getResponse();
                    createSimpleProduct();
                }
                else {
                    ProductSimple simple = DataHolder.getProductSimple(productId);
                    sizeAndProduct.put(simple.getSize(),simple.getProduct_id());
                }
            }
        }

        private void createSimpleProduct() throws IOException, XmlPullParserException {
            ProductSimple simple = new ProductSimple();
            simple.setTitle((String) r.getProperty("name"));
            simple.setProduct_id((String) r.getProperty("product_id"));
            simple.setSku((String) r.getProperty("sku"));
            simple.setType((String)r.getProperty("type"));
            String sizeValue = getAdditionalAttributeValue(simple.getProduct_id());
            String size = allSize.get(sizeValue);
            simple.setSize(size);
            DataHolder.addProductSimple(simple);

            sizeAndProduct.put(size,simple.getProduct_id());
            Log.d("products simple size",simple.getSize());
        }
    }

    private void bottomMenu() {
        layoutSize=(LinearLayout)findViewById(R.id.layout_watch);
        layoutCart=(LinearLayout)findViewById(R.id.layout_add_cart);
        layoutQuantity=(LinearLayout)findViewById(R.id.layout_quantity);
        layoutSize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (popSize != null && popSize.isShowing()) {
                    popSize.dismiss();
                } else {
                    setUpPopWindow(v, 1);
                }
            }
        });
        layoutQuantity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(size.equals("")){
                    noSizeChosenWarning();
                }
                else if (popQuantity != null && popQuantity.isShowing()) {
                    popQuantity.dismiss();
                } else {
                    setUpPopWindow(v, 2);
                }
            }
        });
       layoutCart.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               addToCartDialog();
           }
       });
    }

    private void noSizeChosenWarning() {
        new SweetAlertDialog(IndividualItemInfo.this, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("Choose a Size")
                .setContentText("You have to choose a available size first")
                .setConfirmText("Ok")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.dismiss();
                    }
                })
                .show();
    }

    private void addToCartDialog() {
        if(size.equals("")){
            noSizeChosenWarning();
        }
        if(quantityNumber ==0){
            noQuantityChosenWarning();
        }
        else {
            dialogAddToCart();
        }
    }

    private void noQuantityChosenWarning() {
        new SweetAlertDialog(IndividualItemInfo.this, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("Choose Quantity")
                .setContentText("You have to choose at least 1")
                .setConfirmText("Ok")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.dismiss();
                    }
                })
                .show();
    }

    private void dialogAddToCart() {
        new SweetAlertDialog(IndividualItemInfo.this, SweetAlertDialog.NORMAL_TYPE)
                .setTitleText("Do you want add this item:")
                .setContentText("  " + p.getTitle())
                .setCancelText("No,cancel it!")
                .setConfirmText("Yes,add it!")
                .showCancelButton(true)
                .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.dismiss();
                    }
                })
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.setTitleText("Added")
                                .setContentText("Your item has been added to your cart")
                                .setConfirmText("OK")
                                .showCancelButton(false)
                                .setCancelClickListener(null)
                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sDialog) {
                                        String productId = sizeAndProduct.get(size);
                                        ProductSimple simpleToCart = DataHolder.getProductSimple(productId);
                                        simpleToCart.setPrice(p.getPrice());
                                        simpleToCart.setImage(p.getImage());
                                        Log.d("the product chosen ", simpleToCart.toString());
                                        if (!existProductInTheCart(simpleToCart)) {
                                            DataHolder.addItemToCart(simpleToCart);
                                            Log.d("add to cart new Product",simpleToCart.toString());
                                            DataHolder.updateBoughtSimpleProductQuantity(simpleToCart.getProduct_id(),quantityNumber);
                                            updateTotalQuantity();

                                        }
                                        else{
                                            DataHolder.updateBoughtSimpleProductQuantity(simpleToCart.getProduct_id(),quantityNumber);
                                        }
                                        sDialog.dismiss();
                                        CheckoutOcontinueDialog();
                                    }
                                })
                                .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                    }
                })
                .show();

    }

    private boolean existProductInTheCart(ProductSimple simpleToCart) {
        return DataHolder.getListCartItems().containsKey(simpleToCart.getProduct_id());
    }

    private void updateTotalQuantity() {
        String n = listNumber.getText().toString();
        int number = Integer.parseInt(n);
        number = number+1;
        listNumber.setText(String.valueOf(number));
        DataHolder.setNumber(number);
        totalQuantity = totalQuantity-quantityNumber;
        quantityNumber =0;
    }

    private void CheckoutOcontinueDialog() {
        new SweetAlertDialog(IndividualItemInfo.this, SweetAlertDialog.NORMAL_TYPE)
                .setTitleText("Continue o CheckOut")
                .setContentText("Do you want to check out o to continue buying?")
                .setCancelText("Continue to buy")
                .showCancelButton(true)
                .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.dismiss();
                    }
                })
                .setConfirmText("Check Out")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.dismiss();
                        Intent intent = new Intent(IndividualItemInfo.this,CheckOutList.class);
                        startActivity(intent);
                    }
                })
                .show();
    }

    private void setUpPopWindow(View v, int i) {
        initPopUpWindowsSize(v,i);
        int[] location = new int[2];
        int[] location2 = new int[2];
        Log.d("position ", Integer.toString(i));

        switch (i) {
            case 1:
                v.getLocationOnScreen(location);
                popSize.showAtLocation(v, Gravity.NO_GRAVITY, location[0], location[1] - popSize.getHeight());
                layoutSize.setEnabled(true);
                break;
            case 2:

                v.getLocationOnScreen(location2);
                popQuantity.showAtLocation(v, Gravity.NO_GRAVITY, location2[0], location2[1] - popQuantity.getHeight());
                layoutQuantity.setEnabled(true);
                break;
            case 3:
                popCart.showAtLocation(v, Gravity.CENTER, 0,0);
                layoutQuantity.setEnabled(true);
                break;
        }
    }



    private void initPopUpWindowsSize(View v,int number) {
        View customView =null;
        View customView2 = null;
        switch (number){
            case 1:
                customView= getLayoutInflater().inflate(R.layout.pop_windows_size,null);
                popSize = new PopupWindow(customView);
                DisplayMetrics displaymetrics = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
                popSize.setWidth(displaymetrics.widthPixels);
                popSize.setHeight(150);
                popSize.setAnimationStyle(R.style.AnimationPreview);
                popSize.setFocusable(true);
                popSize.setOutsideTouchable(true);

                getItemSize(customView);
                //  setQuantityButton(customView);
                customView.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        if(popSize!=null && popSize.isShowing()){
                            popSize.dismiss();
                            popSize =null;
                        }
                        return false;
                    }
                });
            case 2:
                customView = getLayoutInflater().inflate(R.layout.pop_windows_quantity,null);
                popQuantity = new PopupWindow(customView);
                DisplayMetrics displaymetrics2 = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(displaymetrics2);
                popQuantity.setWidth(displaymetrics2.widthPixels);
                popQuantity.setHeight(150);
                popQuantity.setAnimationStyle(R.style.AnimationPreview);
                popQuantity.setFocusable(true);
                popQuantity.setOutsideTouchable(true);

                setQuantityButton(customView);
                customView.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        if (popQuantity != null && popQuantity.isShowing()) {
                            popQuantity.dismiss();
                            popQuantity = null;
                        }
                        return false;
                    }
                });


        }


    }


    private void setQuantityButton(View v) {
        plusbutton = (ImageButton)v.findViewById(R.id.plus);
        minusbutton = (ImageButton)v.findViewById(R.id.minus);
        quantity =(TextView)v.findViewById(R.id.quantity);
        int initialnumber = 0;
        if(initialnumber == quantityNumber){
            quantity.setText("0");
            Log.d("quantity", Integer.toString(quantityNumber));
            Log.d("Total quantity", Integer.toString(totalQuantity));
        }
        else if(totalQuantity < quantityNumber){
            quantity.setText("0");
            Log.d("quantity", Integer.toString(quantityNumber));
            Log.d("Total quantity",Integer.toString(totalQuantity));
        }
        else{
            quantity.setText(Integer.toString(quantityNumber));
        }
        listen();

    }

    private void listen() {
        plusbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (totalQuantity == 0) {
                    plusbutton.setEnabled(false);
                    minusbutton.setEnabled(false);
                } else {
                    quantityNumber = quantityNumber + 1;
                    if (quantityNumber > totalQuantity) {
                        quantityNumber = totalQuantity;
                        quantity.setText(Integer.toString(quantityNumber));
                        plusbutton.setEnabled(false);
                    } else {
                        quantity.setText(Integer.toString(quantityNumber));
                        plusbutton.setEnabled(true);
                        minusbutton.setEnabled(true);
                    }
                }
            }
        });

        minusbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int number = Integer.valueOf(quantity.getText().toString());
                if (number < 2) {
                    quantity.setText("1");
                    minusbutton.setEnabled(false);
                } else {
                    quantityNumber = quantityNumber - 1;
                    quantity.setText(Integer.toString(quantityNumber));
                    minusbutton.setEnabled(true);
                    plusbutton.setEnabled(true);
                }
            }
        });

    }

    private void getItemSize(View v) {
        String[] spinnerinfo = new String[]{"XS","S","M","L","XL"};
        radioGroup = (RadioGroup)v.findViewById(R.id.size_group);
        float density = getResources().getDisplayMetrics().density;
        int margin =15;
        int id = 0;
        RadioGroup.LayoutParams params = new RadioGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(margin, margin, margin, margin);
        for (int i =0; i<spinnerinfo.length;i++){
            RadioButton tempButton = new RadioButton(this);
            tempButton.setText(spinnerinfo[i]);
            tempButton.setTextSize(20);
            tempButton.setId(i);
            tempButton.setTag(i);

            if(!sizeAndProduct.containsKey(spinnerinfo[i])){
                tempButton.setEnabled(false);
            }
            radioGroup.addView(tempButton,params);

        }
        if(radioButton !=null){
            radioGroup.check(radioButton.getId());
        }
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                radioButton = (RadioButton) group.getChildAt(checkedId);
                size = radioButton.getText().toString();
                String productId = sizeAndProduct.get(size);
                ProductSimple simple = DataHolder.getProductSimple(productId);
                String simpleProductid = simple.getProduct_id();
                totalQuantity = DataHolder.getSimpleProdcutQuantity(simpleProductid);
                Log.d("totalquantity", Integer.toString(totalQuantity));
            }
        });

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



    private void initCusmizedActionBar() {
        getCustomizedActionBar();

    }

    public void getCustomizedActionBar(){
        android.support.v7.app.ActionBar actionBar=getSupportActionBar();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        LayoutInflater mInflater = LayoutInflater.from(this);
        View mCustomView = mInflater.inflate(R.layout.customactionbar, null);
        imageButton = (ImageButton)mCustomView.findViewById(R.id.shopCartButton);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(IndividualItemInfo.this,CheckOutList.class);
                startActivity(intent);
            }
        });
        listNumber = (TextView)mCustomView.findViewById(R.id.number);
        listNumber.setText(Integer.toString(DataHolder.getNumber()));
        actionBar.setCustomView(mCustomView);
        actionBar.setDisplayShowCustomEnabled(true);
    }

    @Override
    public void onResume(){
        super.onResume();
        getCustomizedActionBar();
    }

}
