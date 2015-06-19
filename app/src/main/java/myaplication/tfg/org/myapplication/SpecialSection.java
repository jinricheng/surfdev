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
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import myaplication.tfg.org.Adapters.MyProductAdapter;
import myaplication.tfg.org.ApiMethod.Product;
import myaplication.tfg.org.models.ProductConfigurable;
import myaplication.tfg.org.models.ProductSimple;


public class SpecialSection extends ActionBarActivity implements AbsListView.OnScrollListener {
    private MyProductAdapter adapter1;
    private String section;
    private boolean isPageDiv;
    private Product product;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_special_section);
        initializeVariable();
        getActivityTitle();
        new DownLoad().execute();
    }

    /*initialize all variable that we need for this activity*/
    private void initializeVariable() {
         product = new Product();
    }

    /*initialize the activity title*/
    private void getActivityTitle() {
        Bundle bundle = getIntent().getExtras();
        String title = (String)bundle.get("name");
        TextView text= (TextView)findViewById(R.id.special_title);
        text.setText(title);
        final LayoutInflater factory = getLayoutInflater();
        final View view= factory.inflate(R.layout.topseller_list, null);
        ImageView imageView = (ImageView) view.findViewById(R.id.special_icon);

        section=title;
        product.setSection(section);
    }



    /*async task that do the work of downloading products' info*/
    private class DownLoad extends AsyncTask<String, Float, String> {
        ProgressDialog pDialog;
        SoapObject r;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.d("Hi", "Download Commencing");
            pDialog = new ProgressDialog(SpecialSection.this);
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

                if(product.getSessionId().equals("")){
                    product.setupSessionLogin();}
                if(product.getAllListItemsSize()==0){
                    product.getAllListItem();}
                product.createPartOfItems();

            } catch (Exception e) {
                e.printStackTrace();
            }
            return "Executed!";

        }


        protected void onProgressUpdate(Float... valores) {
            int p = Math.round(100 * valores[0]);
            pDialog.setProgress(p);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            Log.d("Hi", "Done Downloading.");
            System.out.println("we have product at this moment "+product.getCount()+" show available one time"+ product.getAddItemsNumber());
            if(product.getCount() <= product.getAddItemsNumber()){
                System.out.println("size"+product.getProductConfigurables().size());
                createItemList(product.getProductConfigurables());}
            else {
                if(adapter1 == null){
                   createItemList(product.getProductConfigurables());
                    TextView error = (TextView)findViewById(R.id.error_message);
                    error.setText("We have problem with connection,try again please");
                }
                System.out.println("ok enter one");
                adapter1.addAllProduct(product.getProductConfigurables());
            }
            pDialog.dismiss();

        }
    }



    private void createItemList(List<ProductConfigurable> productConfigurables){
        ListView list = (ListView)findViewById(R.id.top_seller_list);
        adapter1 = new MyProductAdapter(this, productConfigurables,R.layout.topseller_list);
        // moreView =getLayoutInflater().inflate(R.layout.listview_footer, null);
        list.setAdapter(adapter1);
        list.setOnItemClickListener(new productDetailClickListener());
        list.setOnScrollListener(this);

    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (isPageDiv && scrollState ==
                AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {

            if(product.getCount()<product.getAllListItemsSize()){
                new DownLoad().execute();}

        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem,
                         int visibleItemCount, int totalItemCount) {
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
        List<ProductConfigurable> p  = adapter1.getList();
        bundle.putSerializable("key", p.get(position));
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
        ImageButton imageButton = (ImageButton)mCustomView.findViewById(R.id.shopCartButton);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SpecialSection.this,CheckOutList.class);
                startActivity(intent);
            }
        });
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


    public void Cart(){
        Intent intent = new Intent(this,CheckOutList.class);
        startActivity(intent);
    }
}
