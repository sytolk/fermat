package com.bitdubai.fermat_cbp_plugin.layer.identity.crypto_customer.developer.bitdubai.version_1.structure;

import com.bitdubai.fermat_api.FermatException;
import com.bitdubai.fermat_api.layer.all_definition.common.system.interfaces.error_manager.enums.UnexpectedPluginExceptionSeverity;
import com.bitdubai.fermat_api.layer.all_definition.crypto.asymmetric.AsymmetricCryptography;
import com.bitdubai.fermat_api.layer.all_definition.crypto.asymmetric.interfaces.KeyPair;
import com.bitdubai.fermat_api.layer.osa_android.broadcaster.Broadcaster;
import com.bitdubai.fermat_api.layer.osa_android.broadcaster.BroadcasterType;
import com.bitdubai.fermat_cbp_api.all_definition.exceptions.CantCreateNewDeveloperException;
import com.bitdubai.fermat_cbp_api.layer.actor_network_service.crypto_customer.exceptions.CantExposeIdentitiesException;
import com.bitdubai.fermat_cbp_api.layer.actor_network_service.crypto_customer.exceptions.CantExposeIdentityException;
import com.bitdubai.fermat_cbp_api.layer.actor_network_service.crypto_customer.interfaces.CryptoCustomerManager;
import com.bitdubai.fermat_cbp_api.layer.actor_network_service.crypto_customer.utils.CryptoCustomerExposingData;
import com.bitdubai.fermat_cbp_api.layer.identity.crypto_broker.exceptions.CantUpdateCustomerIdentityException;
import com.bitdubai.fermat_cbp_api.layer.identity.crypto_customer.exceptions.CantCreateCryptoCustomerIdentityException;
import com.bitdubai.fermat_cbp_api.layer.identity.crypto_customer.exceptions.CantGetCryptoCustomerIdentityException;
import com.bitdubai.fermat_cbp_api.layer.identity.crypto_customer.exceptions.CantHideIdentityException;
import com.bitdubai.fermat_cbp_api.layer.identity.crypto_customer.exceptions.CantListCryptoCustomerIdentityException;
import com.bitdubai.fermat_cbp_api.layer.identity.crypto_customer.exceptions.CantPublishIdentityException;
import com.bitdubai.fermat_cbp_api.layer.identity.crypto_customer.exceptions.IdentityNotFoundException;
import com.bitdubai.fermat_cbp_api.layer.identity.crypto_customer.interfaces.CryptoCustomerIdentity;
import com.bitdubai.fermat_cbp_api.layer.identity.crypto_customer.interfaces.CryptoCustomerIdentityManager;
import com.bitdubai.fermat_cbp_plugin.layer.identity.crypto_customer.developer.bitdubai.version_1.CryptoCustomerIdentityPluginRoot;
import com.bitdubai.fermat_cbp_plugin.layer.identity.crypto_customer.developer.bitdubai.version_1.database.CryptoCustomerIdentityDatabaseDao;
import com.bitdubai.fermat_cbp_plugin.layer.identity.crypto_customer.developer.bitdubai.version_1.exceptions.CantChangeExposureLevelException;
import com.bitdubai.fermat_cbp_plugin.layer.identity.crypto_customer.developer.bitdubai.version_1.exceptions.CantExposeActorIdentitiesException;
import com.bitdubai.fermat_cbp_plugin.layer.identity.crypto_customer.developer.bitdubai.version_1.exceptions.CantExposeActorIdentityException;
import com.bitdubai.fermat_cbp_plugin.layer.identity.crypto_customer.developer.bitdubai.version_1.exceptions.CantGetIdentityException;
import com.bitdubai.fermat_cbp_plugin.layer.identity.crypto_customer.developer.bitdubai.version_1.exceptions.CantListCryptoCustomerIdentitiesException;
import com.bitdubai.fermat_pip_api.layer.user.device_user.exceptions.CantGetLoggedInDeviceUserException;
import com.bitdubai.fermat_pip_api.layer.user.device_user.interfaces.DeviceUser;
import com.bitdubai.fermat_pip_api.layer.user.device_user.interfaces.DeviceUserManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Yordin Alayn on 12.05.16.
 */
public class CryptoCustomerIdentityManagerImpl implements CryptoCustomerIdentityManager{
    
    private CryptoCustomerIdentityDatabaseDao   cryptoCustomerIdentityDatabaseDao;
    private DeviceUserManager                   deviceUserManager;
    private Broadcaster                         broadcaster;
    private CryptoCustomerManager               cryptoCustomerANSManager;
    private CryptoCustomerIdentityPluginRoot    pluginRoot;
    
    public CryptoCustomerIdentityManagerImpl(
            CryptoCustomerIdentityDatabaseDao   cryptoCustomerIdentityDatabaseDao,
            DeviceUserManager                   deviceUserManager,
            Broadcaster                         broadcaster,
            CryptoCustomerManager               cryptoCustomerANSManager,
            CryptoCustomerIdentityPluginRoot    pluginRoot
    ){
        this.cryptoCustomerIdentityDatabaseDao  = cryptoCustomerIdentityDatabaseDao;
        this.deviceUserManager                  = deviceUserManager;
        this.broadcaster                        = broadcaster;
        this.cryptoCustomerANSManager           = cryptoCustomerANSManager;
        this.pluginRoot                         = pluginRoot;
    }

    /*CryptoCustomerIdentityManager Interface implementation.*/
    public List<CryptoCustomerIdentity> listAllCryptoCustomerFromCurrentDeviceUser() throws CantListCryptoCustomerIdentityException {
        try {
            List<CryptoCustomerIdentity> cryptoCustomerIdentityList1;
            DeviceUser loggedUser = deviceUserManager.getLoggedInDeviceUser();
            cryptoCustomerIdentityList1 = cryptoCustomerIdentityDatabaseDao.getAllCryptoCustomerIdentitiesFromCurrentDeviceUser(loggedUser);
            return cryptoCustomerIdentityList1;
        } catch (CantGetLoggedInDeviceUserException e) {
            pluginRoot.reportError(UnexpectedPluginExceptionSeverity.DISABLES_SOME_FUNCTIONALITY_WITHIN_THIS_PLUGIN, e);
            throw new CantListCryptoCustomerIdentityException("CAN'T GET CRYPTO CUSTOMER IDENTITIES", e, "Error get logged user device", "");
        } catch (CantListCryptoCustomerIdentitiesException e) {
            pluginRoot.reportError(UnexpectedPluginExceptionSeverity.DISABLES_SOME_FUNCTIONALITY_WITHIN_THIS_PLUGIN, e);
            throw new CantListCryptoCustomerIdentityException("CAN'T GET CRYPTO CUSTOMER IDENTITIES", e, "", "");
        } catch (Exception e) {
            pluginRoot.reportError(UnexpectedPluginExceptionSeverity.DISABLES_SOME_FUNCTIONALITY_WITHIN_THIS_PLUGIN, e);
            throw new CantListCryptoCustomerIdentityException("CAN'T GET CRYPTO CUSTOMER IDENTITIES", FermatException.wrapException(e), "", "");
        }
    }

    public CryptoCustomerIdentity getCryptoCustomerIdentity(String publickey) throws CantGetCryptoCustomerIdentityException {
        try {
            return cryptoCustomerIdentityDatabaseDao.getIdentity(publickey);
        } catch (CantGetIdentityException e) {
            pluginRoot.reportError(UnexpectedPluginExceptionSeverity.DISABLES_SOME_FUNCTIONALITY_WITHIN_THIS_PLUGIN, e);
            throw new CantGetCryptoCustomerIdentityException("CAN'T GET CRYPTO CUSTOMER IDENTITIE", e, "Error get Identity", "");
        } catch (IdentityNotFoundException e) {
            pluginRoot.reportError(UnexpectedPluginExceptionSeverity.DISABLES_SOME_FUNCTIONALITY_WITHIN_THIS_PLUGIN, e);
            pluginRoot.reportError(UnexpectedPluginExceptionSeverity.DISABLES_SOME_FUNCTIONALITY_WITHIN_THIS_PLUGIN, e);
            throw new CantGetCryptoCustomerIdentityException("CAN'T GET CRYPTO CUSTOMER IDENTITIE", e, "", "");
        } catch (Exception e) {
            pluginRoot.reportError(UnexpectedPluginExceptionSeverity.DISABLES_SOME_FUNCTIONALITY_WITHIN_THIS_PLUGIN, e);
            throw new CantGetCryptoCustomerIdentityException("CAN'T GET CRYPTO CUSTOMER IDENTITIE", FermatException.wrapException(e), "", "");
        }
    }

    public CryptoCustomerIdentity createCryptoCustomerIdentity(String alias, byte[] profileImage) throws CantCreateCryptoCustomerIdentityException {
        try {
            DeviceUser loggedUser = deviceUserManager.getLoggedInDeviceUser();
            KeyPair keyPair = AsymmetricCryptography.generateECCKeyPair();
            // TODO BY DEFAULT THE CUSTOMER IS PUBLISHED
            CryptoCustomerIdentity cryptoCustomer = new CryptoCustomerIdentityImpl(alias, keyPair.getPrivateKey(), keyPair.getPublicKey(), profileImage, true);
            cryptoCustomerIdentityDatabaseDao.createNewCryptoCustomerIdentity(cryptoCustomer, keyPair.getPrivateKey(), loggedUser);

            broadcaster.publish(BroadcasterType.UPDATE_VIEW, "cambios_en_el_identity_customer_creado");

            return cryptoCustomer;

        } catch (CantGetLoggedInDeviceUserException e) {
            pluginRoot.reportError(UnexpectedPluginExceptionSeverity.DISABLES_SOME_FUNCTIONALITY_WITHIN_THIS_PLUGIN, e);
            throw new CantCreateCryptoCustomerIdentityException("CAN'T CREATE NEW CRYPTO CUSTOMER IDENTITY", e, "Error getting current logged in device user", "");
        } catch (CantCreateNewDeveloperException e) {
            pluginRoot.reportError(UnexpectedPluginExceptionSeverity.DISABLES_SOME_FUNCTIONALITY_WITHIN_THIS_PLUGIN, e);
            throw new CantCreateCryptoCustomerIdentityException("CAN'T CREATE NEW CRYPTO CUSTOMER IDENTITY", e, "Error save user on database", "");
        } catch (Exception e) {
            pluginRoot.reportError(UnexpectedPluginExceptionSeverity.DISABLES_SOME_FUNCTIONALITY_WITHIN_THIS_PLUGIN, e);
            throw new CantCreateCryptoCustomerIdentityException("CAN'T CREATE NEW CRYPTO CUSTOMER IDENTITY", FermatException.wrapException(e), "", "");
        }

    }

    @Override
    public void updateCryptoCustomerIdentity(String alias, String publicKey, byte[] imageProfile) throws CantUpdateCustomerIdentityException {
        cryptoCustomerIdentityDatabaseDao.updateCryptoCustomerIdentity(alias, publicKey, imageProfile);

        try {
            CryptoCustomerIdentity customer = cryptoCustomerIdentityDatabaseDao.getIdentity(publicKey);
            if( customer.isPublished() ){
                cryptoCustomerANSManager.updateIdentity(new CryptoCustomerExposingData(publicKey, alias, imageProfile));
            }
        } catch (CantGetIdentityException e) {

            pluginRoot.reportError(UnexpectedPluginExceptionSeverity.DISABLES_SOME_FUNCTIONALITY_WITHIN_THIS_PLUGIN, e);
            throw new CantUpdateCustomerIdentityException("CAN'T GET CRYPTO CUSTOMER IDENTITY", FermatException.wrapException(e), "", "");

        } catch (IdentityNotFoundException e) {

            pluginRoot.reportError(UnexpectedPluginExceptionSeverity.DISABLES_SOME_FUNCTIONALITY_WITHIN_THIS_PLUGIN, e);
            throw new CantUpdateCustomerIdentityException("CRYPTO CUSTOMER IDENTITY NOT FOUND", FermatException.wrapException(e), "", "");

        } catch (CantExposeIdentityException e) {

            pluginRoot.reportError(UnexpectedPluginExceptionSeverity.DISABLES_SOME_FUNCTIONALITY_WITHIN_THIS_PLUGIN, e);
            throw new CantUpdateCustomerIdentityException("CAN'T EXPOSE CRYPTO CUSTOMER IDENTITY", FermatException.wrapException(e), "", "");

        }
    }

    @Override
    public void publishIdentity(String publicKey) throws CantPublishIdentityException, IdentityNotFoundException {

        try {

            cryptoCustomerIdentityDatabaseDao.changeExposureLevel(publicKey, true);

            exposeIdentity(cryptoCustomerIdentityDatabaseDao.getIdentity(publicKey));

        } catch (final CantExposeActorIdentityException e) {

            pluginRoot.reportError(UnexpectedPluginExceptionSeverity.DISABLES_SOME_FUNCTIONALITY_WITHIN_THIS_PLUGIN, e);
            throw new CantPublishIdentityException(e, "", "Cant expose actor identity.");
        } catch (final CantChangeExposureLevelException e) {

            pluginRoot.reportError(UnexpectedPluginExceptionSeverity.DISABLES_SOME_FUNCTIONALITY_WITHIN_THIS_PLUGIN, e);
            throw new CantPublishIdentityException(e, "", "There was a problem trying to change the exposure level.");
        } catch (final IdentityNotFoundException e) {

            pluginRoot.reportError(UnexpectedPluginExceptionSeverity.DISABLES_SOME_FUNCTIONALITY_WITHIN_THIS_PLUGIN, e);
            throw e;
        } catch (final CantGetIdentityException e) {

            pluginRoot.reportError(UnexpectedPluginExceptionSeverity.DISABLES_SOME_FUNCTIONALITY_WITHIN_THIS_PLUGIN, e);
            throw new CantPublishIdentityException(e, "", "There was a problem trying to get the identity.");
        } catch (final Exception e) {

            pluginRoot.reportError(UnexpectedPluginExceptionSeverity.DISABLES_SOME_FUNCTIONALITY_WITHIN_THIS_PLUGIN, e);
            throw new CantPublishIdentityException(e, "", "Unhandled Exception.");
        }
    }

    @Override
    public void hideIdentity(String publicKey) throws CantHideIdentityException, IdentityNotFoundException {

        try {

            cryptoCustomerIdentityDatabaseDao.changeExposureLevel(publicKey, false);

            // TODO actor network service hide identity?

        } catch (final CantChangeExposureLevelException e) {

            pluginRoot.reportError(UnexpectedPluginExceptionSeverity.DISABLES_SOME_FUNCTIONALITY_WITHIN_THIS_PLUGIN, e);
            throw new CantHideIdentityException(e, "", "There was a problem trying to change the exposure level.");
        } catch (final IdentityNotFoundException e) {

            pluginRoot.reportError(UnexpectedPluginExceptionSeverity.DISABLES_SOME_FUNCTIONALITY_WITHIN_THIS_PLUGIN, e);
            throw e;
        } catch (final Exception e) {

            pluginRoot.reportError(UnexpectedPluginExceptionSeverity.DISABLES_SOME_FUNCTIONALITY_WITHIN_THIS_PLUGIN, e);
            throw new CantHideIdentityException(e, "", "Unhandled Exception.");
        }
    }

    private void exposeIdentity(final CryptoCustomerIdentity identity) throws CantExposeActorIdentityException {

        try {

            cryptoCustomerANSManager.exposeIdentity(new CryptoCustomerExposingData(identity.getPublicKey(), identity.getAlias(), identity.getProfileImage()));

        } catch (final CantExposeIdentityException e) {

            pluginRoot.reportError(UnexpectedPluginExceptionSeverity.DISABLES_SOME_FUNCTIONALITY_WITHIN_THIS_PLUGIN, e);
            throw new CantExposeActorIdentityException(e, "", "Problem exposing identity.");
        }
    }
}
