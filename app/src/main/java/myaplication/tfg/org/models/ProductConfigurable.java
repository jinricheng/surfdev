package myaplication.tfg.org.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by jin on 2015/4/15.
 */
public class ProductConfigurable  implements Serializable{
   private String type;

    private List<String> simpleProductId;
    private String section;
    private String product_id;
    private String image;
    private String title;
    private String sku;
    private String price;
    private String description;
    private String size;
    private String specialPrice="0,00";
    public ProductConfigurable(){
        simpleProductId = new ArrayList<String>();
    }
    public ProductConfigurable(String product_id,String image, String title, String price, String description, String size){

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


    public void setType(String type){
        this.type = type;
    }
    public String getType(String type){
        return this.type;
    }



    public void addSimpleProductId(String ids){
        this.simpleProductId.add(ids);
    }
    public void addSimpleProductListId(List<String> ids){
        this.simpleProductId = ids;
    }

    public List<String> getSimpleProductId(){
        return simpleProductId;
    }

    public void setSection(String section){
        this.section = section;
    }

    public String getSection(){
        return this.section;
    }
    public boolean hasSpecialPrice(){
        if(!specialPrice.equals("0,00")){
            return true;
        }
        else{
            return false;
        }
    }
    public void setSpecialPrice(String specialPrice){
        this.specialPrice = specialPrice;
    }
    public String toString () { return "Product: "+this.getProduct_id()+" "+this.getPrice()+" " +this.getDescription();}
}
