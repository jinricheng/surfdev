package myaplication.tfg.org.ApiMethod;

import android.util.Log;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import myaplication.tfg.org.models.ProductConfigurable;
import myaplication.tfg.org.models.ProductSimple;
import myaplication.tfg.org.myapplication.DataHolder;

/**
 * Created by jin on 2015/4/30.
 */
public  class Product implements Serializable{
    private SoapSerializationEnvelope env;
    private HttpTransportSE androidHttpTransport;
    private SoapObject request;
    private SoapObject r;
    private List<SoapObject> listAllItems;
    private String sessionId;

    private ProductSimple pp_simple;
    private List<ProductConfigurable> productConfigurables;

    String NAMESPACE = "urn:Magento";
    String URL = "http://gonegocio.es/index.php/api/v2_soap/";

    private int addItemsNumber = 4;
    private int count =0;
    private String section="";

    public Product() {
        initialVariable();
    }

    private void initialVariable(){
       productConfigurables = new ArrayList<>();
        sessionId = "";
        listAllItems = new ArrayList<>();
        pp_simple = new ProductSimple();
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
        sessionId = (String)result;
        System.out.println("My session ID:  " + sessionId);
    }

    public void getAllListItem() throws IOException, XmlPullParserException {
        request = new SoapObject(NAMESPACE, "catalogProductList");
        request.addProperty("sessionId", sessionId);
        env.setOutputSoapObject(request);
        androidHttpTransport.call("", env);
        r = (SoapObject) env.getResponse();
        Log.d("Products Gotten", r.toString());
        SoapObject child = (SoapObject) r.getProperty(0);
        String type = (String)child.getProperty("type");
       createAllListItems();
    }
    private void createAllListItems() throws IOException, XmlPullParserException {
        if(section.equals("News")){
            int i = r.getPropertyCount()-1;
            while(i>=0){
                SoapObject child =(SoapObject) r.getProperty(i);
                String type =(String) child.getProperty("type");
                if(type.equals("configurable")){
                listAllItems.add((SoapObject)r.getProperty(i));
                }
                i--;
            }
        }else {
            for (int i = 0; i < r.getPropertyCount(); i++) {
                SoapObject child = (SoapObject) r.getProperty(i);
                String type =(String) child.getProperty("type");
                    if(type.equals("configurable")){
                        listAllItems.add(child);
                    }
                }


        }
    }

    public void createPartOfItems() throws IOException, XmlPullParserException {
        List<String> simpleProductId = new ArrayList<>();
        productConfigurables = new ArrayList<>();
        int tempCount = count+addItemsNumber;
        while(count<tempCount && count<listAllItems.size()) {
            List<String> simpleId = new ArrayList<>();
            SoapObject child = listAllItems.get(count);
            ProductConfigurable p = createConfigurableProduct(child);
            simpleId = relatedProducts(Integer.valueOf(child.getProperty("product_id").toString()));
            p.addSimpleProductListId(simpleId);
            productConfigurables.add(p);
            count++;
        }
        getIndividualProductInfo();

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

    private void checkSection(String section) {

        int i = productConfigurables.size()-1;
        if(section.equals("News")){
            List<ProductConfigurable> newsProducts = new ArrayList<>();
            while(i>=0){
                newsProducts.add(productConfigurables.get(i));
                i--;
            }
            productConfigurables = newsProducts;
        }

    }

    private void getIndividualProductInfo() throws IOException, XmlPullParserException {
        String temp =new String();
        ProductConfigurable p = new ProductConfigurable();
        for (int i =0;i< productConfigurables.size();i++) {
            p = productConfigurables.get(i);
            if(!DataHolder.getListProductConfigurable().containsKey(p.getProduct_id())) {
                getMoreProductInfoFromServer(i,p);
            }
            else{
                ProductConfigurable item = DataHolder.getListProductConfigurable().get(p.getProduct_id());
                item.setSection(section);
                productConfigurables.set(i, item);
            }
        }
    }


    private void getMoreProductInfoFromServer(int position,ProductConfigurable p) throws IOException, XmlPullParserException {
        Double price;
        request = new SoapObject(NAMESPACE, "catalogProductInfo");
        request.addProperty("sessionId", sessionId);
        request.addProperty("productId", p.getProduct_id());
        env.setOutputSoapObject(request);
        androidHttpTransport.call("", env);
        r = (SoapObject) env.getResponse();

        p.setTitle((String) r.getProperty("name"));
        p.setDescription((String) r.getProperty("description"));
        p.setSection(section);
        if(r.hasProperty("special_price") && p.getSection().equals("Offers")){
            p.setSpecialPrice((String)r.getProperty("special_price"));
            price = Double.parseDouble((String) r.getProperty("special_price"));
            System.out.println("ok specialprice "+r.toString());
        }
        else{
            price = Double.parseDouble((String) r.getProperty("price"));
            System.out.println("no special price"+r.toString());
        }
        String pr = String.format("%.2f", price);
        p.setPrice(pr);
        productConfigurables.set(position,p);
        DataHolder.getListProductConfigurable().put(p.getProduct_id(), p);

    }

    private ProductConfigurable createConfigurableProduct(SoapObject r) throws IOException, XmlPullParserException {
        ProductConfigurable p = new ProductConfigurable();
        p.setProduct_id((String) r.getProperty("product_id"));
        p.setSku((String) r.getProperty("sku"));
        String imageUrl = "http://gonegocio.es/media/catalog/product/android/" + p.getSku()+".jpg";
        p.setImage(imageUrl);
        return p;
    }


    public boolean searchProductByName(String result) throws IOException, XmlPullParserException {
        boolean found = false;
        SoapObject s = new SoapObject(NAMESPACE, "associativeEntity");
        SoapObject array = new SoapObject(NAMESPACE,"complexFilterArray");
        SoapObject complexfilter = new SoapObject(NAMESPACE,"complexFilter");
        SoapObject filters = new SoapObject(NAMESPACE,"complex_filter");


        String query = "%"+result+"%";
        PropertyInfo p = new PropertyInfo();
        p.setName("key");
        p.setValue("like");
        p.setType(String.class);
        s.addProperty(p);

        p = new PropertyInfo();
        p.setName("value");
        p.setValue(query);
        p.setType(String.class);
        s.addProperty(p);

        complexfilter.addProperty("key", "name");
        complexfilter.addProperty("value", s);


        array.addProperty("complexFilter", complexfilter);

        filters.addProperty("complex_filter",array);

        request = new SoapObject(NAMESPACE, "catalogProductList");
        request.addProperty("sessionId", sessionId);
        request.addProperty("filters", filters);
        env.setOutputSoapObject(request);
        androidHttpTransport.call("", env);
        r = (SoapObject) env.getResponse();
        if(r.getPropertyCount()!=0){
        SoapObject child = (SoapObject) r.getProperty(0);
            System.out.println(r.toString());
        String type = (String)child.getProperty("type");
            createAllListItems();
            found = true;
            section="no";
            return found;
        }
        else{
            return found;
        }
    }

    public void setSessionId(String sessionId){
        this.sessionId = sessionId;
    }
    public String getSessionId(){
        return this.sessionId;
    }
    public int getCount(){return this.count; }
    public void setCount(int count){this.count = count; }
    public int getAddItemsNumber(){return this.addItemsNumber;}
    public void setAddItemsNumber(int addItemsNumber){this.addItemsNumber = addItemsNumber;}

    public List<ProductConfigurable> getProductConfigurables(){
        return this.productConfigurables;
    }
    public void setListConfigurable(List<ProductConfigurable> productConfigurables){this.productConfigurables = productConfigurables;}

    public void setSection(String section){
        this.section = section;
    }
    public String getSection(){return this.section;}

    public int getAllListItemsSize(){
        return this.listAllItems.size();
    }


}
