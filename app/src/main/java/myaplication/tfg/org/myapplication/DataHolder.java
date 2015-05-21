package myaplication.tfg.org.myapplication;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jin on 2015/5/21.
 */
public class DataHolder {
    private static int number = 0;
    private static List<ProductSimple> productSimples = new ArrayList<>();

    public static void setNumber(int num){
        number = num;
    }

    public static int getNumber(){
        return number;
    }

    public static List<ProductSimple>  getListProducts(){
        return productSimples;
    }
    public static void setProductSimples(List<ProductSimple> list){
        productSimples = list;
    }
    public static void addProductSimple(ProductSimple p){
        productSimples.add(p);
    }
}
