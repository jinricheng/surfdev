package myaplication.tfg.org.myapplication;

import java.io.Serializable;
import java.util.List;

/**
 * Created by jin on 2015/4/30.
 */
public class ProductSimple extends Product implements Serializable{

    private String type;
    private int quantity;
    private int itemNumber;

    public ProductSimple(){}

    public ProductSimple(String product_id,String image, String title, String price, String description, String size){
        super(product_id,image,title,price,description,size);
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


    public String toString(){
        return "Product Name: "+this.getTitle()+" Sku:  "+this.getSku()+"  Type: "+this.getType()+" Price "+this.getPrice()+" Quantity "+this.getQuantity()+" Size: " +
                this.getSize();
    }
}
