package myaplication.tfg.org.myapplication;

/**
 * Created by jin on 2015/5/29.
 */
public class Address {
    private String streetName;
    private String code;
    private String telefon;
    private String countryName;
    private String cityName;

    public Address(){
    }

    public Address(String streetName,String code, String telefon,String cityName){
        this.streetName = streetName;
        this.code = code;
        this.telefon = telefon;
        this.countryName = countryName;
        this.cityName = cityName;
    }

    public void setStreetName(String streetName){
        this.streetName = streetName;
    }
    public void setCode(String postCode){
        this.code = postCode;
    }
    public void setTelefon(String telefon){
        this.telefon = telefon;
    }
    public void setCountryName(String countryName){
        this.countryName = countryName;
    }
    public void setCityName(String cityName){
        this.cityName = cityName;
    }

    public String getStreetName(){
        return this.streetName;
    }
    public String getCode(){
        return this.code;
    }
    public String getTelefon(){
        return this.telefon;
    }
    public String getCountryName(){
        return this.countryName;
    }
    public String getCityName(){
        return this.cityName;
    }
}
