package myaplication.tfg.org.Adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;

import java.util.List;

import myaplication.tfg.org.models.ProductConfigurable;
import myaplication.tfg.org.myapplication.R;

/**
 * Created by jin on 2015/4/15.
 */
public class MyProductAdapter extends BaseAdapter {
    Context context;
    private List<ProductConfigurable> productConfigurables;
    private int resource;

    @SuppressWarnings("static-access")
    public MyProductAdapter(Context context,List<ProductConfigurable> productConfigurables, int resource) {
        this.productConfigurables = productConfigurables;
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
        return productConfigurables.size();
    }

    @Override
    public Object getItem(int position) {
        return productConfigurables.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    public void addAllProduct(List<ProductConfigurable> p){
        for(int i = 0;i<p.size();i++){
            this.productConfigurables.add(p.get(i));
        }
        notifyDataSetChanged();
    }

    public List<ProductConfigurable> getList(){
        return this.productConfigurables;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = new ViewHolder();
        if(convertView==null){
            convertView=View.inflate(context, resource, null);
            viewHolder.title = (TextView)convertView.findViewById(R.id.title);
            viewHolder.price = (TextView)convertView.findViewById(R.id.Price);
            viewHolder.icon =(ImageView)convertView.findViewById(R.id.next3);
            viewHolder.image =(ImageView)convertView.findViewById(R.id.thumbnail);
            viewHolder.especial=(ImageView)convertView.findViewById(R.id.special_icon);
            viewHolder.quantity=(TextView)convertView.findViewById(R.id.quantityNumber);
            convertView.setTag(viewHolder);
        }
        else{
            viewHolder = (ViewHolder) convertView.getTag();
        }


        ProductConfigurable p = productConfigurables.get(position);

        if(p != null){
            if(p.getSection()!=null){
                if(p.getSection().equals("Offers")) {
                viewHolder.especial.setImageResource(R.drawable.oferta_icon);
                }
                else if(p.getSection().equals("News")){
                    viewHolder.especial.setImageResource(R.drawable.new_icon);
                 }
                else{
                    viewHolder.especial.setImageResource(R.drawable.hot_icon);
                    viewHolder.quantity.setText("Sold:"+"3");
                }
            }

        viewHolder.title.setText(p.getTitle());
        UrlImageViewHelper.setUrlDrawable(viewHolder.image, p.getImage());
       // image.setImageResource(R.drawable.jacket);
        viewHolder.icon.setImageResource(R.drawable.ic_action_next_item_dark);
        viewHolder.price.setText(p.getPrice()+"\u20AC");
        }

        return convertView;
    }
}
