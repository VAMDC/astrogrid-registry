package org.astrogrid.registry.client.query;

import org.astrogrid.store.Ivorn;
import java.util.Collection;
import java.util.ArrayList;
import java.net.URL;
//import org.astrogrid.registry.common.InterfaceType;

public class ResourceData {
    
    private Ivorn ivorn = null;
    
    private ArrayList interfaceTypes = null;
    
    private String title = null;
    
    private String description = null;
    
    private URL accessURL = null;
    
    public ResourceData() {
        interfaceTypes = new ArrayList();
    }
    
    public URL getAccessURL() {
        return this.accessURL;
    }
    
    public void setAccessURL(URL accessURL) {
        this.accessURL = accessURL;
    }
    
    public String getTitle() {
        return this.title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getDescription() {
        return this.description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public Ivorn getIvorn() {
        return this.ivorn;
    }
    
    public void setIvorn(Ivorn ivorn) {
        this.ivorn = ivorn;
    }
    
    public Collection getInterface() {
        return this.interfaceTypes;
    }
    
    /*
    public void addInterfaceType(InterfaceType interfaceType) {
        interfaceTypes.add(interfaceType);
    }
    */
    
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("Ivon = " + getIvorn() + "\n");
        sb.append("Title = " + getTitle() + "\n");
        sb.append("Description = " + getDescription() + "\n");
        sb.append("AccessURL = " + getAccessURL() + "\n");        
        return sb.toString();
    }
    
}