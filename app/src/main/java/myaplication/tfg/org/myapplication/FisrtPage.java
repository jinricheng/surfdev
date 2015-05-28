package myaplication.tfg.org.myapplication;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ExpandableListActivity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.view.View.OnClickListener;
import android.content.Intent;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class FisrtPage extends ActionBarActivity {
    private String[] drawerListViewItems;
    private List<HashMap<String,String>> list;
    private List<HashMap<String,String>> list2;
     private ListView listView;
    private SimpleAdapter listAdapter;
    private DrawerLayout drawerlayout;
    private ListView navigationList;
    private Intent intent;
    private ImageButton imageButton;
    private SimpleAdapter adapter1;
    private ListAdapter adapter2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fisrt_page);
        initMainListView();
        initCusmizedActionBar();
        initNavigationList();

    }

    private void initNavigationList() {
        navigationList = (ListView)findViewById(R.id.left_drawer);
        String[] content = getResources().getStringArray(R.array.navigationFirstLevel);
        list2 = new ArrayList<HashMap<String, String>>();
        for(int i=0;i<content.length;i++){
            HashMap<String,String> map1 = new HashMap<String,String>();
            map1.put("nom",content[i]);
            map1.put("images",Integer.toString(R.drawable.ic_action_next_item));

            list2.add(map1);
        }
        adapter1 = new SimpleAdapter(this, list2,R.layout.navigation_list_items, new String[]{"nom","images"},new int[]{R.id.text1,R.id.image2});
        navigationList.setAdapter(adapter1);
        drawerlayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerlayout, R.string.drawer_open, R.string.drawer_close);
        actionBarDrawerToggle.syncState();
        drawerlayout.setDrawerListener(actionBarDrawerToggle);
        navigationList.setOnItemClickListener(new firstLevelItemsClickListener());

    }


 /*listener of first level of navigation menu*/
    private class firstLevelItemsClickListener implements ListView.OnItemClickListener{
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            loadContent(position);
        }
    }

    private void loadContent(int position){
         String[] content;
        switch(position){
            case 0:
                content = getResources().getStringArray(R.array.snowboard);
                setAdapter(content);
                break;
            case 1:
                content = getResources().getStringArray(R.array.snowWear);
                setAdapter(content);
                break;
            case 2:
                content = getResources().getStringArray(R.array.fleece);
                setAdapter(content);
                break;
            case 3:
                content = getResources().getStringArray(R.array.boots);
                setAdapter(content);
                break;
            case 4:
                content = getResources().getStringArray(R.array.goggles);
                setAdapter(content);
                break;
            case 5:
                content = getResources().getStringArray(R.array.protection);
                setAdapter(content);
                break;
            case 6:
                content = getResources().getStringArray(R.array.StreetWear);
                setAdapter(content);
                break;
            case 7:
                content = getResources().getStringArray(R.array.bindings);
                setAdapter(content);
                break;
            case 8:
                content = getResources().getStringArray(R.array.bags);
                setAdapter(content);
                break;
            case 9:
                content = getResources().getStringArray(R.array.Accessories);
                setAdapter(content);
                break;
            default:
                navigationList.setOnItemClickListener(new firstLevelItemsClickListener());
        }

    }



    private class secondLevelMenuItemsClickListener implements ListView.OnItemClickListener{

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            loadContent2(position);
        }
    }
    private void loadContent2(int position){
        String[] content;
    }

    private class listPrincipalItemClickListener implements ListView.OnItemClickListener{

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            loadContent3(position);
        }
    }

    private void loadContent3(int position){
          switch (position){
              case 0:
                  Intent intent = new Intent(this,MasVendidos.class);
                  intent.putExtra("name","Offers");
                  startActivity(intent);
                  break;
              case 1:
                  Intent intent1 = new Intent(this,MasVendidos.class);
                  intent1.putExtra("name","News");
                  startActivity(intent1);
                  break;
              case 2:
                  Intent intent2 = new Intent(this,MasVendidos.class);
                  intent2.putExtra("name","Top_Sellers");
                  startActivity(intent2);
                  break;
              default:

          }

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
        TextView listNumber = (TextView)mCustomView.findViewById(R.id.number);
        int number = DataHolder.getNumber();
        listNumber.setText(Integer.toString(number));
        imageButton = (ImageButton)mCustomView.findViewById(R.id.shopCartButton);
        imageButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(FisrtPage.this,CheckOutList.class);
                startActivity(intent);
            }
        });
        actionBar.setCustomView(mCustomView);
        actionBar.setDisplayShowCustomEnabled(true);
    }

    private void initMainListView() {
        listView = (ListView)findViewById(R.id.list1);
        list =new ArrayList<HashMap<String,String>>();
        HashMap<String,String> map1=new HashMap<String,String>();
        HashMap<String,String> map2=new HashMap<String,String>();
        HashMap<String,String> map3=new HashMap<String,String>();
        map1.put("nom","Offer >>");
        map1.put("images",Integer.toString(R.drawable.oferta));
        map2.put("nom","News>>");
        map2.put("images",Integer.toString(R.drawable.novedades));
        map3.put("nom", "Top sellers>>");
        map3.put("images", Integer.toString(R.drawable.mas_vendidos));
        list.add(map1);
        list.add(map2);
        list.add(map3);
        listAdapter = new SimpleAdapter(this, list,R.layout.list_item, new String[]{"nom","images"},new int[]{R.id.rowTextView,R.id.images});
        listView.setAdapter(listAdapter);
        listView.setOnItemClickListener(new listPrincipalItemClickListener());
    }


    public void Cart(View view){
        Intent intent = new Intent(this,soapTest.class);
        startActivity(intent);
    }

    private void setAdapter(String[] content) {
        adapter2 = new ArrayAdapter<String>(this,R.layout.second_level_items,content);
        navigationList.setAdapter(adapter2);
        navigationList.setOnItemClickListener(new secondLevelMenuItemsClickListener());
    }
    /*
        @Override
        public boolean onCreateOptionsMenu(Menu menu) {
            // Inflate the menu; this adds items to the action bar if it is present.
            getMenuInflater().inflate(R.menu.menu_fisrt_page, menu);
    
    
            return true;
        }
    */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            if(  drawerlayout.isDrawerOpen(GravityCompat.START)
                    ){
                if(navigationList.getAdapter()!=adapter1){
                    navigationList.setAdapter(adapter1);
                    navigationList.setOnItemClickListener(new firstLevelItemsClickListener());
                }else{
                drawerlayout.closeDrawers();}
            }else{
                 drawerlayout.openDrawer(GravityCompat.START);
            }
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
