package myaplication.tfg.org.models;

import java.io.Serializable;

/**
 * Created by jin on 2015/4/30.
 */
public class ProductSimple implements Serializable{

    private String type;
    private int quantity;
    private int itemNumber;
    private String product_id;
    private String title;
    private String sku;
    private String price;
    private String description;
    private String size;
    private String image;
    public ProductSimple(){}

    public ProductSimple(String product_id, String title, String price, String description, String size){

    }

    public void setType(String type){
        this.type = type;
    }
    public String getType(){
        return this.type;
    }
    public void setQuantity(int quantity){
        this.quantity = quantity;
    }
    public int getQuantity(){
        return this.quantity;
    }
    public void setItemNumber(int number){
        this.itemNumber = number;
    }
    public int getItemNumber(){
        return this.itemNumber;
    }

    public void setProduct_id(String product_id){
        this.product_id=product_id;
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

    public void setImage(String imageUrl){
        this.image = imageUrl;
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

    public String toString(){
        return "Product Name: "+this.getTitle()+" Sku:  "+this.getSku()+"  Type: "+this.getType()+" Price "+this.getPrice()+" Quantity "+this.getQuantity()+" Size: " +
                this.getSize();
    }

}
