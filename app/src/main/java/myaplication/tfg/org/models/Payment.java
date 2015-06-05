package myaplication.tfg.org.models;

/**
 * Created by jin on 2015/6/5.
 */
public interface Payment {

    public void setPoNumber(String poNumber);
    public String getPoNumber();

    public void setPayMethod(String payMethod);
    public String getPayMethod();

    public void setCardCID(String cardCID);
    public String getCardCID();

    public void setCardOwner(String cardOwner);
    public String getCardOwner();

    public void setCardNumber(String cardNumber);
    public String getCardNumber();

    public void setCardType(String cardType);
    public String getCardType();

    public void setCardExpYear(String cardYear);
    public String getCardExpYear();

    public void setCardMonth(String cardMonth);
    public String getCardMonth();
}
