package myaplication.tfg.org.myapplication;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.Serializable;

/**
 * Created by jin on 2015/5/11.
 */
public class WebService implements Serializable{
    String NAMESPACE = "urn:Magento";
    String URL = "http://mininegocio.es/api/v2_soap/";
    String sessionId;
    SoapObject request;
    public SoapObject webService(SoapObject request) throws IOException, XmlPullParserException {

        this.request = request;
        SoapSerializationEnvelope env = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        env.dotNet = false;
        env.xsd = SoapSerializationEnvelope.XSD;
        env.enc = SoapSerializationEnvelope.ENC;
        env.setOutputSoapObject(this.request);
        HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);
        androidHttpTransport.call("", env);
        SoapObject result = (SoapObject) env.getResponse();

        return result;

        }

    public void setSessionId(String sessionId){
        this.sessionId = sessionId;
    }


}
