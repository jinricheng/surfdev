package myaplication.tfg.org.myapplication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.provider.ContactsContract;
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
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
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
import java.util.Collection;
import java.util.HashMap;
import java.util.List;


public class MasVendidos extends ActionBarActivity implements AbsListView.OnScrollListener {
    private MyProductAdapter adapter1;
    private SoapSerializationEnvelope env;
    private SoapObject request;
    private String sessionId;
    private HttpTransportSE androidHttpTransport;
    private List<String> size;
    private List<String> product_id;
    private ProductConfigurable p_configurable;
    private ProductSimple pp_simple;
    private List<SoapObject> listAllItems;
    private HashMap<String,String>allSize;
    private String section;
    private int count;
    private int allitems =0;
    private boolean isPageDiv;

    private List<ProductConfigurable> listConfigurable;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mas_vendidos);
        getActivityTitle();
        initializeVariable();
        count =0;
        listAllItems = new ArrayList<>();
        listConfigurable = new ArrayList<>();
        sessionId = "";

        new DownLoad().execute();
    }

    /*initialize all variable that we need for this activity*/
    private void initializeVariable() {
        product_id = new ArrayList<String>();
        pp_simple =new ProductSimple();
        allSize = new HashMap<String,String>();

    }

    /*initialize the activity title*/
    private void getActivityTitle() {
        Bundle bundle = getIntent().getExtras();
        String title = (String)bundle.get("name");
        TextView text= (TextView)findViewById(R.id.special_title);

        final LayoutInflater factory = getLayoutInflater();
        final View view= factory.inflate(R.layout.topseller_list, null);

        ImageView imageView = (ImageView) view.findViewById(R.id.special_icon);
        text.setText(title);
        section=title;
    }



    /*async task that do the work of downloading products' info*/
    private class DownLoad extends AsyncTask<String, Float, String> {
        ProgressDialog pDialog;
        SoapObject r;
        String NAMESPACE = "urn:Magento";
        String URL = "http://gonegocio.es/index.php/api/v2_soap/";
        List<ProductConfigurable> productConfigurables = new ArrayList<>();
        private int addItemsNumber = 4;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.d("Hi", "Download Commencing");
            pDialog = new ProgressDialog(MasVendidos.this);
            String message = "Esperando...";
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

                if(sessionId.equals("")){
                    setupSessionLogin();}
 /*               StringArraySerializer stringArray = new StringArraySerializer();
                  stringArray.add("7");
                  stringArray.add("8");
                  PropertyInfo stringArrayProperty = new PropertyInfo();
                  stringArrayProperty.setName("products");
                  stringArrayProperty.setValue(stringArray);
                  stringArrayProperty.setType(stringArray.getClass());

*/                Log.d("list all items size",Integer.toString(listAllItems.size()));
                if(listAllItems.size()==0){
                    getAllListItem();}
                createPartOfItems();

            } catch (Exception e) {
                e.printStackTrace();
            }
            return "Executed!";

        }

        /*get the session id from the web */
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
            sessionId = (String)result;
        }

        private void getAllListItem() throws IOException, XmlPullParserException {
            request = new SoapObject(NAMESPACE, "catalogProductList");
            request.addProperty("sessionId", sessionId);
            env.setOutputSoapObject(request);
            androidHttpTransport.call("", env);
            r = (SoapObject) env.getResponse();
            Log.d("Products Gotten",r.toString());
            SoapObject child = (SoapObject) r.getProperty(0);
            String type = (String)child.getProperty("type");
            if((type.equals("configurable"))){
                startWithConfigurable();
            }
            else{
                startWithSimple();
            }
        }

        private void startWithSimple() throws IOException, XmlPullParserException {
            for (int i = 0; i < r.getPropertyCount(); i++) {
                SoapObject child = (SoapObject) r.getProperty(i);
                listAllItems.add(child);
            }
        }

        private void createPartOfItems() throws IOException, XmlPullParserException {
            String temp = new String();
            List<String> simpleProductId = new ArrayList<>();
            int tempCount = count+addItemsNumber;
            while(count<tempCount && allitems<listAllItems.size()) {
                SoapObject child = listAllItems.get(allitems);
                String name = (String) child.getProperty("name");
                String type = (String) child.getProperty("type");
                if (!name.equals(temp) && type.equals("configurable")) {
                    p_configurable = createConfigurableProduct(child);
                    p_configurable.addSimpleProductListId(simpleProductId);
                    productConfigurables.add(p_configurable);
                    p_configurable.setSection(section);
                    temp = name;
                    simpleProductId = new ArrayList<>();
                    Log.d("product configurable",child.toString());
                    count = count+1;
                } else {
                    simpleProductId.add((String) child.getProperty("product_id"));
                }

                allitems++;
            }
            getIndividualProductInfo();

        }

        private void startWithConfigurable() throws IOException, XmlPullParserException {
            String temp = new String();
            List<String> simpleProductId = new ArrayList<>();
            for (int i = 0; i < r.getPropertyCount(); i++) {
                SoapObject child = (SoapObject) r.getProperty(i);
                String name =(String)child.getProperty("name");
                String type = (String)child.getProperty("type");
                if(!name.equals(temp) && type.equals("configurable")){
                    p_configurable = createConfigurableProduct(child);
                    productConfigurables.add(p_configurable);
                    temp = name;
                }
                else{

                    p_configurable.addSimpleProductId((String)child.getProperty("product_id"));
                }
            }
            for(ProductConfigurable pp : productConfigurables){
                List<String> result = pp.getSimpleProductId();
                for(int i =0;i<result.size();i++){
                    Log.d("simple Id",result.get(i));
                }
            }
        }

        public void getIndividualProductInfo() throws IOException, XmlPullParserException {
            String temp =new String();
            ProductConfigurable p = new ProductConfigurable();
            for (int i =0;i< productConfigurables.size();i++) {
                p = productConfigurables.get(i);
                request = new SoapObject(NAMESPACE, "catalogProductInfo");
                request.addProperty("sessionId", sessionId);
                request.addProperty("productId", p.getProduct_id());
                env.setOutputSoapObject(request);
                androidHttpTransport.call("", env);
                r = (SoapObject) env.getResponse();
                Double price=Double.parseDouble((String)r.getProperty("price"));
                String pr = String.format("%.2f",price);
                p.setPrice(pr);
                p.setDescription((String) r.getProperty("description"));
                p.setSection(section);
                productConfigurables.set(i, p);
                Log.d("info",p.toString());
            }

        }

        private ProductConfigurable createConfigurableProduct(SoapObject r) throws IOException, XmlPullParserException {
            ProductConfigurable p = new ProductConfigurable();
            p.setProduct_id((String) r.getProperty("product_id"));
            p.setSku((String) r.getProperty("sku"));
            p.setTitle((String) r.getProperty("name"));
            String imageUrl = "http://gonegocio.es/media/catalog/product/androidImage/"+p.getProduct_id()+".jpg";
            p.setImage(imageUrl);
            return p;}

        /*get the attribute size with the respect label and value, save them for future use*/
        private ProductSimple createSimpleProduct(SoapObject r) {
            ProductSimple p = new ProductSimple();
            p.setProduct_id((String) r.getProperty("product_id"));
            p.setSku((String) r.getProperty("sku"));
            p.setTitle((String) r.getProperty("name"));
            return p;
        }


          /*     p.setDescription((String)r.getProperty("description"));
               Double price=Double.parseDouble((String)r.getProperty("price"));
               String pr = String.format("%.2f",price);
             //  String ImageUrl= getProductImage(p.getProduct_id());
               p.setPrice(pr);
               return p;*/


        protected void onProgressUpdate(Float... valores) {
            int p = Math.round(100 * valores[0]);
            pDialog.setProgress(p);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            Log.d("Hi", "Done Downloading.");
            Log.d("count number",Integer.toString(count));
            if(count == addItemsNumber){
                createItemList(productConfigurables);}
            else {
                Log.d("size product", Integer.toString(productConfigurables.size()));
                if(adapter1 == null){
                    createItemList(productConfigurables);
                    TextView error = (TextView)findViewById(R.id.error_message);
                    error.setText("We have problem with connection,try again please");
                }
                adapter1.addAllProduct(productConfigurables);
            }

            pDialog.dismiss();

        }


    }



    private void createItemList(List<ProductConfigurable> productConfigurables){
        ListView list = (ListView)findViewById(R.id.top_seller_list);
        adapter1 = new MyProductAdapter(this, productConfigurables,R.layout.topseller_list);
        // moreView =getLayoutInflater().inflate(R.layout.listview_footer, null);

        list.setAdapter(adapter1);
        listConfigurable= productConfigurables;
        list.setOnItemClickListener(new productDetailClickListener());
        list.setOnScrollListener(this);

    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (isPageDiv
                && scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
            if(allitems<listAllItems.size()){
                new DownLoad().execute();}


        }

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        isPageDiv = (firstVisibleItem + visibleItemCount == totalItemCount);

    }

    private class productDetailClickListener implements ListView.OnItemClickListener{
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            loadProductDetail(position);
        }
    }

    private void loadProductDetail(int position){
        Bundle bundle = new Bundle();
        bundle.putSerializable("key", listConfigurable.get(position));
        Intent intent = new Intent(this,IndividualItemInfo.class);
        intent.putExtra("bundle",bundle);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
      getCustomizedActionBar();
        return true;
    }

    private void getCustomizedActionBar(){
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        LayoutInflater mInflater = LayoutInflater.from(this);
        View mCustomView = mInflater.inflate(R.layout.customactionbar, null);
        ImageButton imageButton = (ImageButton) findViewById(R.id.shopCartButton);
        TextView listNumber = (TextView)mCustomView.findViewById(R.id.number);
        int number = DataHolder.getNumber();
        listNumber.setText(Integer.toString(number));
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

    @Override
    public void onResume(){
        super.onResume();
        getCustomizedActionBar();
    }
}
