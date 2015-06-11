package myaplication.tfg.org.myapplication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.media.Image;
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
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import org.ksoap2.serialization.SoapObject;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.List;

import myaplication.tfg.org.Adapters.MyProductAdapter;
import myaplication.tfg.org.ApiMethod.Category;
import myaplication.tfg.org.models.ProductConfigurable;


public class CategorySection extends ActionBarActivity implements AbsListView.OnScrollListener{
    private String sectionTitle;
    private TextView Title;
    private int categoryId;
    private Category categoryMethodApi;
    private MyProductAdapter adapter1;
    private boolean isPageDiv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_section);
        categoryMethodApi = new Category();
        getCustomizedActionBar();
        Intent intent = getIntent();
        sectionTitle = intent.getStringExtra("section");
        categoryId =  intent.getIntExtra("categoryId", 0);
        System.out.println("section: "+sectionTitle+"  categoryId:  "+Integer.toString(categoryId));
        Title = (TextView)findViewById(R.id.sectionTitle);
        Title.setText(sectionTitle);
        new DownloadCategoryProducts().execute();
    }



    private class DownloadCategoryProducts extends AsyncTask<String,Float,String>{
        ProgressDialog pDialog;
        SoapObject r;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.d("Hi", "Download Commencing");
            pDialog = new ProgressDialog(CategorySection.this);
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
                if(categoryMethodApi.getSessionId().equals("")){
                    categoryMethodApi.setupSessionLogin();
                }
                if(categoryMethodApi.getAllListSize()==0){
                categoryMethodApi.categoryAllAsignedProducts(categoryId);
                }
                categoryMethodApi.createProduct();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            System.out.println("count " + categoryMethodApi.getCount() + "productConfigurable list size " + categoryMethodApi.getListProductConfigurables().size());
                if (categoryMethodApi.getCount() <= categoryMethodApi.getAddItemsNumber()) {
                    createItemList(categoryMethodApi.getListProductConfigurables());
                } else {
                    if (adapter1 == null) {
                        createItemList(categoryMethodApi.getListProductConfigurables());
                        TextView error = (TextView) findViewById(R.id.error_message);
                        error.setText("We have problem with connection,try again please");
                    }

                    adapter1.addAllProduct(categoryMethodApi.getListProductConfigurables());
                }
            pDialog.dismiss();
            }


    }


    private void createItemList(List<ProductConfigurable> productConfigurables){
        ListView list = (ListView)findViewById(R.id.category_section_result);
        adapter1 = new MyProductAdapter(this, productConfigurables,R.layout.topseller_list);
        // moreView =getLayoutInflater().inflate(R.layout.listview_footer, null);
        list.setAdapter(adapter1);
        list.setOnItemClickListener(new productDetailClickListener());
        list.setOnScrollListener(this);
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (isPageDiv && scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
            if(categoryMethodApi.getCount()<categoryMethodApi.getAllListSize()){

                new DownloadCategoryProducts().execute();}

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
        List<ProductConfigurable> p  = adapter1.getList();
        bundle.putSerializable("key", p.get(position));
        Intent intent = new Intent(this,IndividualItemInfo.class);
        intent.putExtra("bundle",bundle);
        startActivity(intent);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_fisrt_page, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setQueryHint("type a Key Word");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                Intent intent = new Intent(CategorySection.this, SearchInfo.class);
                intent.putExtra("query", s);
                startActivity(intent);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        }
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
        TextView listNumber = (TextView)mCustomView.findViewById(R.id.number);
        TextView title = (TextView)mCustomView.findViewById(R.id.logoTitle);
        title.setVisibility(View.GONE);
        int number = DataHolder.getNumber();
        listNumber.setText(Integer.toString(number));
        listNumber.setVisibility(View.GONE);
        ImageButton imageButton = (ImageButton)mCustomView.findViewById(R.id.shopCartButton);
        imageButton.setVisibility(View.GONE);
        actionBar.setCustomView(mCustomView);
        actionBar.setDisplayShowCustomEnabled(true);
    }
}
