package com.bitdubai.fermat_bnk_plugin.layer.bank_money_transaction.deposit.developer.bitdubai.version_1.structure;

import com.bitdubai.fermat_api.CantStartPluginException;
import com.bitdubai.fermat_api.layer.all_definition.common.system.interfaces.error_manager.enums.UnexpectedPluginExceptionSeverity;
import com.bitdubai.fermat_api.layer.all_definition.enums.Plugins;
import com.bitdubai.fermat_api.layer.osa_android.database_system.PluginDatabaseSystem;
import com.bitdubai.fermat_bnk_api.all_definition.bank_money_transaction.BankTransaction;
import com.bitdubai.fermat_bnk_api.all_definition.bank_money_transaction.BankTransactionParameters;
import com.bitdubai.fermat_bnk_api.all_definition.enums.BalanceType;
import com.bitdubai.fermat_bnk_api.all_definition.enums.BankAccountType;
import com.bitdubai.fermat_bnk_api.all_definition.enums.BankOperationType;
import com.bitdubai.fermat_bnk_api.all_definition.enums.BankTransactionStatus;
import com.bitdubai.fermat_bnk_api.all_definition.enums.TransactionType;
import com.bitdubai.fermat_bnk_api.layer.bnk_bank_money_transaction.deposit.exceptions.CantMakeDepositTransactionException;
import com.bitdubai.fermat_bnk_api.layer.bnk_bank_money_transaction.deposit.interfaces.DepositManager;
import com.bitdubai.fermat_bnk_api.layer.bnk_wallet.bank_money.exceptions.CantRegisterCreditException;
import com.bitdubai.fermat_bnk_api.layer.bnk_wallet.bank_money.interfaces.BankMoneyWalletManager;
import com.bitdubai.fermat_bnk_plugin.layer.bank_money_transaction.deposit.developer.bitdubai.version_1.DepositBankMoneyTransactionPluginRoot;
import com.bitdubai.fermat_bnk_plugin.layer.bank_money_transaction.deposit.developer.bitdubai.version_1.database.DepositBankMoneyTransactionDao;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;


/**
 * Created by memo on 19/11/15.
 */
public class DepositBankMoneyTransactionManager implements DepositManager, Serializable {


    private DepositBankMoneyTransactionPluginRoot pluginRoot;
    private DepositBankMoneyTransactionDao depositBankMoneyTransactionDao;
    private BankMoneyWalletManager bankMoneyWallet;

    public DepositBankMoneyTransactionManager(UUID pluginId, PluginDatabaseSystem pluginDatabaseSystem, DepositBankMoneyTransactionPluginRoot pluginRoot, BankMoneyWalletManager bankMoneyWallet) throws CantStartPluginException {
        this.pluginRoot = pluginRoot;
        this.bankMoneyWallet = bankMoneyWallet;
        depositBankMoneyTransactionDao = new DepositBankMoneyTransactionDao(pluginId, pluginDatabaseSystem, pluginRoot);
        try {
            depositBankMoneyTransactionDao.initialize();
        } catch (Exception e) {
            pluginRoot.reportError(UnexpectedPluginExceptionSeverity.DISABLES_THIS_PLUGIN, e);
            throw new CantStartPluginException(Plugins.BITDUBAI_BNK_DEPOSIT_MONEY_TRANSACTION);
        }
    }


    @Override
    public BankTransaction makeDeposit(BankTransactionParameters bankTransactionParameters) throws CantMakeDepositTransactionException {
        depositBankMoneyTransactionDao.registerDepositTransaction(bankTransactionParameters);
        try {
            bankMoneyWallet.getAvailableBalance().credit(new BankMoneyTransactionRecordImpl(pluginRoot, UUID.randomUUID(), BalanceType.AVAILABLE.getCode(), TransactionType.CREDIT.getCode(), bankTransactionParameters.getAmount(), bankTransactionParameters.getCurrency().getCode(), BankOperationType.DEPOSIT.getCode(), "test_reference", null, bankTransactionParameters.getAccount(), BankAccountType.SAVINGS.getCode(), BigDecimal.ZERO, BigDecimal.ZERO, (new Date().getTime()), bankTransactionParameters.getMemo(), null));
            bankMoneyWallet.getBookBalance().credit(new BankMoneyTransactionRecordImpl(pluginRoot, UUID.randomUUID(), BalanceType.BOOK.getCode(), TransactionType.CREDIT.getCode(), bankTransactionParameters.getAmount(), bankTransactionParameters.getCurrency().getCode(), BankOperationType.DEPOSIT.getCode(), "test_reference", null, bankTransactionParameters.getAccount(), BankAccountType.SAVINGS.getCode(), BigDecimal.ZERO, BigDecimal.ZERO, (new Date().getTime()), bankTransactionParameters.getMemo(), null));
        } catch (CantRegisterCreditException e) {
            throw new CantMakeDepositTransactionException(CantRegisterCreditException.DEFAULT_MESSAGE, e, null, null);
        }
        return new BankTransactionImpl(bankTransactionParameters.getTransactionId(), bankTransactionParameters.getPublicKeyPlugin(), bankTransactionParameters.getPublicKeyWallet(),
                bankTransactionParameters.getAmount(), bankTransactionParameters.getAccount(), bankTransactionParameters.getCurrency(), bankTransactionParameters.getMemo(), BankOperationType.DEPOSIT, TransactionType.CREDIT, new Date().getTime(), BankTransactionStatus.CONFIRMED);

    }
}
