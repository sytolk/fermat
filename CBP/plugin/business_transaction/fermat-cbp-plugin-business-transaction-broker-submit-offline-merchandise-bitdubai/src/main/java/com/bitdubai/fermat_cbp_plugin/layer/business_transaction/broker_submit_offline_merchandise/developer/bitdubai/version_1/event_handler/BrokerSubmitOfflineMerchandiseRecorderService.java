package com.bitdubai.fermat_cbp_plugin.layer.business_transaction.broker_submit_offline_merchandise.developer.bitdubai.version_1.event_handler;

import com.bitdubai.fermat_api.FermatException;
import com.bitdubai.fermat_api.layer.all_definition.enums.Plugins;
import com.bitdubai.fermat_api.layer.all_definition.enums.ServiceStatus;
import com.bitdubai.fermat_api.layer.all_definition.events.interfaces.FermatEventHandler;
import com.bitdubai.fermat_api.layer.all_definition.events.interfaces.FermatEventListener;
import com.bitdubai.fermat_cbp_api.all_definition.events.CBPService;
import com.bitdubai.fermat_cbp_api.all_definition.events.enums.EventType;
import com.bitdubai.fermat_cbp_api.all_definition.exceptions.CantSaveEventException;
import com.bitdubai.fermat_cbp_api.all_definition.exceptions.CantSetObjectException;
import com.bitdubai.fermat_cbp_api.all_definition.exceptions.CantStartServiceException;
import com.bitdubai.fermat_cbp_api.layer.network_service.transaction_transmission.events.IncomingConfirmBusinessTransactionResponse;
import com.bitdubai.fermat_cbp_api.layer.network_service.transaction_transmission.events.IncomingNewContractStatusUpdate;
import com.bitdubai.fermat_cbp_plugin.layer.business_transaction.broker_submit_offline_merchandise.developer.bitdubai.version_1.BrokerSubmitOfflineMerchandisePluginRoot;
import com.bitdubai.fermat_cbp_plugin.layer.business_transaction.broker_submit_offline_merchandise.developer.bitdubai.version_1.database.BrokerSubmitOfflineMerchandiseBusinessTransactionDao;
import com.bitdubai.fermat_pip_api.layer.platform_service.event_manager.interfaces.EventManager;

import java.util.ArrayList;
import java.util.List;

import static com.bitdubai.fermat_api.layer.all_definition.common.system.interfaces.error_manager.enums.UnexpectedPluginExceptionSeverity.DISABLES_SOME_FUNCTIONALITY_WITHIN_THIS_PLUGIN;
import static com.bitdubai.fermat_api.layer.all_definition.common.system.interfaces.error_manager.enums.UnexpectedPluginExceptionSeverity.DISABLES_THIS_PLUGIN;


/**
 * Created by Manuel Perez (darkpriestrelative@gmail.com) on 20/12/15.
 */
public class BrokerSubmitOfflineMerchandiseRecorderService implements CBPService {
    /**
     * DealsWithEvents Interface member variables.
     */
    private EventManager eventManager;
    private List<FermatEventListener> listenersAdded = new ArrayList<>();
    BrokerSubmitOfflineMerchandiseBusinessTransactionDao brokerSubmitOfflineMerchandiseBusinessTransactionDao;
    private BrokerSubmitOfflineMerchandisePluginRoot pluginRoot;
    /**
     * TransactionService Interface member variables.
     */
    private ServiceStatus serviceStatus = ServiceStatus.CREATED;

    public BrokerSubmitOfflineMerchandiseRecorderService(
            BrokerSubmitOfflineMerchandiseBusinessTransactionDao brokerSubmitOfflineMerchandiseBusinessTransactionDao,
            EventManager eventManager,
            BrokerSubmitOfflineMerchandisePluginRoot pluginRoot) throws CantStartServiceException {
        try {
            this.pluginRoot = pluginRoot;
            setDatabaseDao(brokerSubmitOfflineMerchandiseBusinessTransactionDao);
            setEventManager(eventManager);
        } catch (CantSetObjectException exception) {
            this.pluginRoot.reportError(DISABLES_SOME_FUNCTIONALITY_WITHIN_THIS_PLUGIN, exception);
            throw new CantStartServiceException(exception,
                    "Cannot set the submit offline merchandise recorder service",
                    "The database handler is null");
        } catch (Exception exception) {
            this.pluginRoot.reportError(DISABLES_THIS_PLUGIN, exception);
            throw new CantStartServiceException(CantStartServiceException.DEFAULT_MESSAGE, FermatException.wrapException(exception),
                    "Cannot set the submit offline merchandise recorder service",
                    "Unexpected error");
        }
    }

    private void setDatabaseDao(BrokerSubmitOfflineMerchandiseBusinessTransactionDao brokerSubmitOnlineMerchandiseBusinessTransactionDao)
            throws CantSetObjectException {
        if (brokerSubmitOnlineMerchandiseBusinessTransactionDao == null) {
            throw new CantSetObjectException("The BrokerSubmitOnlineMerchandiseBusinessTransactionDao is null");
        }
        this.brokerSubmitOfflineMerchandiseBusinessTransactionDao = brokerSubmitOnlineMerchandiseBusinessTransactionDao;
    }

    public void setEventManager(EventManager eventManager) {
        this.eventManager = eventManager;
    }

    public void incomingNewContractStatusUpdateEventHandler(IncomingNewContractStatusUpdate event) throws CantSaveEventException {
        try {
            //Logger LOG = Logger.getGlobal();
            //LOG.info("EVENT TEST, I GOT AN EVENT:\n"+event);
            if (event.getRemoteBusinessTransaction().getCode().equals(Plugins.BROKER_SUBMIT_OFFLINE_MERCHANDISE.getCode())) {
                this.brokerSubmitOfflineMerchandiseBusinessTransactionDao.saveNewEvent(event.getEventType().getCode(), event.getSource().getCode());
                //LOG.info("CHECK THE DATABASE");
            }
        } catch (CantSaveEventException exception) {
            this.pluginRoot.reportError(DISABLES_THIS_PLUGIN, exception);
            throw new CantSaveEventException(CantSaveEventException.DEFAULT_MESSAGE, exception,
                    "incoming new Contract Status Update Event Handler CantSaveException", "");
        } catch (Exception exception) {
            this.pluginRoot.reportError(DISABLES_THIS_PLUGIN, exception);
            throw new CantSaveEventException(CantSaveEventException.DEFAULT_MESSAGE, exception, "Unexpected error", "Check the cause");
        }
    }

    public void incomingConfirmBusinessTransactionResponse(IncomingConfirmBusinessTransactionResponse event) throws CantSaveEventException {
        try {
            //Logger LOG = Logger.getGlobal();
            //LOG.info("EVENT TEST, I GOT AN EVENT:\n"+event);
            if (event.getRemoteBusinessTransaction().getCode().equals(Plugins.BROKER_SUBMIT_OFFLINE_MERCHANDISE.getCode())) {
                this.brokerSubmitOfflineMerchandiseBusinessTransactionDao.saveNewEvent(event.getEventType().getCode(), event.getSource().getCode());
                //LOG.info("CHECK THE DATABASE");
            }
        } catch (CantSaveEventException exception) {
            this.pluginRoot.reportError(DISABLES_THIS_PLUGIN, exception);
            throw new CantSaveEventException(CantSaveEventException.DEFAULT_MESSAGE, exception,
                    "incoming Confirm Business Transaction Response CantSaveException", "");
        } catch (Exception exception) {
            this.pluginRoot.reportError(DISABLES_THIS_PLUGIN, exception);
            throw new CantSaveEventException(CantSaveEventException.DEFAULT_MESSAGE, exception, "Unexpected error", "Check the cause");
        }
    }

    @Override
    public void start() throws CantStartServiceException {
        try {
            /**
             * I will initialize the handling of com.bitdubai.platform events.
             */
            FermatEventListener fermatEventListener;
            FermatEventHandler fermatEventHandler;

            fermatEventListener = eventManager.getNewListener(EventType.INCOMING_NEW_CONTRACT_STATUS_UPDATE);
            fermatEventHandler = new IncomingNewContractStatusUpdateEventHandler();
            ((IncomingNewContractStatusUpdateEventHandler) fermatEventHandler).setBrokerSubmitOfflineMerchandiseRecorderService(this);
            fermatEventListener.setEventHandler(fermatEventHandler);
            eventManager.addListener(fermatEventListener);
            listenersAdded.add(fermatEventListener);

            fermatEventListener = eventManager.getNewListener(EventType.INCOMING_CONFIRM_BUSINESS_TRANSACTION_RESPONSE);
            fermatEventHandler = new IncomingConfirmBusinessTransactionResponseEventHandler();
            ((IncomingConfirmBusinessTransactionResponseEventHandler) fermatEventHandler).setBrokerSubmitOfflineMerchandiseRecorderService(this);
            fermatEventListener.setEventHandler(fermatEventHandler);
            eventManager.addListener(fermatEventListener);
            listenersAdded.add(fermatEventListener);

            this.serviceStatus = ServiceStatus.STARTED;
        } catch (CantSetObjectException exception) {
            this.pluginRoot.reportError(DISABLES_THIS_PLUGIN, exception);
            throw new CantStartServiceException(
                    exception,
                    "Starting the BrokerSubmitOfflineMerchandiseRecorderService",
                    "The BrokerSubmitOfflineMerchandiseRecorderService is probably null");
        } catch (Exception exception) {
            this.pluginRoot.reportError(DISABLES_THIS_PLUGIN, exception);
            throw new CantStartServiceException(CantStartServiceException.DEFAULT_MESSAGE, exception,
                    "Starting the CustomerOnlinePaymentRecorderService",
                    "Unexpected error");
        }

    }

    @Override
    public void stop() {
        try {
            removeRegisteredListeners();
            this.serviceStatus = ServiceStatus.STOPPED;
        } catch (Exception exception) {
            this.pluginRoot.reportError(DISABLES_SOME_FUNCTIONALITY_WITHIN_THIS_PLUGIN, FermatException.wrapException(exception));
        }
    }

    private void removeRegisteredListeners() {
        for (FermatEventListener fermatEventListener : listenersAdded) {
            eventManager.removeListener(fermatEventListener);
        }
        listenersAdded.clear();
    }

    @Override
    public ServiceStatus getStatus() {
        return this.serviceStatus;
    }
}

