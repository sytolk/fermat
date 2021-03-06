package com.bitdubai.fermat_p2p_api.layer.all_definition.communication.commons.profiles;

import com.bitdubai.fermat_p2p_api.layer.all_definition.communication.commons.util.GsonProvider;

/**
 * The Class <code>ClientProfile</code>
 * <p/>
 * Created by Roberto Requena - (rart3001@gmail.com) on 02/12/15.
 *
 * @version 1.0
 * @since Java JDK 1.7
 */
public class ClientProfile extends Profile {

    /**
     * Represent the deviceType
     */
    private String deviceType;

    /**
     * Constructor
     */
    public ClientProfile(){
        super();
    }

    /**
     * Gets the value of deviceType and returns
     *
     * @return deviceType
     */
    public String getDeviceType() {
        return deviceType;
    }

    /**
     * Sets the deviceType
     *
     * @param deviceType to set
     */
    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    /**
     * Return this object in json string
     *
     * @return json string
     */
    public String toJson(){
        return GsonProvider.getGson().toJson(this, ClientProfile.class);
    }

    /**
     * Get the object
     *
     * @param jsonString
     * @return ClientProfile
     */
    public static ClientProfile fromJson(String jsonString) {
        return GsonProvider.getGson().fromJson(jsonString, ClientProfile.class);
    }

}
