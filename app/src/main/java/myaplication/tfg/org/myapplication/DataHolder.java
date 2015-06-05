package myaplication.tfg.org.myapplication;

import com.nineoldandroids.util.Property;

import java.util.HashMap;

import myaplication.tfg.org.models.Cart;
import myaplication.tfg.org.models.ProductConfigurable;
import myaplication.tfg.org.models.ProductSimple;

/**
 * Created by jin on 2015/5/21.
 */
public class DataHolder {
    private static int number = 0;
    private static HashMap<String,ProductSimple> listCartItems = new HashMap<>();
    private static HashMap<String,ProductConfigurable> listProductConfigurable = new HashMap<>();
    private static HashMap<String,ProductSimple> listProductSimple = new HashMap<>();
    private static int CartId = 0;
    private static Cart cart ;


    public static void setNumber(int num){
        number = num;
    }

    public static int getNumber(){
        return number;
    }

    /*add a new productSimple*/
    public static void addProductSimple(ProductSimple p){
        listProductSimple.put(p.getProduct_id(),p);
        listProductSimple.put(p.getProduct_id(),p);

    }
    public static void addProductConfigurable(ProductConfigurable productConfigurable){
        listProductConfigurable.put(productConfigurable.getProduct_id(),productConfigurable);
    }
    public static HashMap<String,ProductConfigurable> getListProductConfigurable(){
        return listProductConfigurable;
    }
    public static void setListProductConfigurable(HashMap<String,ProductConfigurable> list){
        listProductConfigurable = list;
    }


    public static HashMap<String,ProductSimple> getListProductSimple(){
        return listProductSimple;
    }
    public static ProductSimple getProductSimple(String productId){
        return listProductSimple.get(productId);
    }

    public static void setListProductSimple(HashMap<String,ProductSimple> list){
        listProductSimple = list;
    }


    public static int getSimpleProdcutQuantity(String productId){
       return  listProductSimple.get(productId).getQuantity();
    }

    public static void setSimpleProductQuantity(String productId,String totalQuantity){
        Double number = Double.parseDouble(totalQuantity);
        int t= number.intValue();
        ProductSimple p = listProductSimple.get(productId);
        p.setQuantity(t);
        listProductSimple.put(productId, p);
    }

    public static void updateBoughtSimpleProductQuantity(String productId,int quantity){
        ProductSimple p = listProductSimple.get(productId);
        int newTotalQuantity = p.getQuantity()-quantity;
        p.setQuantity(newTotalQuantity);
        int boughtQuantity = p.getItemNumber()+quantity;
        p.setItemNumber(boughtQuantity);
        listProductSimple.put(productId, p);
        listCartItems.put(productId,p);
    }
    public static void updateCanceledProductSimpleQuantity(String productId,int quantity){
        ProductSimple p = listProductSimple.get(productId);
        int totalQuanitity = p.getQuantity();
        totalQuanitity = totalQuanitity+quantity;
        p.setItemNumber(p.getItemNumber() - quantity);
        p.setQuantity(totalQuanitity);
        listProductSimple.put(p.getProduct_id(),p);
        listCartItems.remove(p.getProduct_id());
    }


    public static void addItemToCart(ProductSimple p){
        listCartItems.put(p.getProduct_id(), p);
    }
    public static HashMap<String,ProductSimple> getListCartItems(){
        return listCartItems;
    }
    public static void setListCartItems(HashMap<String,ProductSimple> CartItems){
        listCartItems = CartItems;
    }
    public static int getCartId(){
        return CartId;
    }

    public static void setCartId(int num){
        CartId = num;
    }

    public static void setCart(Cart c){
         cart = c;
    }

    public static Cart getCart(){
        return cart;
    }
}
