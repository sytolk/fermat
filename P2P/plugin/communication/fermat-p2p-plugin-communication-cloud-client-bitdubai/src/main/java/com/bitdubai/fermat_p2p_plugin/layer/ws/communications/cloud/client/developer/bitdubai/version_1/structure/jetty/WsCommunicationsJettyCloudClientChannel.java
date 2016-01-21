/*
 * @#WsCommunicationsCloudClient.java - 2015
 * Copyright bitDubai.com., All rights reserved.
 * You may not modify, use, reproduce or distribute this software.
 * BITDUBAI/CONFIDENTIAL
 */
package com.bitdubai.fermat_p2p_plugin.layer.ws.communications.cloud.client.developer.bitdubai.version_1.structure.jetty;

import com.bitdubai.fermat_api.layer.all_definition.components.interfaces.PlatformComponentProfile;
import com.bitdubai.fermat_api.layer.all_definition.crypto.asymmetric.AsymmetricCryptography;
import com.bitdubai.fermat_api.layer.all_definition.crypto.asymmetric.ECCKeyPair;
import com.bitdubai.fermat_api.layer.all_definition.events.EventSource;
import com.bitdubai.fermat_api.layer.all_definition.events.interfaces.FermatEvent;
import com.bitdubai.fermat_p2p_api.layer.all_definition.communication.commons.contents.FermatPacketDecoder;
import com.bitdubai.fermat_p2p_api.layer.all_definition.communication.enums.P2pEventType;
import com.bitdubai.fermat_p2p_api.layer.all_definition.communication.events.CompleteClientComponentRegistrationNotificationEvent;
import com.bitdubai.fermat_p2p_api.layer.p2p_communication.commons.contents.FermatPacket;
import com.bitdubai.fermat_p2p_api.layer.p2p_communication.commons.enums.FermatPacketType;
import com.bitdubai.fermat_p2p_api.layer.p2p_communication.commons.enums.JsonAttNamesConstants;
import com.bitdubai.fermat_p2p_plugin.layer.ws.communications.cloud.client.developer.bitdubai.version_1.structure.WsCommunicationsCloudClientConnection;
import com.bitdubai.fermat_p2p_plugin.layer.ws.communications.cloud.client.developer.bitdubai.version_1.structure.agents.WsCommunicationsCloudClientSupervisorConnectionAgent;
import com.bitdubai.fermat_p2p_plugin.layer.ws.communications.cloud.client.developer.bitdubai.version_1.structure.jetty.conf.CLoudClientConfigurator;
import com.bitdubai.fermat_p2p_plugin.layer.ws.communications.cloud.client.developer.bitdubai.version_1.structure.processors.FermatPacketProcessor;
import com.bitdubai.fermat_pip_api.layer.platform_service.event_manager.interfaces.EventManager;
import com.google.gson.JsonObject;

import org.java_websocket.WebSocket;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft;
import org.java_websocket.framing.Framedata;
import org.java_websocket.handshake.ServerHandshake;

import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.websocket.ClientEndpoint;
import javax.websocket.CloseReason;
import javax.websocket.ContainerProvider;
import javax.websocket.DeploymentException;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;

/**
 * The Class <code>com.bitdubai.fermat_p2p_plugin.layer.ws.communications.cloud.client.developer.bitdubai.version_1.structure.WsCommunicationsCloudClientChannel</code>
 * <p/>
 * Created by Roberto Requena - (rart3001@gmail.com) on 02/09/15.
 *
 * @version 1.0
 * @since Java JDK 1.7
 */
@ClientEndpoint(configurator = CLoudClientConfigurator.class)
public class WsCommunicationsJettyCloudClientChannel{

    /**
     * DealWithEvents Interface member variables.
     */
    private EventManager eventManager;

    /**
     * Represent the wsCommunicationsCloudClientConnection
     */
    private WsCommunicationsCloudClientConnection wsCommunicationsCloudClientConnection;

    /**
     * Represent the wsCommunicationsCloudClientSupervisorConnectionAgent
     */
    private WsCommunicationsCloudClientSupervisorConnectionAgent wsCommunicationsCloudClientSupervisorConnectionAgent;

    /**
     * Represent the temporalIdentity
     */
    private ECCKeyPair temporalIdentity;

    /**
     * Represent the clientIdentity
     */
    private ECCKeyPair clientIdentity;

    /**
     * Represent the serverIdentity
     */
    private String serverIdentity;

    /**
     * Represent the platformComponentProfile
     */
    private PlatformComponentProfile platformComponentProfile;

    /**
     * Holds the packet processors objects
     */
    private Map<FermatPacketType, CopyOnWriteArrayList<FermatPacketProcessor>> packetProcessorsRegister;

    /**
     * Represent is the client is register with the server
     */
    private boolean isRegister;


    private Session clientSession;

    /**
     * Constructor with parameters
     * @param wsCommunicationsCloudClientConnection
     * @param eventManager
     * @param clientIdentity
     * @throws IOException
     * @throws DeploymentException
     */
    public WsCommunicationsJettyCloudClientChannel(WsCommunicationsCloudClientConnection wsCommunicationsCloudClientConnection, EventManager eventManager, ECCKeyPair clientIdentity) throws IOException, DeploymentException {

        this.clientIdentity = clientIdentity;
        this.temporalIdentity = CLoudClientConfigurator.tempIdentity;
        this.packetProcessorsRegister = new ConcurrentHashMap<>();
        this.wsCommunicationsCloudClientConnection = wsCommunicationsCloudClientConnection;
        this.eventManager = eventManager;
        this.isRegister = Boolean.FALSE;
    }

    public void sendMessage(final String message) {
        clientSession.getAsyncRemote().sendText(message);
    }


    /**
     * (non-javadoc)
     * @see WebSocketClient#onOpen(ServerHandshake)
     */
    @OnOpen
    public void onOpen(final Session session) {

        System.out.println(" --------------------------------------------------------------------- ");
        System.out.println(" WsCommunicationsCloudClientChannel - Starting method onOpen");
        System.out.println(" WsCommunicationsCloudClientChannel - ready state = "+session.getId());
        this.clientSession = session;
    }

    /**
     * (non-javadoc)
     * @see WebSocketClient#onMessage(String)
     */
    @OnMessage
    public void onMessage(String fermatPacketEncode) {

        System.out.println(" --------------------------------------------------------------------- ");
        System.out.println(" WsCommunicationsCloudClientChannel - Starting method onMessage(String)");
       // System.out.println(" WsCommunicationsCloudClientChannel - encode fermatPacket " + fermatPacketEncode);

        FermatPacket fermatPacketReceive = null;

        /*
         * If the client is no register
         */
        if (!isRegister){

            System.out.println(" WsCommunicationsCloudClientChannel - decoding fermatPacket with temp-identity ");

            /**
             * Decode the message with the temporal identity
             */
            fermatPacketReceive = FermatPacketDecoder.decode(fermatPacketEncode, temporalIdentity.getPrivateKey());

        }else {

            System.out.println(" WsCommunicationsCloudClientChannel - decoding fermatPacket with client-identity ");

            /**
             * Decode the message with the client identity
             */
            fermatPacketReceive = FermatPacketDecoder.decode(fermatPacketEncode, clientIdentity.getPrivateKey());

            /*
             * Validate the signature
             */
            validateFermatPacketSignature(fermatPacketReceive);
        }

       // System.out.println(" WsCommunicationsCloudClientChannel - decode fermatPacket " + fermatPacketReceive.toJson());


        //verify is packet supported
        if (packetProcessorsRegister.containsKey(fermatPacketReceive.getFermatPacketType())){

             /*
             * Call the processors for this packet
             */
            for (FermatPacketProcessor fermatPacketProcessor :packetProcessorsRegister.get(fermatPacketReceive.getFermatPacketType())) {

                /*
                 * Processor make his job
                 */
                fermatPacketProcessor.processingPackage(fermatPacketReceive);
            }

        }else {

            System.out.println(" WsCommunicationsCloudClientChannel - Packet type " + fermatPacketReceive.getFermatPacketType() + "is not supported");

        }

    }

    /**
     * (non-javadoc)
     * @see WebSocketClient#onClose(int, String, boolean)
     */
    @OnClose
    public void onClose(final Session userSession, final CloseReason reason) {

        System.out.println(" --------------------------------------------------------------------- ");
        System.out.println(" WsCommunicationsCloudClientChannel - Starting method onClose");

       /* try {
            switch (code) {

                case 1002:
                case 1006:
                        raiseClientConnectionLooseNotificationEvent();
                        System.out.println(" WsCommunicationsCloudClientChannel - Connection loose");
                    break;

                default:

                        if (reason.contains("ENETUNREACH") || reason.contains("connect failed:ETIMEDOUT (Connection timed out)")){
                            raiseClientConnectionLooseNotificationEvent();
                        }else{
                            raiseClientConnectionCloseNotificationEvent();
                            setIsRegister(Boolean.FALSE);
                        }

                    break;
            }

        }catch (Exception e){
            e.printStackTrace();
        } */

    }

    /**
     * (non-javadoc)
     * @see WebSocketClient#onError(Exception)
     */
    @OnError
    public void onError(Exception ex) {
        System.out.println(" --------------------------------------------------------------------- ");
        System.out.println(" WsCommunicationsCloudClientChannel - Starting method onError");
        ex.printStackTrace();
    }

    /**
     * Validate the signature of the packet
     * @param fermatPacketReceive
     */
    private void validateFermatPacketSignature(FermatPacket fermatPacketReceive){

        System.out.println(" WsCommunicationsCloudClientChannel - validateFermatPacketSignature");

         /*
         * Validate the signature
         */
        boolean isValid = AsymmetricCryptography.verifyMessageSignature(fermatPacketReceive.getSignature(), fermatPacketReceive.getMessageContent(), getServerIdentity());

       // System.out.println(" WsCommunicationsCloudClientChannel - isValid = " + isValid);

        /*
         * if not valid signature
         */
        if (!isValid){
            throw new RuntimeException("Fermat Packet received has not a valid signature, go to close this connection maybe is compromise");
        }

    }

    /**
     * This method register a FermatJettyPacketProcessor object with this
     * server
     */
    public void registerFermatPacketProcessor(FermatPacketProcessor fermatPacketProcessor) {


        //Validate if a previous list created
        if (packetProcessorsRegister.containsKey(fermatPacketProcessor.getFermatPacketType())){

            /*
             * Add to the existing list
             */
            packetProcessorsRegister.get(fermatPacketProcessor.getFermatPacketType()).add(fermatPacketProcessor);

        }else{

            /*
             * Create a new list and add the fermatPacketProcessor
             */
            CopyOnWriteArrayList<FermatPacketProcessor> fermatPacketProcessorList = new CopyOnWriteArrayList<>();
            fermatPacketProcessorList.add(fermatPacketProcessor);

            /*
             * Add to the packetProcessorsRegister
             */
            packetProcessorsRegister.put(fermatPacketProcessor.getFermatPacketType(), fermatPacketProcessorList);
        }

    }

    /**
     * Get the Client Identity
     * @return ECCKeyPair
     */
    public ECCKeyPair getClientIdentity() {
        return clientIdentity;
    }

    /**
     * Get Temporal Identity
     * @return ECCKeyPair
     */
    public ECCKeyPair getTemporalIdentity() {
        return temporalIdentity;
    }

    /**
     * Get Server Identity
     *
     * @return String
     */
    public String getServerIdentity() {
        return serverIdentity;
    }

    /**
     * Set Server Identity
     * @param serverIdentity
     */
    public void setServerIdentity(String serverIdentity) {
        this.serverIdentity = serverIdentity;
    }

    /**
     * Clean all packet processors registered
     */
    public void cleanPacketProcessorsRegistered(){
        packetProcessorsRegister.clear();
    }


    /**
     * Get the PlatformComponentProfile
     *
     * @return PlatformComponentProfile
     */
    public PlatformComponentProfile getPlatformComponentProfile() {
        return platformComponentProfile;
    }

    /**
     * Set the PlatformComponentProfile
     * @param platformComponentProfile
     */
    public void setPlatformComponentProfile(PlatformComponentProfile platformComponentProfile) {
        this.platformComponentProfile = platformComponentProfile;
    }

    /**
     * Get the isActive value
     * @return boolean
     */
    public boolean isRegister() {
        return isRegister;
    }

    /**
     * Set the isActive
     * @param isRegister
     */
    public void setIsRegister(boolean isRegister) {
        this.isRegister = isRegister;
    }

    /**
     * Set the wsCommunicationsCloudClientSupervisorConnectionAgent
     * @param wsCommunicationsCloudClientSupervisorConnectionAgent
     */
    public void setWsCommunicationsCloudClientSupervisorConnectionAgent(WsCommunicationsCloudClientSupervisorConnectionAgent wsCommunicationsCloudClientSupervisorConnectionAgent) {
        this.wsCommunicationsCloudClientSupervisorConnectionAgent = wsCommunicationsCloudClientSupervisorConnectionAgent;
    }

    /**
     * Get the WsCommunicationsCloudClientConnection
     * @return WsCommunicationsCloudClientConnection
     */
    public WsCommunicationsCloudClientConnection getWsCommunicationsCloudClientConnection() {
        return wsCommunicationsCloudClientConnection;
    }

    /**
     * Get the EventManager
     * @return EventManager
     */
    public EventManager getEventManager() {
        return eventManager;
    }

    /**
     * Notify when cloud client component es registered,
     * this event is raise to show the message in a popup of the UI
     */
    public void riseCompleteClientComponentRegistrationNotificationEvent() {

        FermatEvent platformEvent = eventManager.getNewEvent(P2pEventType.COMPLETE_CLIENT_COMPONENT_REGISTRATION_NOTIFICATION);
        CompleteClientComponentRegistrationNotificationEvent event =  (CompleteClientComponentRegistrationNotificationEvent) platformEvent;
        event.setSource(EventSource.WS_COMMUNICATION_CLOUD_CLIENT_PLUGIN);
        event.setMessage("Cloud client communication, registered and established connection.");
        eventManager.raiseEvent(platformEvent);
    }

    /**
     * Notify when cloud client is disconnected
     */
    public void raiseClientConnectionCloseNotificationEvent() {

        System.out.println("WsCommunicationsCloudClientChannel - raiseClientConnectionCloseNotificationEvent");
        FermatEvent platformEvent = eventManager.getNewEvent(P2pEventType.CLIENT_CONNECTION_CLOSE);
        platformEvent.setSource(EventSource.WS_COMMUNICATION_CLOUD_CLIENT_PLUGIN);
        eventManager.raiseEvent(platformEvent);
        System.out.println("WsCommunicationsCloudClientChannel - Raised Event = P2pEventType.CLIENT_CONNECTION_CLOSE");
    }

    /**
     * Notify when cloud client is disconnected
     */
    public void raiseClientConnectionLooseNotificationEvent() {

        System.out.println("WsCommunicationsCloudClientChannel - raiseClientConnectionLooseNotificationEvent");
        FermatEvent platformEvent = eventManager.getNewEvent(P2pEventType.CLIENT_CONNECTION_LOOSE);
        platformEvent.setSource(EventSource.WS_COMMUNICATION_CLOUD_CLIENT_PLUGIN);
        eventManager.raiseEvent(platformEvent);
        System.out.println("WsCommunicationsCloudClientChannel - Raised Event = P2pEventType.CLIENT_CONNECTION_LOOSE");
    }

    /**
     * Get the IdentityPublicKey
     * @return String
     */
    public String getIdentityPublicKey(){
        return clientIdentity.getPublicKey();
    }
}