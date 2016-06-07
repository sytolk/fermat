package com.bitdubai.fermat_ccp_plugin.layer.crypto_transaction.outgoing_extra_user.developer.bitdubai.version_1.structure;

import com.bitdubai.fermat_api.FermatException;
import com.bitdubai.fermat_api.layer.all_definition.enums.Actors;
import com.bitdubai.fermat_api.layer.all_definition.enums.BlockchainNetworkType;
import com.bitdubai.fermat_api.layer.all_definition.enums.CryptoCurrency;
import com.bitdubai.fermat_api.layer.all_definition.enums.Plugins;
import com.bitdubai.fermat_api.layer.all_definition.enums.ReferenceWallet;
import com.bitdubai.fermat_api.layer.all_definition.money.CryptoAddress;
import com.bitdubai.fermat_api.layer.dmp_module.wallet_manager.CantLoadWalletsException;
import com.bitdubai.fermat_ccp_api.layer.basic_wallet.crypto_wallet.interfaces.CryptoWalletWallet;
import com.bitdubai.fermat_ccp_api.layer.basic_wallet.crypto_wallet.interfaces.CryptoWalletManager;
import com.bitdubai.fermat_ccp_api.layer.basic_wallet.loss_protected_wallet.interfaces.BitcoinLossProtectedWallet;
import com.bitdubai.fermat_ccp_api.layer.basic_wallet.loss_protected_wallet.interfaces.BitcoinLossProtectedWalletManager;
import com.bitdubai.fermat_ccp_api.layer.crypto_transaction.outgoing_extra_user.TransactionManager;
import com.bitdubai.fermat_ccp_api.layer.crypto_transaction.outgoing_extra_user.exceptions.CantSendFundsException;
import com.bitdubai.fermat_ccp_api.layer.crypto_transaction.outgoing_extra_user.exceptions.InsufficientFundsException;
import com.bitdubai.fermat_ccp_api.layer.basic_wallet.common.enums.BalanceType;
import com.bitdubai.fermat_ccp_api.layer.basic_wallet.common.exceptions.CantCalculateBalanceException;
import com.bitdubai.fermat_api.layer.osa_android.database_system.PluginDatabaseSystem;
import com.bitdubai.fermat_api.layer.osa_android.database_system.exceptions.CantInsertRecordException;
import com.bitdubai.fermat_api.layer.all_definition.common.system.interfaces.ErrorManager;
import com.bitdubai.fermat_api.layer.all_definition.common.system.interfaces.error_manager.enums.UnexpectedPluginExceptionSeverity;
import com.bitdubai.fermat_ccp_plugin.layer.crypto_transaction.outgoing_extra_user.developer.bitdubai.version_1.exceptions.CantInitializeDaoException;

import java.util.UUID;

/**
 * Created by eze on 2015.06.23..
 * Modified by lnacosta (laion.cj91@gmail.com) on 15/10/2015.
 */
public class OutgoingExtraUserTransactionManager implements TransactionManager {

    private final CryptoWalletManager cryptoWalletManager;
    private final ErrorManager         errorManager        ;
    private final PluginDatabaseSystem pluginDatabaseSystem;
    private final UUID                 pluginId            ;
    private BitcoinLossProtectedWalletManager bitcoinLossProtectedWalletManager;

    public OutgoingExtraUserTransactionManager(final CryptoWalletManager cryptoWalletManager,
                                               final ErrorManager         errorManager        ,
                                               final PluginDatabaseSystem pluginDatabaseSystem,
                                               final UUID                 pluginId            ,
                                               BitcoinLossProtectedWalletManager bitcoinLossProtectedWalletManager) {

        this.cryptoWalletManager = cryptoWalletManager;
        this.errorManager         = errorManager        ;
        this.pluginDatabaseSystem = pluginDatabaseSystem;
        this.pluginId             = pluginId            ;
        this.bitcoinLossProtectedWalletManager = bitcoinLossProtectedWalletManager;
    }

    /*
     * TransactionManager Interface methods implementation
     */
    @Override
    public void send(final String        walletPublicKey          ,
                     final CryptoAddress destinationAddress       ,
                     final long          cryptoAmount             ,
                     final String        notes                    ,
                     final String        deliveredByActorPublicKey,
                     final Actors        deliveredByActorType     ,
                     final String        deliveredToActorPublicKey,
                     final Actors        deliveredToActorType     ,
                     ReferenceWallet referenceWallet,
                     BlockchainNetworkType blockchainNetworkType,
                     final CryptoCurrency cryptoCurrency) throws InsufficientFundsException,
                                                                           CantSendFundsException    {
        /*
         * TODO: Create a class fir tge selection of the correct wallet
         *       We will have as parameter the walletPublicKey and walletType
         *       The class will have a reference to all the basicwallet managers
         *       implemented that could be a destination of the transactions managed
         *       by an extra user.
         */

        OutgoingExtraUserDao dao = new OutgoingExtraUserDao(errorManager, pluginDatabaseSystem, pluginId);
        long funds = 0;
        try {
            dao.initialize();
            CryptoWalletWallet cryptoWalletWallet;
            switch (referenceWallet) {
                case BASIC_WALLET_BITCOIN_WALLET:
                     cryptoWalletWallet = this.cryptoWalletManager.loadWallet(walletPublicKey);
                    funds = cryptoWalletWallet.getBalance(BalanceType.AVAILABLE).getBalance(blockchainNetworkType);
                    break;
                case BASIC_WALLET_FERMAT_WALLET:
                     cryptoWalletWallet = this.cryptoWalletManager.loadWallet(walletPublicKey);
                    funds = cryptoWalletWallet.getBalance(BalanceType.AVAILABLE).getBalance(blockchainNetworkType);
                    break;

                case BASIC_WALLET_LOSS_PROTECTED_WALLET:
                    BitcoinLossProtectedWallet lossProtectedWalletWallet = this.bitcoinLossProtectedWalletManager.loadWallet(walletPublicKey);
                    funds = lossProtectedWalletWallet.getBalance(BalanceType.AVAILABLE).getBalance(blockchainNetworkType);
                    break;
            }


            if (cryptoAmount > funds) {

                throw new InsufficientFundsException(
                        "We don't have enough funds",
                        null, "" +
                        "CryptoAmount: " + cryptoAmount + "\nBalance: " + funds,
                        "Many transactions were accepted before discounting from basic wallet balanace"
                );

            } else {

                dao.registerNewTransaction(walletPublicKey, destinationAddress, cryptoAmount, notes, deliveredByActorPublicKey, deliveredByActorType, deliveredToActorPublicKey,
                        deliveredToActorType, blockchainNetworkType,
                        cryptoCurrency);
            }
        } catch (InsufficientFundsException exception) {

            throw exception;
        } catch (CantInitializeDaoException e) {

            this.errorManager.reportUnexpectedPluginException(Plugins.BITDUBAI_OUTGOING_EXTRA_USER_TRANSACTION, UnexpectedPluginExceptionSeverity.DISABLES_SOME_FUNCTIONALITY_WITHIN_THIS_PLUGIN, e);
            throw new CantSendFundsException("I coundn't initialize dao", e, "Plug-in id: " + this.pluginId.toString(), "");
        } catch (CantLoadWalletsException e) {

            this.errorManager.reportUnexpectedPluginException(Plugins.BITDUBAI_OUTGOING_EXTRA_USER_TRANSACTION, UnexpectedPluginExceptionSeverity.DISABLES_SOME_FUNCTIONALITY_WITHIN_THIS_PLUGIN, e);
            throw new CantSendFundsException("I couldn't load the wallet", e, "walletPublicKey: " + walletPublicKey, "");
        } catch (CantCalculateBalanceException e) {

            this.errorManager.reportUnexpectedPluginException(Plugins.BITDUBAI_OUTGOING_EXTRA_USER_TRANSACTION, UnexpectedPluginExceptionSeverity.DISABLES_SOME_FUNCTIONALITY_WITHIN_THIS_PLUGIN, e);
            throw new CantSendFundsException("I couldn't calculate balance", e, "", "");
        } catch (CantInsertRecordException e) {

            this.errorManager.reportUnexpectedPluginException(Plugins.BITDUBAI_OUTGOING_EXTRA_USER_TRANSACTION, UnexpectedPluginExceptionSeverity.DISABLES_SOME_FUNCTIONALITY_WITHIN_THIS_PLUGIN, e);
            throw new CantSendFundsException("I couldn't insert new record", e, "", "");
        } catch (Exception exception){

            throw new CantSendFundsException(CantSendFundsException.DEFAULT_MESSAGE, FermatException.wrapException(exception), null, null);
        }

    }
}
