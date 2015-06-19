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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import org.ksoap2.serialization.SoapObject;

import java.util.List;

import myaplication.tfg.org.Adapters.MyProductAdapter;
import myaplication.tfg.org.ApiMethod.Product;
import myaplication.tfg.org.models.ProductConfigurable;


public class SearchInfo extends ActionBarActivity implements AbsListView.OnScrollListener {
    private String result;
    private Product productApiMethod;
    private boolean isPageDiv;
    private MyProductAdapter adapter1;
    private ListView list;
    TextView text;
    private boolean found;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_info);
        Intent intent = getIntent();
        result = intent.getStringExtra("query");
        text = (TextView)findViewById(R.id.searchResult);
        String title = "Search Result: "+result;
        text.setText(title);
        productApiMethod = new Product();
        list = (ListView)findViewById(R.id.search_result_list);

        new search().execute();
    }

    private class search extends AsyncTask<String,Float,String>{
        ProgressDialog pDialog;
        SoapObject r;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.d("Hi", "Download Commencing");
            pDialog = new ProgressDialog(SearchInfo.this);
            String message = "Searching...";
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

                if(productApiMethod.getSessionId().equals("")){
                    productApiMethod.setupSessionLogin();}
                if(productApiMethod.getAllListItemsSize()==0){
                    found =productApiMethod.searchProductByName(result);
                }
                    productApiMethod.createPartOfItems();
                Log.d("Hi", "Done Downloading.");
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);


            if(found == false){
                text.setVisibility(View.GONE);
                list.setVisibility(View.GONE);
                TextView notFount = (TextView)findViewById(R.id.notFound);
                notFount.setText("Sorry,We can not found anything");
            }
            else {
                if (productApiMethod.getCount() <= productApiMethod.getAddItemsNumber()) {
                    createItemList(productApiMethod.getProductConfigurables());
                } else {
                    if (adapter1 == null) {
                        createItemList(productApiMethod.getProductConfigurables());
                        TextView error = (TextView) findViewById(R.id.error_message);
                        error.setText("We have problem with connection,try again please");
                    }

                    adapter1.addAllProduct(productApiMethod.getProductConfigurables());
                }
            }
            pDialog.dismiss();

        }
    }

    private void createItemList(List<ProductConfigurable> productConfigurables){

        adapter1 = new MyProductAdapter(this, productConfigurables,R.layout.topseller_list);
        list.setAdapter(adapter1);
        list.setOnItemClickListener(new productDetailClickListener());
        list.setOnScrollListener(this);

    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (isPageDiv && scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
            if(productApiMethod.getCount()<productApiMethod.getAllListItemsSize()){
                new search().execute();}

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

        private void loadProductDetail(int position){
            Bundle bundle = new Bundle();
            List<ProductConfigurable> p  = adapter1.getList();
            bundle.putSerializable("key", p.get(position));
            Intent intent = new Intent(SearchInfo.this,IndividualItemInfo.class);
            intent.putExtra("bundle",bundle);
            startActivity(intent);
        }
    }






    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search_info, menu);
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
