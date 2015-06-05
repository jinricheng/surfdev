package myaplication.tfg.org.Adapters;

/**
 * Created by jin on 2015/5/22.
 */

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;

import java.text.DecimalFormat;
import java.util.List;

import myaplication.tfg.org.models.ProductSimple;
import myaplication.tfg.org.myapplication.R;

/**
 * Created by jin on 2015/4/15.
 */
public class CheckOutItemsAdapter extends BaseAdapter {
    Context context;
    private List<ProductSimple> productSimples;
    private int resource;

    @SuppressWarnings("static-access")
    public CheckOutItemsAdapter(Context context,List<ProductSimple> productSimples, int resource) {
        this.productSimples = productSimples;
        this.resource = resource;
        this.context=context;

    }

    static class ViewHolder{
        TextView title;
        TextView price;
        ImageView image;
        ImageView icon;
        ImageView especial;
        TextView quantity;
    }
    @Override
    public int getCount() {
        return productSimples.size();
    }

    @Override
    public Object getItem(int position) {
        return productSimples.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    public void addAllProduct(List<ProductSimple> p){
        for(int i=0;i<p.size();i++){
            this.productSimples.add(p.get(i));}
        notifyDataSetChanged();
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = new ViewHolder();
        if(convertView==null){
            convertView=View.inflate(context, resource, null);
            viewHolder.title = (TextView)convertView.findViewById(R.id.check_out_title);
            viewHolder.image =(ImageView)convertView.findViewById(R.id.check_out_images);
            viewHolder.quantity= (TextView)convertView.findViewById(R.id.check_out_quantity);
            viewHolder.price= (TextView)convertView.findViewById(R.id.check_out_price);
            convertView.setTag(viewHolder);
        }
        else{
            viewHolder = (ViewHolder) convertView.getTag();
        }
        ProductSimple p = productSimples.get(position);

            viewHolder.title.setText(p.getTitle());
            UrlImageViewHelper.setUrlDrawable(viewHolder.image, p.getImage());
            // image.setImageResource(R.drawable.jacket);
        //    viewHolder.icon.setImageResource(R.drawable.ic_action_next_item_dark);
            String quantityNumber = "Quantity: "+p.getItemNumber();
            String priceIndividual = p.getPrice();
            priceIndividual = priceIndividual.replaceAll(",",".");
            Double finalPrice = Double.parseDouble(priceIndividual)*p.getItemNumber();
            DecimalFormat formatter = new DecimalFormat("#,##0.00");
            viewHolder.price.setText(formatter.format(finalPrice)+"\u20AC");
            viewHolder.quantity.setText(quantityNumber);


        return convertView;
    }
}
