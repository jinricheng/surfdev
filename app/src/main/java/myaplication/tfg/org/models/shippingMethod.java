package myaplication.tfg.org.models;

import java.text.DecimalFormat;

/**
 * Created by jin on 2015/6/2.
 */
public class shippingMethod {
    private String code;
    private String title;
    private String price;

    public shippingMethod(){

    }


    public shippingMethod(String code, String title, String price){
        this.code = code;
        this.title = title;
        this.price = price;
    }

    public void setCode(String code){
        this.code = code;
    }
    public void setTitle(String title){
        this.title = title;
    }
    public void setPrice(String price){
        this.price = price;
    }

    public String getCode(){
        return this.code;
    }
    public String getTitle(){
        return this.title;
    }

    public String getPrice(){
       /* DecimalFormat formatter = new DecimalFormat("#,###.00");
        String price = formatter.format(this.price);*/
        return this.price;
    }
}
