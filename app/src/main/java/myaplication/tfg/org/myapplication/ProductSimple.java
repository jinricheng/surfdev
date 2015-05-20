package myaplication.tfg.org.myapplication;

import java.io.Serializable;
import java.util.List;

/**
 * Created by jin on 2015/4/30.
 */
public class ProductSimple extends Product implements Serializable{

    private String type;
    public ProductSimple(){}

    public ProductSimple(String product_id,String image, String title, String price, String description, String size){
        super(product_id,image,title,price,description,size);
    }

    public void setType(String type){
        this.type = type;
    }
    public String getType(String type){
        return this.type;
    }
}
