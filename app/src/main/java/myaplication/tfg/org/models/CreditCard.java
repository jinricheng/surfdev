package myaplication.tfg.org.models;

/**
 * Created by jin on 2015/6/5.
 */
public class CreditCard implements Payment {
    private String poNumber;
    private String PayMethod;
    private String cardCID;
    private String cardOwner;
    private String cardNumber;
    private String cardType;
    private String cardYear;
    private String cardMonth;

    public CreditCard(){
        poNumber = "";
        PayMethod = "";
        cardCID="";
        cardOwner="";
        cardNumber="";
        cardType = "";
        cardYear = "";
        cardMonth = "";
    }

    @Override
    public void setPoNumber(String poNumber) {
        this.poNumber = poNumber;
    }

    @Override
    public String getPoNumber() {
        return this.poNumber;
    }

    @Override
    public void setPayMethod(String payMethod) {
        this.PayMethod = payMethod;
    }

    @Override
    public String getPayMethod() {
        return this.PayMethod;
    }

    @Override
    public void setCardCID(String cardCID) {
        this.cardCID = cardCID;
    }

    @Override
    public String getCardCID() {
        return this.cardCID;
    }

    @Override
    public void setCardOwner(String cardOwner) {
        this.cardOwner = cardOwner;
    }

    @Override
    public String getCardOwner() {
        return this.cardOwner;
    }

    @Override
    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    @Override
    public String getCardNumber() {
        return this.cardNumber;
    }

    @Override
    public void setCardType(String cardType) {
        this.cardType = cardType;
    }

    @Override
    public String getCardType() {
        return this.cardType;
    }

    @Override
    public void setCardExpYear(String cardYear) {
        this.cardYear = cardYear;
    }

    @Override
    public String getCardExpYear() {
        return this.cardYear;
    }

    @Override
    public void setCardMonth(String cardMonth) {
        this.cardMonth = cardMonth;
    }

    @Override
    public String getCardMonth() {
        return this.cardMonth;
    }
}
