package org.astrogrid.registry.server.admin;

/**
 * Class: AuthorityList
 * Description: Small object class to hold authorityid's and version numbers along with known owners of an authority. Currently
 * used exclusively by RegistryAdminService for storing in HashMap for management of authority id's. Normally
 * the storage is key (versionNumber,authorityid) and value (versionNumber,authorityid, main authorityid owner).
 *  
 * @author Kevin Benson
 *
 */
public class AuthorityList {
    
    /**
     * authority id
     */
    private String authorityID = null;
    /**
     * version number
     */
    private String versionNumber = null;
    
    /**
     * owner authority id
     */
    private String owner = null;

    /**
     * Constructor: AuthorityList
     * Description: Constructor for authorityid, versionNumber, owner.
     * @param authorityID - authority id string.
     * @param versionNumber - version number of the registry it is holding.
     * @param owner - owner authority id.
     */
    public AuthorityList(String authorityID, String versionNumber, String owner) {
        this.authorityID = authorityID.trim();
        this.versionNumber = versionNumber.trim();
        this.owner = owner;        
    }

    /**
     * Constructor: AuthorityList
     * Description: Constructor for authorityid, versionNumber, owner.
     * @param authorityID - authority id string.
     * @param versionNumber - version number of the registry it is holding.
     */
    public AuthorityList(String authorityID, String versionNumber) {
        this.authorityID = authorityID.trim();
        this.versionNumber = versionNumber.trim();
    }    
    
    
    /**
     * Method: getAuthorityID
     * Description: return the authority id
     * @return String of the authorityid
     */
    public String getAuthorityID() {
        return this.authorityID;
    }
    
    /**
     * Method: getVersionNumber
     * Description: return the version number of the vr namespace
     * @return String of the version number.
     */    
    public String getVersionNumber() {
        return this.versionNumber;
    }

    /**
     * Method: getOwner
     * Description: return the authority id's owner
     * @return String of the authorityid's owner
     */
    public String getOwner() {
        return this.owner;
    }
    
    /**
     * Method: setAuthorityID
     * Description: Set the authority ID
     */
    public void setAuthorityID(String authorityID) {
        this.authorityID = authorityID;
    }
    
    /**
     * Method: setVersionNumber
     * Description: Set the version number
     */    
    public void setVersionNumber(String versionNumber) {
        this.versionNumber = versionNumber;
    }

    /**
     * Method: setOwner
     * Description: Set the owner which is the owner/manager of the authority id.
     */    
    public void setOwner(String owner) {
        this.owner = owner;
    }
    
    /**
     * Mehod: hasOwner
     * Description: Does this authority id have a owner
     * @return boolean if the authority id has a owning authority id or not.
     */
    public boolean hasOwner(String owner) {
        return this.owner.equals(owner);
    }
    
    /**
     * Method: equals
     * Description: Determine if this AuthorityList object equals another AuthorityList object, used in the Hashtable index type methods a lot.
     * @return boolean if the AuthorityList equals the given AuthorityList object.
     */
    public boolean equals(Object authList) {
        AuthorityList al = null;
        if(authList instanceof AuthorityList)
            al = (AuthorityList)authList;
        else
            return false;
        if(this.authorityID.equals(al.getAuthorityID()) && 
           this.versionNumber.equals(al.getVersionNumber())) {
            if(this.owner == null && al.getOwner() == null)
                return true;
            else if(this.owner != null && this.owner.equals(al.getOwner()))
                return true;
        }
        return false;
    }
    
    /**
     * Method: hashCode
     * Description: Return the hashcode of this object, used for searching and indexing in the HashMap.
     * @return int hashCode. 
     */
    public int hashCode() {
        int hashCode = (this.authorityID.hashCode() + this.versionNumber.hashCode());
        if(this.owner != null) 
            hashCode += this.owner.hashCode();
        return hashCode;
    }
    
    /**
     * Method: toString
     * Purpose: Give a string representation of the AuthorityList object.
     * @return String representation of this object.
     */
    public String toString() {
        return "AuthorityID: " + this.authorityID + 
               " VersionNumber: " + this.versionNumber + 
               " Owner: " + this.owner;
    }
}