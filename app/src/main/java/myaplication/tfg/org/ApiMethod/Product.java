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
    private List<ProductConfigurable> listConfigurable;
    private SoapSerializationEnvelope env;
    private HttpTransportSE androidHttpTransport;
    private SoapObject request;
    private SoapObject r;
    private List<SoapObject> listAllItems;
    private String sessionId;
    private ProductConfigurable p_configurable;
    private ProductSimple pp_simple;
    private List<ProductConfigurable> productConfigurables;
    String NAMESPACE = "urn:Magento";
    String URL = "http://gonegocio.es/index.php/api/v2_soap/";
    private int addItemsNumber = 5;
    private int count =0;
    private int allitems =0;
    private String section="";

    public Product() {
        initialVariable();
    }

    private void initialVariable(){
        listConfigurable = new ArrayList<>();
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
        if((type.equals("configurable"))){
            startWithConfigurable();
        }
        else{
            startWithSimple();
        }
    }
    private void startWithSimple() throws IOException, XmlPullParserException {
        if(section.equals("News")){
            int i = r.getPropertyCount()-1;
            while(i>=0){
                System.out.println(r.getProperty(i).toString());
                listAllItems.add((SoapObject)r.getProperty(i));
                i--;
            }
        }else {
            for (int i = 0; i < r.getPropertyCount(); i++) {
                SoapObject child = (SoapObject) r.getProperty(i);
                listAllItems.add(child);
            }
        }
    }

    private void startWithConfigurable() throws IOException, XmlPullParserException {
        String temp = new String();
        List<String> simpleProductId = new ArrayList<>();
        for (int i = 0; i < r.getPropertyCount(); i++) {
            SoapObject child = (SoapObject) r.getProperty(i);
            String name =(String)child.getProperty("name");
            String type = (String)child.getProperty("type");
            if(!name.equals(temp) && type.equals("configurable")){
                p_configurable = createConfigurableProduct(child);
                productConfigurables.add(p_configurable);
                temp = name;
            }
            else{
                p_configurable.addSimpleProductId((String) child.getProperty("product_id"));
            }
        }
    }

    public void createPartOfItems() throws IOException, XmlPullParserException {
        String temp = new String();
        List<String> simpleProductId = new ArrayList<>();
        productConfigurables = new ArrayList<>();
        int tempCount = count+addItemsNumber;
        while(count<tempCount && allitems<listAllItems.size()) {
            SoapObject child = listAllItems.get(allitems);
            String name = (String) child.getProperty("name");
            String type = (String) child.getProperty("type");
            if (!name.equals(temp) && type.equals("configurable")) {
                p_configurable = createConfigurableProduct(child);
                p_configurable.addSimpleProductListId(simpleProductId);
                p_configurable.setSection(section);
                productConfigurables.add(p_configurable);
                temp = name;
                simpleProductId = new ArrayList<>();
                count = count+1;
            } else {
                simpleProductId.add((String) child.getProperty("product_id"));
            }

            allitems++;
        }
//        checkSection(section);
        getIndividualProductInfo();

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
        p.setSection(section);

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
            if((type.equals("configurable"))){
                startWithConfigurable();
             }
            else{
                startWithSimple();
             }
            found = true;
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
    public int getAllItemsNumber(){return this.allitems;}
    public void setAllItemsNumber(int allitems){this.allitems = allitems;}

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
