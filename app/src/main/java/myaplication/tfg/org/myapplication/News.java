package myaplication.tfg.org.myapplication;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;


public class News extends ActionBarActivity {

    private List<ProductConfigurable> productConfigurables;
    private MyProductAdapter adapter1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);
        createItemList();
    }

    private void createItemList(){
        ListView oferta = (ListView)findViewById(R.id.new_list);
        productConfigurables = new ArrayList<ProductConfigurable>();
        String[] name = new String[]{"VOLCOM SUPERNATURAL INS JACKET ELECTRIC GREEN - 2015", "BURTON WOMAN SOCIETY PANT BLUE-RAY NOVEAU NEON - 2014","BURTON WB PELE MITT KAMANA WANNA LEI YA - 2015","SALOMON ASSASSIN 155 - 2015"};
        String[] price = new String[]{"256.5€", "120.95€","34.97€","84.00€"};
        String[] description = new String[]{"Great Jacket", "Comfortable snowPants","LowPrice, good quality gloves","New model of SnowBoard with incredible price"};
        int[] images = new int[]{R.drawable.jacket,R.drawable.pant2,R.drawable.gloves,R.drawable.board};

        for(int i =0;i<name.length;i++) {
            ProductConfigurable p = new ProductConfigurable();
            p.setTitle(name[i]);
            //p.setImage(images[i]);
            p.setPrice(price[i]);
            p.setDescription(description[i]);
            productConfigurables.add(p);
        }
        adapter1 = new MyProductAdapter(this, productConfigurables,R.layout.new_list);
        oferta.setAdapter(adapter1);
        oferta.setOnItemClickListener(new productDetailClickListener());
    }


    private class productDetailClickListener implements ListView.OnItemClickListener{
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            loadProductDetail(position);
        }
    }

    private void loadProductDetail(int position){
        Bundle bundle = new Bundle();
        bundle.putSerializable("key", productConfigurables.get(position));
        Intent intent = new Intent(this,IndividualItemInfo.class);
        intent.putExtra("bundle",bundle);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        LayoutInflater mInflater = LayoutInflater.from(this);
        View mCustomView = mInflater.inflate(R.layout.customactionbar, null);
        ImageButton imageButton = (ImageButton) findViewById(R.id.shopCartButton);
        actionBar.setCustomView(mCustomView);
        actionBar.setDisplayShowCustomEnabled(true);
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
}
