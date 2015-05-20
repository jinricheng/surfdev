package myaplication.tfg.org.myapplication;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jin on 2015/4/9.
 */
public class FragmentOferta extends Fragment {
    private List<ProductConfigurable> productConfigurables;
    private MyProductAdapter adapter1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.oferta_fragment, container, false);
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);
        new DownLoad().execute();
    }

    private class DownLoad extends AsyncTask<String,Float,String> {
        ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.d("Hi", "Download Commencing");

            pDialog = new ProgressDialog(FragmentOferta.this.getActivity());

            String message= "Esperando...";
            SpannableString ss2 =  new SpannableString(message);
            ss2.setSpan(new RelativeSizeSpan(2f), 0, ss2.length(), 0);
            ss2.setSpan(new ForegroundColorSpan(Color.BLACK), 0, ss2.length(), 0);
            pDialog.setMessage(ss2);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
          try {

                ListView oferta = (ListView)getActivity().findViewById(R.id.oferta_list);
                productConfigurables = new ArrayList<ProductConfigurable>();
                String[] name = new String[]{"VOLCOM SUPERNATURAL INS JACKET ELECTRIC GREEN - 2015", "BURTON WOMAN SOCIETY PANT BLUE-RAY NOVEAU NEON - 2014","BURTON WB PELE MITT KAMANA WANNA LEI YA - 2015","SALOMON ASSASSIN 155 - 2015"};
                String[] price = new String[]{"256.5€", "120.95€","34.97€","84.00€"};
                String[] description = new String[]{"Great Jacket", "Comfortable snowPants","LowPrice, good quality gloves","New model of SnowBoard with incredible price"};
                int[] images = new int[]{R.drawable.jacket,R.drawable.pant2,R.drawable.gloves,R.drawable.board};

                for(int i =0;i<name.length;i++) {
                    ProductConfigurable p = new ProductConfigurable();
                    p.setTitle(name[i]);
                 //   p.setImage(images[i]);
                    p.setPrice(price[i]);
                    p.setDescription(description[i]);
                    productConfigurables.add(p);
                }
                adapter1 = new MyProductAdapter(FragmentOferta.this.getActivity(), productConfigurables,R.layout.oferta_list);
                oferta.setAdapter(adapter1);
                oferta.setOnItemClickListener(new productDetailClickListener());

            } catch (Exception e) {
                e.printStackTrace();
            }
            return "Executed!";

        }


        protected void onProgressUpdate (Float... valores) {
            int p = Math.round(100*valores[0]);
            pDialog.setProgress(p);
        }
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Log.d("Hi", "Done Downloading.");
            pDialog.dismiss();

        }
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
        Log.d("Product info",productConfigurables.get(position).toString());
        Intent intent = new Intent(getActivity(),IndividualItemInfo.class);
        intent.putExtra("bundle",bundle);
        startActivity(intent);
    }
}