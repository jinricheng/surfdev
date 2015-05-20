package myaplication.tfg.org.myapplication;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by jin on 2015/4/15.
 */
public class ProductConfigurable  extends Product implements Serializable{
   private String type;
    private HashMap<String,ProductSimple> productsSimple;
    private List<String> simpleProductId;
    private String section;

    public ProductConfigurable(){
        productsSimple = new HashMap<String,ProductSimple>();
        simpleProductId = new ArrayList<String>();
    }
    public ProductConfigurable(String product_id,String image, String title, String price, String description, String size){
        super(product_id,image,title,price,description,size);
    }

    public void setType(String type){
        this.type = type;
    }
    public String getType(String type){
        return this.type;
    }

    public void addNewSimpleProduct(ProductSimple p){
        productsSimple.put(p.getProduct_id(),p);
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
     public ProductSimple getSimpleProduct(String product_id){
         return productsSimple.get(product_id);
     }

    public void setListProductSimple(HashMap<String,ProductSimple> simple_product){
        this.productsSimple=simple_product;
    }
    public HashMap<String,ProductSimple> getListProductSimple(){
        return this.productsSimple;
    }

    public void setSection(String section){
        this.section = section;
    }

    public String getSection(){
        return this.section;
    }
}
