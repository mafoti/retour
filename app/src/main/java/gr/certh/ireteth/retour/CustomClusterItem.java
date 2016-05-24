package gr.certh.ireteth.retour;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.maps.android.clustering.ClusterItem;

/**
 * Created by dimitriskoutsaftikis on 11/13/15.
 */

public class CustomClusterItem implements ClusterItem {
    private final LatLng mPosition;
    private int iconId;
    private String title;
    private int providerID;
    private String email;
    private String address;
    private String details;
    private String phone;
    private Boolean amea;
    private String serviceType;
    private int status;
    private int votes;

    public void setVotes(int votes){
        this.votes = votes;
    }

    public int getVotes(){
        return this.votes;
    }

    public void setStatus(int status){
        this.status = status;
    }

    public int getStatus(){
        return this.status;
    }

    public void setServiceType(String serviceType){
        this.serviceType = serviceType;
    }

    public String getServiceType(){
        return this.serviceType;
    }

    public void setAmea(Boolean amea){
        this.amea = amea;
    }

    public Boolean getAmea(){
        return this.amea;
    }

    public void setPhone(String phone){
        this.phone = phone;
    }

    public String getPhone(){
        return this.phone;
    }

    public void setDetails(String details){
        this.details = details;
    }

    public String getDetails(){
        return this.details;
    }

    public void setAddress(String address){
        this.address = address;
    }

    public String getAddress(){
        return this.address;
    }

    public void setEmail(String email){
        this.email = email;
    }

    public String getEmail(){
        return this.email;
    }

    public void setProviderID(int id){
        this.providerID = id;
    }

    public int getProviderID(){
        return  this.providerID;
    }

    public void setTitle(String title){
        this.title = title;
    }

    public String getTitle(){
        return this.title;
    }

    public void setIconId(int iconId){
        this.iconId = iconId;
    }

    public int getIconId(){
        return this.iconId;
    }

    public CustomClusterItem(){
        mPosition = null;
    }

    public CustomClusterItem(double lat, double lng) {
        mPosition = new LatLng(lat, lng);
    }

    @Override
    public LatLng getPosition() {
        return mPosition;
    }
}