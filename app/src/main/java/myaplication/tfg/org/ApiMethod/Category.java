package myaplication.tfg.org.ApiMethod;

import android.util.Log;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import myaplication.tfg.org.models.ProductConfigurable;
import myaplication.tfg.org.models.ProductSimple;
import myaplication.tfg.org.myapplication.DataHolder;

/**
 * Created by jin on 2015/6/10.
 */
public class Category {
    private SoapSerializationEnvelope env;
    private HttpTransportSE androidHttpTransport;
    private SoapObject request;
    private SoapObject r;
    private String sessionId;
    private int addItemsNumber = 4;
    private int count =0;
    private List<SoapObject> asignedProducts;
    private List<ProductConfigurable> listProductConfigurables;
    String NAMESPACE = "urn:Magento";
    String URL = "http://gonegocio.es/index.php/api/v2_soap/";
    private HashMap<String,HashMap<String,Integer>> navigationList;

    public Category() {
        navigationList = new HashMap<>();
        sessionId = "";
        asignedProducts = new ArrayList<>();
    }

    public void setupSessionLogin() throws IOException, XmlPullParserException {
        env = new SoapSerializationEnvelope(
                SoapEnvelope.VER11);
        env.dotNet = false;
        env.xsd = SoapSerializationEnvelope.XSD;
        env.enc = SoapSerializationEnvelope.ENC;
        request = new SoapObject(NAMESPACE, "login");
        request.addProperty("username", "jin");
        request.addProperty("apiKey", "1234567890");
        env.setOutputSoapObject(request);
        androidHttpTransport = new HttpTransportSE(URL);
        androidHttpTransport.call("", env);
        Object result = env.getResponse();
        sessionId = (String) result;
    }

    public HashMap<String,HashMap<String,Integer>> getAllCategoryIds() throws IOException, XmlPullParserException {
        request = new SoapObject(NAMESPACE, "catalogCategoryTree");
        request.addProperty("sessionId", sessionId);
        env.setOutputSoapObject(request);
        androidHttpTransport.call("", env);
        r = (SoapObject) env.getResponse();
        SoapObject result = rearchFisrtLevel(r);
        navigationList = new HashMap<String,HashMap<String,Integer>>();
        HashMap<String,Integer> secondLevel = new HashMap<>();
        for(int i =0;i<result.getPropertyCount();i++){
            SoapObject child=(SoapObject)result.getProperty(i);
            String name = child.getProperty("name").toString();
            secondLevel = getSecondLevelInfo(child);
            navigationList.put(name,secondLevel);
        }
        return navigationList;
    }

    private HashMap<String, Integer> getSecondLevelInfo(SoapObject child) {
        SoapObject levelTwo = (SoapObject) child.getProperty("children");
        HashMap<String,Integer> r = new HashMap<>();
        for(int i =0;i<levelTwo.getPropertyCount();i++) {
            SoapObject result = (SoapObject)levelTwo.getProperty(i);
            r.put(result.getProperty("name").toString(), (Integer)result.getProperty("category_id"));
        }
        return r;
    }


    public void categoryAllAsignedProducts(int categoryId) throws IOException, XmlPullParserException {

        request = new SoapObject(NAMESPACE, "catalogCategoryAssignedProducts");
        request.addProperty("sessionId", sessionId);
        request.addProperty("categoryId", String.valueOf(categoryId));
        env.setOutputSoapObject(request);
        androidHttpTransport.call("", env);
        r = (SoapObject) env.getResponse();
        for(int i=0;i<r.getPropertyCount();i++ ){
            SoapObject child = (SoapObject)r.getProperty(i);
            String type = (String)child.getProperty("type");
            if(type.equals("configurable")){
                asignedProducts.add(child);
            }
        }

    }

    public void createProduct() throws IOException, XmlPullParserException {

        listProductConfigurables = new ArrayList();
        int tempCount = count+addItemsNumber;
        while(count<tempCount && count<asignedProducts.size()) {
            List<String> simpleId = new ArrayList<>();
            SoapObject child = asignedProducts.get(count);
            ProductConfigurable p = createConfigurableProduct(child);
            simpleId = relatedProducts((Integer)child.getProperty("product_id"));
            p.addSimpleProductListId(simpleId);
            listProductConfigurables.add(p);
            count++;
        }
        getIndividualProductInfo();

    }

    private void getIndividualProductInfo() throws IOException, XmlPullParserException {
        ProductConfigurable p = new ProductConfigurable();
        for (int i =0;i< listProductConfigurables.size();i++) {
            p = listProductConfigurables.get(i);
            if(!DataHolder.getListProductConfigurable().containsKey(p.getProduct_id())) {
                getMoreProductInfoFromServer(i, p);
            }
            else{
                ProductConfigurable item = DataHolder.getListProductConfigurable().get(p.getProduct_id());
                item.setSection("no");
                listProductConfigurables.set(i, item);
            }
        }
    }

    private void getMoreProductInfoFromServer(int position,ProductConfigurable p) throws IOException, XmlPullParserException {
        request = new SoapObject(NAMESPACE, "catalogProductInfo");
        request.addProperty("sessionId", sessionId);
        request.addProperty("productId", p.getProduct_id());
        env.setOutputSoapObject(request);
        androidHttpTransport.call("", env);
        r = (SoapObject) env.getResponse();
        Double price = Double.parseDouble((String) r.getProperty("price"));
        String pr = String.format("%.2f", price);
        p.setTitle((String)r.getProperty("name"));
        p.setPrice(pr);
        p.setDescription((String) r.getProperty("description"));
        p.setSection("no");
        listProductConfigurables.set(position, p);
        DataHolder.getListProductConfigurable().put(p.getProduct_id(), p);
    }



    private List<String> relatedProducts(int productId) throws IOException, XmlPullParserException {
        List<String> id = new ArrayList<>();
        request = new SoapObject(NAMESPACE, "catalogProductLinkList");
        request.addProperty("sessionId", sessionId);
        request.addProperty("type", "up_sell");
        request.addProperty("product", String.valueOf(productId));
        env.setOutputSoapObject(request);
        androidHttpTransport.call("", env);
        r = (SoapObject) env.getResponse();
        for(int i =0;i<r.getPropertyCount();i++){
            SoapObject child =(SoapObject) r.getProperty(i);
            id.add(child.getProperty("product_id").toString());
        }
        return id;
    }

    private SoapObject rearchFisrtLevel(SoapObject r) {
        SoapObject result;
        result = (SoapObject)r.getProperty("children");
        result = (SoapObject)result.getProperty("item");
        result = (SoapObject)result.getProperty("children");
        return result;
    }


    private ProductConfigurable createConfigurableProduct(SoapObject r) throws IOException, XmlPullParserException {
        ProductConfigurable p = new ProductConfigurable();
        p.setProduct_id(String.valueOf((Integer)r.getProperty("product_id")));
        p.setSku((String) r.getProperty("sku"));
        String imageUrl = "http://gonegocio.es/media/catalog/product/android/" + p.getSku()+".jpg";
        p.setImage(imageUrl);

        return p;}

    public HashMap<String,HashMap<String,Integer>> getNavigationList(){
        return this.navigationList;
    }

    public List<ProductConfigurable> getListProductConfigurables(){
        return this.listProductConfigurables;
    }

    public int getCount(){
        return this.count;
    }

    public int getAddItemsNumber(){
        return this.addItemsNumber;
    }

    public int getAllListSize(){
        return this.asignedProducts.size();
    }

    public String getSessionId(){
        return this.sessionId;
    }
}
