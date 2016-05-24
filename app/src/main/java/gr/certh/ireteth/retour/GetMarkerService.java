package gr.certh.ireteth.retour;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.maps.model.Marker;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by dimitriskoutsaftikis on 11/13/15.
 */

public class GetMarkerService {

    XMLParser parser;
    CustomClusterItem markerItem;

    public GetMarkerService(){
        parser = new XMLParser();
        parser.ParserFinishedEventListener(new XMLParser.ParserFinishedEventListener() {
            @Override
            public void onEvent() {
                parseXML(parser.xml);
                if (mListener !=null){
                    mListener.onEvent();
                }
            }
        });
    }

    public ArrayList<CustomClusterItem> markerList;

    ParseCompletedEventListener mListener;

    public interface ParseCompletedEventListener {
        void onEvent();
    }

    public void setCustomEventListener(ParseCompletedEventListener eventListener) {
        mListener = eventListener;
    }

    void parseXML(String xml){
        Document doc = parser.getDomElement(xml); // getting DOM element
        if (doc == null){
            return;
        }

        NodeList nl = doc.getElementsByTagName("marker");
        if (nl == null){
            return;
        }
        // looping through all song nodes <marker>
        for (int i = 0; i < nl.getLength(); i++) {
            // creating new HashMap
            HashMap<String, String> map = new HashMap<String, String>();
            Element e = (Element) nl.item(i);
            NamedNodeMap attrs = nl.item(i).getAttributes();
            if (attrs == null){
                continue;
            }

            for(int j = 0 ; j<attrs.getLength() ; j++) {
                Attr attribute = (Attr)attrs.item(j);
                Log.d("GetMarkerService", " " + attribute.getName() + " = " + attribute.getValue());
                if (attribute == null){
                    continue;
                }
                map.put(attribute.getName(), attribute.getValue());
            }


            if (map.containsKey("lat") && map.containsKey("lng")) {
                if ((Double.parseDouble(map.get("lat"))==0) || (Double.parseDouble(map.get("lng"))==0)){
                    continue;
                }
                markerItem = new CustomClusterItem(Double.parseDouble(map.get("lat")),Double.parseDouble(map.get("lng")));
                if (map.containsKey("service")){
                    markerItem.setServiceType(map.get("service"));
                    setIconByServiceType();
                }
                else {
                    markerItem.setServiceType("Review");
                    markerItem.setIconId(R.drawable.marker_review);
                }
                if (map.containsKey("provider_id")){
                    markerItem.setProviderID(Integer.parseInt(map.get("provider_id")));
                }
                if (map.containsKey("name")){
                    markerItem.setTitle(map.get("name"));
                }
                else {
                    markerItem.setTitle("");
                }

                if (map.containsKey("phone")) {
                    markerItem.setPhone(map.get("phone"));
                }
                else if (map.containsKey("youtube")){
                    markerItem.setPhone(map.get("youtube"));
                }
                else {
                    markerItem.setPhone("");
                }


                if (map.containsKey("address")){
                    markerItem.setAddress(map.get("address"));
                }
                else {
                    markerItem.setAddress("");
                }

                if (map.containsKey("details")){
                    markerItem.setDetails(map.get("details"));
                }
                else {
                    markerItem.setDetails("");
                }

                if (map.containsKey("email")){
                    markerItem.setEmail(map.get("email"));
                }
                else {
                    markerItem.setEmail("");
                }

                if (map.containsKey("status")){
                    markerItem.setStatus(Integer.parseInt(map.get("status")));
                }
                else {
                    markerItem.setStatus(0);
                }


                if (map.containsKey("votes")){
                    markerItem.setVotes(Integer.parseInt(map.get("votes")));
                }
                else if (map.containsKey("rating")){
                    markerItem.setVotes(Integer.parseInt(map.get("rating")));
                }
                else {
                    markerItem.setVotes(0);
                }

                if (map.containsKey("amea")){
                    markerItem.setAmea(Boolean.parseBoolean(map.get("amea")));
                }
                else {
                    markerItem.setAmea(false);
                }

                markerList.add(markerItem);
                markerItem = null;
            }
        }
    }

    void setIconByServiceType(){
        if (markerItem.getServiceType().compareTo("Ξενοδοχείο")==0){
            markerItem.setIconId(R.drawable.marker_hotel);
        }
        else if (markerItem.getServiceType().compareTo("Κέντρο Αποκατάστασης")==0){
            markerItem.setIconId(R.drawable.marker_rehab);
        }
        else if (markerItem.getServiceType().compareTo("Αεροπορική εταιρία")==0){
            markerItem.setIconId(R.drawable.marker_airport);
        }
        else if (markerItem.getServiceType().compareTo("Επιτόπια μετακίνηση")==0){
            markerItem.setIconId(R.drawable.marker_transport);
        }
        else if (markerItem.getServiceType().compareTo("Νοσοκομείο")==0){
            markerItem.setIconId(R.drawable.marker_hospital);
        }
        else if (markerItem.getServiceType().compareTo("Τοπική διασκέδαση")==0){
            markerItem.setIconId(R.drawable.marker_food);
        }
        else if (markerItem.getServiceType().compareTo("Κλινική")==0){
            markerItem.setIconId(R.drawable.marker_clinic);
        }
    }

    void getMarker(String URL){
        if((markerList!=null) && markerList.size()>0) {
            markerList.clear();
        }
        markerList = null;
        markerList = new ArrayList<CustomClusterItem>();

        if(android.os.Build.VERSION.SDK_INT>=11) {
            parser.getXmlFromUrl(URL);
        }
        else {
            String xml = parser.getXmlFromUrl(URL);
            parseXML(xml);
            if (mListener!=null){
                mListener.onEvent();
            }
        }
    }
}
