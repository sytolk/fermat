package com.bitdubai.fermat_cbp_plugin.layer.business_transaction.customer_online_payment.developer.bitdubai.version_1.database.CustomerOnlinePaymentBusinessTransactionDaoTest;

import com.bitdubai.fermat_api.layer.osa_android.database_system.Database;
import com.bitdubai.fermat_api.layer.osa_android.database_system.DatabaseTable;
import com.bitdubai.fermat_api.layer.osa_android.database_system.DatabaseTableRecord;
import com.bitdubai.fermat_api.layer.osa_android.database_system.PluginDatabaseSystem;
import com.bitdubai.fermat_cbp_api.all_definition.events.enums.EventStatus;
import com.bitdubai.fermat_cbp_api.all_definition.exceptions.UnexpectedResultReturnedFromDatabaseException;
import com.bitdubai.fermat_cbp_plugin.layer.business_transaction.customer_online_payment.developer.bitdubai.version_1.database.CustomerOnlinePaymentBusinessTransactionDao;
import com.bitdubai.fermat_cbp_plugin.layer.business_transaction.customer_online_payment.developer.bitdubai.version_1.database.CustomerOnlinePaymentBusinessTransactionDatabaseConstants;
import com.bitdubai.fermat_api.layer.all_definition.common.system.interfaces.ErrorManager;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by alexander jimenez (alex_jimenez76@hotmail.com) on 25/02/16.
 */
public class updateEventStatusTest {
    private CustomerOnlinePaymentBusinessTransactionDao customerOnlinePaymentBusinessTransactionDao;
    @Mock
    PluginDatabaseSystem pluginDatabaseSystem;
    @Mock
    Database mockDatabase;
    @Mock
    ErrorManager errorManager;
    @Mock
    DatabaseTable databaseTable;
    @Mock
    DatabaseTableRecord databaseTableRecord;
    @Mock
    List<DatabaseTableRecord> databaseTableRecordList;
    private UUID testId;
    @Before
    public void setup()throws Exception{
        testId = UUID.randomUUID();
        MockitoAnnotations.initMocks(this);
        customerOnlinePaymentBusinessTransactionDao = new CustomerOnlinePaymentBusinessTransactionDao(
                pluginDatabaseSystem,
                testId,
                mockDatabase,
                errorManager);
        when(databaseTable.getRecords()).thenReturn(databaseTableRecordList);
        when(databaseTableRecordList.get(0)).thenReturn(databaseTableRecord);
        doNothing().when(databaseTableRecord).setStringValue(
                CustomerOnlinePaymentBusinessTransactionDatabaseConstants.ONLINE_PAYMENT_EVENTS_RECORDED_STATUS_COLUMN_NAME,
                EventStatus.PENDING.getCode());
        doNothing().when(databaseTable).updateRecord(databaseTableRecord);
    }

    @Test
    public void updateEventStatusTest()throws Exception{
        when(mockDatabase.getTable(
               CustomerOnlinePaymentBusinessTransactionDatabaseConstants.ONLINE_PAYMENT_EVENTS_RECORDED_TABLE_NAME
        )).thenReturn(databaseTable);
        customerOnlinePaymentBusinessTransactionDao.updateEventStatus("EventID", EventStatus.PENDING);
        verify(databaseTable,times(1)).updateRecord(databaseTableRecord);
    }
    @Test(expected = UnexpectedResultReturnedFromDatabaseException.class)
    public void updateEventStatusTest_Should_Throw_Exception()throws Exception{
        customerOnlinePaymentBusinessTransactionDao.updateEventStatus("EventID", EventStatus.PENDING);
    }
}
