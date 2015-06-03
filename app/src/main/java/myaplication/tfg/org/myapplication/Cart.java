package myaplication.tfg.org.myapplication;

import android.util.Log;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;

/**
 * Created by jin on 2015/6/3.
 */
public class Cart implements Serializable{
    private int cartId =0;
    private SoapSerializationEnvelope env;
    private HttpTransportSE androidHttpTransport;
    private SoapObject request;
    private SoapObject r;
    private String sessionId;
    String NAMESPACE = "urn:Magento";
    String URL = "http://gonegocio.es/index.php/api/v2_soap/";

    public Cart() throws IOException, XmlPullParserException {
        setupSessionLogin();
    }

    private void setupSessionLogin() throws IOException, XmlPullParserException {
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


    public void createShopCart() throws IOException, XmlPullParserException {
        request = new SoapObject(NAMESPACE,"shoppingCartCreate");
        request.addProperty("sessionId", sessionId);
        env.setOutputSoapObject(request);
        androidHttpTransport.call("", env);
        cartId = (Integer)env.getResponse();
        DataHolder.setCartId(cartId);
    }

    public int getCartId(){
        return this.cartId;
    }


    public void addToCart(List<ProductSimple> checkOutItemsList) throws IOException, XmlPullParserException {
        for(ProductSimple p : checkOutItemsList) {
            SoapObject SingleProduct = new SoapObject(NAMESPACE, "shoppingCartProductEntity");
            PropertyInfo pi = new PropertyInfo();
            pi.setName("product_id");
            pi.setValue(Integer.parseInt(p.getProduct_id()));
            pi.setType(Integer.class);
            SingleProduct.addProperty(pi);

            pi = new PropertyInfo();
            pi.setName("sku");
            pi.setValue(p.getSku());
            pi.setType(String.class);
            SingleProduct.addProperty(pi);

            pi = new PropertyInfo();
            pi.setName("qty");
            pi.setValue(p.getQuantity());
            pi.setType(Double.class);
            SingleProduct.addProperty(pi);
            SoapObject EntityArray = new SoapObject(NAMESPACE, "shoppingCartProductEntityArray");
            EntityArray.addProperty("products", SingleProduct);

            request = new SoapObject(NAMESPACE, "shoppingCartProductAdd");
            request.addProperty("sessionId", sessionId);
            request.addProperty("quoteId", cartId);
            request.addProperty("products", EntityArray);
            env.setOutputSoapObject(request);
            androidHttpTransport.call("", env);
            boolean ok = (boolean) env.getResponse();

            if (ok == true) {
                System.out.println("add product correctly");
            }
        }
    }

    public void getRetriveInformation() throws IOException, XmlPullParserException {
        request = new SoapObject(NAMESPACE,"shoppingCartProductList");
        request.addProperty("sessionId", sessionId);
        request.addProperty("quoteId", cartId);
        env.setOutputSoapObject(request);
        androidHttpTransport.call("", env);
        SoapObject r  = (SoapObject)env.getResponse();
        Log.d("retriveList", r.toString());
    }


    public void removeDataFromShoppingCart(ProductSimple productSimple) throws IOException, XmlPullParserException {
        SoapObject SingleProduct = new SoapObject(NAMESPACE, "shoppingCartProductEntity");
        PropertyInfo pi = new PropertyInfo();
        pi.setName("product_id");
        pi.setValue(Integer.parseInt(productSimple.getProduct_id()));
        pi.setType(Integer.class);
        SingleProduct.addProperty(pi);

        pi = new PropertyInfo();
        pi.setName("sku");
        pi.setValue(productSimple.getSku());
        pi.setType(String.class);
        SingleProduct.addProperty(pi);

        pi = new PropertyInfo();
        pi.setName("qty");
        pi.setValue(productSimple.getQuantity());
        pi.setType(Double.class);
        SingleProduct.addProperty(pi);
        SoapObject EntityArray = new SoapObject(NAMESPACE, "shoppingCartProductEntityArray");
        EntityArray.addProperty("products", SingleProduct);

        request = new SoapObject(NAMESPACE, "shoppingCartProductRemove");
        request.addProperty("sessionId", sessionId);
        request.addProperty("quoteId", cartId);
        request.addProperty("products", EntityArray);
        env.setOutputSoapObject(request);
        androidHttpTransport.call("", env);
        boolean ok = (boolean) env.getResponse();

        if (ok == true) {
            System.out.println("remove product correctly");
        }
    }


    public void addAddress(Address address) throws IOException, XmlPullParserException {
        SoapObject addressInfo = new SoapObject(NAMESPACE, "shoppingCartCustomerAddressEntity");
        Customer customer = DataHolder.getCustomer();
        PropertyInfo pi = new PropertyInfo();
        System.out.println(address.getCityName()+" "+address.getStreetName()+" "+address.getCountryName());
        System.out.println(customer.getFirstName()+" "+customer.getLastName()+" "+customer.getEmailAdress());
        pi.setName("mode");
        pi.setValue("shipping");
        pi.setType(String.class);
        addressInfo.addProperty(pi);

        pi = new PropertyInfo();
        pi.setName("firstname");
        pi.setValue(customer.getFirstName());
        pi.setType(String.class);
        addressInfo.addProperty(pi);

        pi = new PropertyInfo();
        pi.setName("lastname");
        pi.setValue(customer.getFirstName());
        pi.setType(String.class);
        addressInfo.addProperty(pi);

        pi = new PropertyInfo();
        pi.setName("street");
        pi.setValue(address.getStreetName());
        pi.setType(String.class);
        addressInfo.addProperty(pi);

        pi = new PropertyInfo();
        pi.setName("city");
        pi.setValue(address.getCityName());
        pi.setType(String.class);
        addressInfo.addProperty(pi);

        pi = new PropertyInfo();
        pi.setName("postcode");
        pi.setValue(address.getCode());
        pi.setType(String.class);
        addressInfo.addProperty(pi);

        pi = new PropertyInfo();
        pi.setName("country_id");
        pi.setValue(address.getCountryName());
        pi.setType(String.class);
        addressInfo.addProperty(pi);

        pi = new PropertyInfo();
        pi.setName("telephone");
        pi.setValue(address.getTelefon());
        pi.setType(String.class);
        addressInfo.addProperty(pi);

        pi = new PropertyInfo();
        pi.setName("is_default_billing");
        pi.setValue(0);
        pi.setType(Integer.class);
        addressInfo.addProperty(pi);

        pi = new PropertyInfo();
        pi.setName("is_default_shipping");
        pi.setValue(0);
        pi.setType(Integer.class);
        addressInfo.addProperty(pi);

        SoapObject EntityArray = new SoapObject(NAMESPACE, "shoppingCartProductEntityArray");
        EntityArray.addProperty("customer", addressInfo);
        request = new SoapObject(NAMESPACE, "shoppingCartCustomerAddresses");
        request.addProperty("sessionId", sessionId);
        request.addProperty("quoteId", DataHolder.getCartId());
        request.addProperty("customer", EntityArray);

        env.setOutputSoapObject(request);
        androidHttpTransport.call("", env);
        boolean ok = (boolean) env.getResponse();
        if (ok == true) {
            System.out.println("add address correctly");
        }
    }


    public SoapObject getShippingment() throws IOException, XmlPullParserException {
        request = new SoapObject(NAMESPACE,"shoppingCartShippingList");
        request.addProperty("sessionId",sessionId);
        request.addProperty("quoteId", DataHolder.getCartId());
        env.setOutputSoapObject(request);
        androidHttpTransport = new HttpTransportSE(URL);
        androidHttpTransport.call("", env);
        SoapObject shippingmethod = (SoapObject)env.getResponse();
        Log.d("shippingment available", shippingmethod.toString());
        return shippingmethod;
    }


    public SoapObject getPayment() throws IOException, XmlPullParserException {
        request = new SoapObject(NAMESPACE,"shoppingCartPaymentList");
        request.addProperty("sessionId",sessionId);
        request.addProperty("quoteId", DataHolder.getCartId());
        env.setOutputSoapObject(request);
        androidHttpTransport.call("", env);
        SoapObject paymentMethod =(SoapObject)env.getResponse();
        Log.d("payment money",paymentMethod.toString());
        return paymentMethod;
    }
}
