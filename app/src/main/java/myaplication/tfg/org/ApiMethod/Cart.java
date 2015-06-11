package myaplication.tfg.org.ApiMethod;

import android.util.Log;

import com.nineoldandroids.util.Property;

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

import myaplication.tfg.org.models.Address;
import myaplication.tfg.org.models.Customer;
import myaplication.tfg.org.models.Payment;
import myaplication.tfg.org.models.ProductSimple;
import myaplication.tfg.org.models.shippingMethod;
import myaplication.tfg.org.myapplication.DataHolder;

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
    private List<ProductSimple> simple;
    private Customer customer;
    private Address address;
    private myaplication.tfg.org.models.shippingMethod shippingMethod;
    private Payment payment;


    public Cart() throws IOException, XmlPullParserException {
        simple = new ArrayList<>();

        customer = new Customer();
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
            pi.setValue(p.getItemNumber());
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
        SoapObject billingAddressInfo = new SoapObject(NAMESPACE, "shoppingCartCustomerAddressEntity");
        SoapObject shippingAddressInfo = new SoapObject(NAMESPACE, "shoppingCartCustomerAddressEntity");
        Customer customer = DataHolder.getCart().getCustomerInfo();
        PropertyInfo pi = new PropertyInfo();
        PropertyInfo pi2 = new PropertyInfo();


        pi.setName("mode");
        pi.setValue("shipping");
        pi.setType(String.class);
        shippingAddressInfo.addProperty(pi);

        pi2.setName("mode");
        pi2.setValue("billing");
        pi2.setType(String.class);
        billingAddressInfo.addProperty(pi2);

        pi = new PropertyInfo();
        pi.setName("firstname");
        pi.setValue(customer.getFirstName());
        pi.setType(String.class);
        shippingAddressInfo.addProperty(pi);
        billingAddressInfo.addProperty(pi);

        pi = new PropertyInfo();
        pi.setName("lastname");
        pi.setValue(customer.getLastName());
        pi.setType(String.class);
        shippingAddressInfo.addProperty(pi);
        billingAddressInfo.addProperty(pi);

        pi = new PropertyInfo();
        pi.setName("street");
        pi.setValue(address.getStreetName());
        pi.setType(String.class);
        shippingAddressInfo.addProperty(pi);
        billingAddressInfo.addProperty(pi);

        pi = new PropertyInfo();
        pi.setName("city");
        pi.setValue(address.getCityName());
        pi.setType(String.class);
        shippingAddressInfo.addProperty(pi);
        billingAddressInfo.addProperty(pi);

        pi = new PropertyInfo();
        pi.setName("postcode");
        pi.setValue(address.getCode());
        pi.setType(String.class);
        shippingAddressInfo.addProperty(pi);
        billingAddressInfo.addProperty(pi);

        pi = new PropertyInfo();
        pi.setName("country_id");
        pi.setValue(address.getCountryName());
        pi.setType(String.class);
        shippingAddressInfo.addProperty(pi);
        billingAddressInfo.addProperty(pi);


        pi = new PropertyInfo();
        pi.setName("telephone");
        pi.setValue(address.getTelefon());
        pi.setType(String.class);
        shippingAddressInfo.addProperty(pi);
        billingAddressInfo.addProperty(pi);

        pi = new PropertyInfo();
        pi.setName("is_default_billing");
        pi.setValue(1);
        pi.setType(Integer.class);
        shippingAddressInfo.addProperty(pi);
        billingAddressInfo.addProperty(pi);

        pi = new PropertyInfo();
        pi.setName("is_default_shipping");
        pi.setValue(1);
        pi.setType(Integer.class);
        shippingAddressInfo.addProperty(pi);
        billingAddressInfo.addProperty(pi);

        SoapObject EntityArray = new SoapObject(NAMESPACE, "shoppingCartProductEntityArray");
        EntityArray.addProperty("customer", shippingAddressInfo);
        EntityArray.addProperty("customer2", billingAddressInfo);

        request = new SoapObject(NAMESPACE, "shoppingCartCustomerAddresses");
        request.addProperty("sessionId", sessionId);
        request.addProperty("quoteId", cartId);
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
        request.addProperty("quoteId", cartId);
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
        request.addProperty("quoteId", cartId);
        env.setOutputSoapObject(request);
        androidHttpTransport.call("", env);
        SoapObject paymentMethod =(SoapObject)env.getResponse();
        Log.d("payment money",paymentMethod.toString());
        return paymentMethod;
    }

    public boolean setPayment(Payment payment) throws IOException, XmlPullParserException {
        boolean ok;
        SoapObject paymentMethod= new SoapObject(NAMESPACE, "shoppingCartPaymentMethodEntity");
        PropertyInfo pi = new PropertyInfo();
        pi.setName("po_number");
        pi.setValue(payment.getPoNumber());
        pi.setType(String.class);
        paymentMethod.addProperty(pi);

        pi = new PropertyInfo();
        pi.setName("method");
        pi.setValue(payment.getPayMethod());
        pi.setType(String.class);
        paymentMethod.addProperty(pi);

        pi = new PropertyInfo();
        pi.setName("cc_cid");
        pi.setValue(payment.getCardCID());
        pi.setType(String.class);
        paymentMethod.addProperty(pi);

        pi = new PropertyInfo();
        pi.setName("cc_owner");
        pi.setValue(payment.getCardOwner());
        pi.setType(String.class);
        paymentMethod.addProperty(pi);

        pi = new PropertyInfo();
        pi.setName("cc_number");
        pi.setValue(payment.getCardNumber());
        pi.setType(String.class);
        paymentMethod.addProperty(pi);

        pi = new PropertyInfo();
        pi.setName("cc_type");
        pi.setValue(payment.getCardType());
        pi.setType(String.class);
        paymentMethod.addProperty(pi);

        pi = new PropertyInfo();
        pi.setName("cc_exp_year");
        pi.setValue(payment.getCardExpYear());
        pi.setType(String.class);
        paymentMethod.addProperty(pi);

        pi = new PropertyInfo();
        pi.setName("cc_exp_month");
        pi.setValue(payment.getCardMonth());
        pi.setType(String.class);
        paymentMethod.addProperty(pi);

        request = new SoapObject(NAMESPACE, "shoppingCartPaymentMethod");
        request.addProperty("sessionId", sessionId);
        request.addProperty("quoteId", cartId);
        request.addProperty("method", paymentMethod);
        env.setOutputSoapObject(request);
        androidHttpTransport.call("", env);
        ok =(boolean)env.getResponse();
        if(ok == true){
            System.out.println("add payment correctly");
        }
        return ok;
    }


    public String createOrder() throws IOException, XmlPullParserException {
        request = new SoapObject(NAMESPACE, "shoppingCartOrder");
        request.addProperty("sessionId", sessionId);
        request.addProperty("quoteId", cartId);
        env.setOutputSoapObject(request);
        androidHttpTransport.call("", env);
        String result =(String)env.getResponse();
        Log.d("result of create order",result.toString());
        return result;
    }
    public SoapObject getTotalAmountt() throws IOException, XmlPullParserException {
        request = new SoapObject(NAMESPACE,"shoppingCartTotals");
        request.addProperty("sessionId",sessionId);
        request.addProperty("quoteId", cartId);
        env.setOutputSoapObject(request);
        androidHttpTransport.call("", env);
        SoapObject totalAmount =(SoapObject)env.getResponse();
        Log.d("Total Amount ",totalAmount.toString());
        return totalAmount;
    }

    public void addShippingMethod(String shippingMethodCode) throws IOException, XmlPullParserException {
        request = new SoapObject(NAMESPACE, "shoppingCartShippingMethod");
        request.addProperty("sessionId", sessionId);
        request.addProperty("quoteId", cartId);
        request.addProperty("method", shippingMethodCode);
        env.setOutputSoapObject(request);
        androidHttpTransport.call("", env);
        boolean result = (boolean) env.getResponse();
        if (result == true) {
            System.out.println("set correctly the shippingMethod");
        }
    }


    public void setListItems(List<ProductSimple> simple){
        this.simple = simple;
    }

    public List<ProductSimple> getListItems(){
        return this.simple;
    }

    public void setCustomer(Customer customer){
        this.customer = customer;
    }
    public Customer getCustomerInfo(){
        return this.customer;
    }

    public void setAddress(Address address){
        this.address = address;
    }
    public Address getAddressInfo(){
        return this.address;
    }

    public void setShippingMethod(myaplication.tfg.org.models.shippingMethod ship){
        this.shippingMethod = ship;
    }
    public shippingMethod getShippingMethod(){
        return this.shippingMethod;
    }

    public void sPayment(Payment p){
        this.payment = p;
    }

    public Payment gPayment(){
        return this.payment;
    }
}
