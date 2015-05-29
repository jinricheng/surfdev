package myaplication.tfg.org.myapplication;

/**
 * Created by jin on 2015/5/29.
 */
public class Customer {
    private String firstName;
    private String lastName;
    private String emailAdress;

    public Customer(){};

    public Customer(String firstName,String lastName, String emailAdress){
        this.firstName = firstName;
        this.lastName = lastName;
        this.emailAdress = emailAdress;
    }

    public void setFirstName(String firstName){
        this.firstName= firstName;
    }

    public void setLastName(String lastName){
        this.lastName = lastName;
    }

    public void setEmailAdress(String emailAdress){
        this.emailAdress = emailAdress;
    }


    public String getFirstName(){
        return this.firstName;
    }

    public String getLastName(){
        return this.lastName;
    }

    public String getEmailAdress(){
        return this.emailAdress;
    }
}
