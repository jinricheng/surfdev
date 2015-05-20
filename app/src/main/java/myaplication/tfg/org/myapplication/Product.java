package myaplication.tfg.org.myapplication;

import java.io.Serializable;
import java.util.List;

/**
 * Created by jin on 2015/4/30.
 */
public abstract class Product implements Serializable{
    private String product_id;
    private String image;
    private String title;
    private String sku;
    private String price;
    private String description;
    private String size;
    private String quantity;

    public Product(){}
    public Product(String product_id ,String image, String title, String price, String description, String size){
        this.product_id = product_id;
        this.image = image;
        this.title=title;
        this.price = price;
        this.size=size;
    }

    public void setProduct_id(String product_id){
        this.product_id=product_id;
    }
    public void setImage(String imageUrl){
        this.image = imageUrl;
    }

    public void setTitle(String title){
        this.title =  title;
    }

    public void setDescription(String description){
        this.description=  description;
    }

    public void setSize(String size){this.size =  size; }

    public void setPrice(String price){
        this.price =  price;
    }
    public String getImage(){
        return this.image;
    }

    public String getDescription(){
        return this.description;
    }

    public String getPrice(){
        return this.price;
    }

    public String getTitle(){
        return this.title;
    }

    public String getProduct_id(){return this.product_id;}

    public void setSku(String sku){
        this.sku=sku;
    }
    public String getSku(){
        return this.sku;
    }

    public String getSize(){
        return this.size;
    }
    public String toString () { return "Product: "+this.getTitle()+" "+this.getPrice()+" " +this.getDescription();}

}
