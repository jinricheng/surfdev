package myaplication.tfg.org.myapplication;

import android.app.ActionBar;
import android.graphics.Color;
import android.provider.MediaStore;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.ActionBarDrawerToggle;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


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
    private ListView slideMenu;
    private List<HashMap<String,String>> list2;
    private SimpleAdapter adapter1;
    private DrawerLayout drawerLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initSlideMenu();
        initCusmizedActionBar();
        getIndividualItemInfo();

        bottomMenu();
      //  setQuantityButton();
    }

    private void initSlideMenu() {
    }

    private class slideMenuItemsClickListener implements ListView.OnItemClickListener{
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

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
                if (popQuantity != null && popQuantity.isShowing()) {
                    popQuantity.dismiss();
                } else {
                    setUpPopWindow(v, 2);
                }
            }
        });
    }

    private void setUpPopWindow(View v, int i) {
        initPopUpWindowsSize(v,i);
        int[] location = new int[2];
        int[] location2 = new int[2];
        Log.d("position ",Integer.toString(i));

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
                        if(popQuantity!=null && popQuantity.isShowing()){
                            popQuantity.dismiss();
                            popQuantity =null;
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
        quantity.setText("1");
        listen();

    }

    private void listen() {
        plusbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int number = Integer.valueOf(quantity.getText().toString());
                number = number+1;
                if(number>9){
                    quantity.setText("10");
                    plusbutton.setEnabled(false);
                }
                else{
                    quantity.setText(Integer.toString(number));
                    plusbutton.setEnabled(true);
                    minusbutton.setEnabled(true);
                }
            }
        });

        minusbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int number = Integer.valueOf(quantity.getText().toString());
                number = number-1;
                if(number<2){
                    quantity.setText("1");
                    minusbutton.setEnabled(false);
                }
                else{
                    quantity.setText(Integer.toString(number));
                    minusbutton.setEnabled(true);
                    plusbutton.setEnabled(true);
                }
            }
        });

    }

    private void getItemSize(View v) {
        String[] spinnerinfo = new String[]{"XS","S","M","L","XL"};
        final RadioGroup radioGroup = (RadioGroup)v.findViewById(R.id.size_group);
        float density = getResources().getDisplayMetrics().density;
        int margin =15;
        int id = 0;
        RadioGroup.LayoutParams params = new RadioGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(margin,margin,margin,margin);
        for(int i =0; i<spinnerinfo.length;i++){
          RadioButton tempButton = new RadioButton(this);
          tempButton.setText(spinnerinfo[i]);
          tempButton.setTextSize(18);
          tempButton.setId(i);
          tempButton.setTag(i);
          radioGroup.addView(tempButton,params);
        }

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                    RadioButton radioButton = (RadioButton)group.getChildAt(checkedId);
                    Log.d("Radio", radioButton.getText().toString());
            }
        });

    }

    private void getIndividualItemInfo() {
        setContentView(R.layout.activity_individual_item_info);
        Bundle bundle = getIntent().getBundleExtra("bundle");
        ProductConfigurable p =(ProductConfigurable)bundle.getSerializable("key");
        TextView title = (TextView)findViewById(R.id.detail_title);
        TextView price = (TextView)findViewById(R.id.detail_price);
        TextView description = (TextView)findViewById(R.id.detail_description);
        TextView stock =(TextView)findViewById(R.id.stock);
        ImageView image = (ImageView)findViewById(R.id.detail_image);
        String fullprice = "Price: "+p.getPrice();
        title.setText(p.getTitle());
       if(p.getTitle().equals("VOLCOM SUPERNATURAL INS JACKET ELECTRIC GREEN - 2015")){
            stock.setText("In Stock");
            stock.setTextColor(Color.parseColor("#31B404"));
        }
        else{
            stock.setText("Out Of Stock");
            stock.setTextColor(Color.parseColor("#DF0101"));
        }
        UrlImageViewHelper.setUrlDrawable(image, p.getImage());
        description.setText(p.getDescription());
        price.setText(fullprice);
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
        imageButton = (ImageButton)findViewById(R.id.shopCartButton);
        actionBar.setCustomView(mCustomView);
        actionBar.setDisplayShowCustomEnabled(true);
    }

}
