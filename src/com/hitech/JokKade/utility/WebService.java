package com.hitech.JokKade.utility;

import java.util.List;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.AndroidHttpTransport;
import org.ksoap2.transport.HttpTransportSE;

import android.util.Log;


public class WebService {
    
    String namespace = "http://howdoi.org/";
   // private String url = "http://10.0.2.2:4840/Services/MyWebService.asmx";
    private String url = "http://zanbaas.ir/Services/MyWebService.asmx";
    String SOAP_ACTION;
    SoapObject request = null;
    SoapSerializationEnvelope envelope;
    AndroidHttpTransport androidHttpTransport;
     
    public WebService() {
    }
     
    protected void SetEnvelope() {
        try {
        	envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        	envelope.dotNet = true;
            envelope.setOutputSoapObject(request);
            androidHttpTransport = new AndroidHttpTransport(url);
        } catch (Exception e) {
            System.out.println("Soap Exception---->>>" + e.toString());   
        }
    }
 
    public String ExecWebServiceRequset(String MethodName,List<Parameter> params)
      {
        try {
        	request = new SoapObject(namespace, MethodName);  
            for (Parameter parameter : params) {
				PropertyInfo wsParam = new PropertyInfo();
				wsParam.setName(parameter.Name);
				wsParam.setValue(parameter.Value);
				wsParam.setType(parameter.getClass());
				request.addProperty(wsParam);
			}
            SetEnvelope();
            SOAP_ACTION = namespace + MethodName;
            androidHttpTransport.call(SOAP_ACTION, envelope);
            String result = envelope.getResponse().toString();
           // if(result == null)
            //	return null;
           // JsonParser parser = new JsonParser();
            //return parser.UserParser(result);
            return result;
        } 
        catch (Exception e) {
        	e.printStackTrace();
            return null;
        }
    }
    

    public String ExecWebServiceRequset	(String MethodName)
      {
        try {
            SOAP_ACTION = namespace + MethodName;
            request = new SoapObject(namespace, MethodName);
            SetEnvelope();
            androidHttpTransport.call(SOAP_ACTION, envelope);
            String result = envelope.getResponse().toString();
            JsonParser parser = new JsonParser();
            return result;
            
        } catch (Exception e) {
        	String err= e.toString();
            return e.toString();
        }
    }
}