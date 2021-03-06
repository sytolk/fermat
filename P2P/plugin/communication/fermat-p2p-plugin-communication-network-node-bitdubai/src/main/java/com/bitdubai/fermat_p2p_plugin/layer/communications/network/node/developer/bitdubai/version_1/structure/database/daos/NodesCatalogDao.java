/*
 * @#NodesCatalogDao.java - 2015
 * Copyright bitDubai.com., All rights reserved.
 * You may not modify, use, reproduce or distribute this software.
 * BITDUBAI/CONFIDENTIAL
 */
package com.bitdubai.fermat_p2p_plugin.layer.communications.network.node.developer.bitdubai.version_1.structure.database.daos;

import com.bitdubai.fermat_api.layer.all_definition.exceptions.InvalidParameterException;
import com.bitdubai.fermat_api.layer.osa_android.database_system.Database;
import com.bitdubai.fermat_api.layer.osa_android.database_system.DatabaseFilterOrder;
import com.bitdubai.fermat_api.layer.osa_android.database_system.DatabaseFilterType;
import com.bitdubai.fermat_api.layer.osa_android.database_system.DatabaseTable;
import com.bitdubai.fermat_api.layer.osa_android.database_system.DatabaseTableRecord;
import com.bitdubai.fermat_api.layer.osa_android.database_system.exceptions.CantLoadTableToMemoryException;
import com.bitdubai.fermat_p2p_plugin.layer.communications.network.node.developer.bitdubai.version_1.structure.database.CommunicationsNetworkNodeP2PDatabaseConstants;
import com.bitdubai.fermat_p2p_plugin.layer.communications.network.node.developer.bitdubai.version_1.structure.entities.NodesCatalog;
import com.bitdubai.fermat_p2p_plugin.layer.communications.network.node.developer.bitdubai.version_1.structure.exceptions.CantReadRecordDataBaseException;

import java.util.ArrayList;
import java.util.List;

/**
 * The Class <code>com.bitdubai.fermat_p2p_plugin.layer.communications.network.node.developer.bitdubai.version_1.structure.database.daos.NodesCatalogDao</code>
 * <p/>
 * Created by Roberto Requena - (rart3001@gmail.com) on 23/11/15.
 *
 * @version 1.0
 * @since Java JDK 1.7
 */
public class NodesCatalogDao  extends AbstractBaseDao<NodesCatalog> {

    /**
     * Constructor with parameter
     *
     * @param dataBase
     */
    public NodesCatalogDao(Database dataBase) {
        super(dataBase, CommunicationsNetworkNodeP2PDatabaseConstants.NODES_CATALOG_TABLE_NAME, CommunicationsNetworkNodeP2PDatabaseConstants.NODES_CATALOG_IDENTITY_PUBLIC_KEY_COLUMN_NAME );
    }

    /**
     * (non-javadoc)
     * @see AbstractBaseDao#getEntityFromDatabaseTableRecord(DatabaseTableRecord)
     */
    @Override
    protected NodesCatalog getEntityFromDatabaseTableRecord(DatabaseTableRecord record) throws InvalidParameterException {

        NodesCatalog entity = new NodesCatalog();

        entity.setLastConnectionTimestamp(getTimestampFromLongValue(record.getLongValue(CommunicationsNetworkNodeP2PDatabaseConstants.NODES_CATALOG_LAST_CONNECTION_TIMESTAMP_COLUMN_NAME)));
        entity.setDefaultPort(record.getIntegerValue(CommunicationsNetworkNodeP2PDatabaseConstants.NODES_CATALOG_DEFAULT_PORT_COLUMN_NAME));
        entity.setIdentityPublicKey(record.getStringValue(CommunicationsNetworkNodeP2PDatabaseConstants.NODES_CATALOG_IDENTITY_PUBLIC_KEY_COLUMN_NAME));
        entity.setIp(record.getStringValue(CommunicationsNetworkNodeP2PDatabaseConstants.NODES_CATALOG_IP_COLUMN_NAME));
        entity.setName(record.getStringValue(CommunicationsNetworkNodeP2PDatabaseConstants.NODES_CATALOG_NAME_COLUMN_NAME));
        entity.setLastLocation(record.getDoubleValue(CommunicationsNetworkNodeP2PDatabaseConstants.NODES_CATALOG_LAST_LATITUDE_COLUMN_NAME), record.getDoubleValue(CommunicationsNetworkNodeP2PDatabaseConstants.NODES_CATALOG_LAST_LONGITUDE_COLUMN_NAME));
        entity.setRegisteredTimestamp(getTimestampFromLongValue(record.getLongValue(CommunicationsNetworkNodeP2PDatabaseConstants.NODES_CATALOG_REGISTERED_TIMESTAMP_COLUMN_NAME)));
        entity.setLateNotificationsCounter(record.getIntegerValue(CommunicationsNetworkNodeP2PDatabaseConstants.NODES_CATALOG_LATE_NOTIFICATION_COUNTER_COLUMN_NAME));
        entity.setOfflineCounter(record.getIntegerValue(CommunicationsNetworkNodeP2PDatabaseConstants.NODES_CATALOG_OFFLINE_COUNTER_COLUMN_NAME));
        
        return entity;
    }

    /**
     * (non-javadoc)
     * @see AbstractBaseDao#getDatabaseTableRecordFromEntity
     */
    @Override
    protected DatabaseTableRecord getDatabaseTableRecordFromEntity(NodesCatalog entity) {

        DatabaseTableRecord databaseTableRecord = getDatabaseTable().getEmptyRecord();

        databaseTableRecord.setLongValue(CommunicationsNetworkNodeP2PDatabaseConstants.NODES_CATALOG_LAST_CONNECTION_TIMESTAMP_COLUMN_NAME, getLongValueFromTimestamp(entity.getLastConnectionTimestamp()));
        databaseTableRecord.setIntegerValue(CommunicationsNetworkNodeP2PDatabaseConstants.NODES_CATALOG_DEFAULT_PORT_COLUMN_NAME, entity.getDefaultPort());
        databaseTableRecord.setStringValue(CommunicationsNetworkNodeP2PDatabaseConstants.NODES_CATALOG_IDENTITY_PUBLIC_KEY_COLUMN_NAME, entity.getIdentityPublicKey());
        databaseTableRecord.setStringValue(CommunicationsNetworkNodeP2PDatabaseConstants.NODES_CATALOG_IP_COLUMN_NAME, entity.getIp());
        databaseTableRecord.setStringValue(CommunicationsNetworkNodeP2PDatabaseConstants.NODES_CATALOG_NAME_COLUMN_NAME, entity.getName());
        databaseTableRecord.setDoubleValue(CommunicationsNetworkNodeP2PDatabaseConstants.NODES_CATALOG_LAST_LATITUDE_COLUMN_NAME, entity.getLastLocation().getLatitude());
        databaseTableRecord.setDoubleValue(CommunicationsNetworkNodeP2PDatabaseConstants.NODES_CATALOG_LAST_LONGITUDE_COLUMN_NAME, entity.getLastLocation().getLongitude());
        databaseTableRecord.setLongValue(CommunicationsNetworkNodeP2PDatabaseConstants.NODES_CATALOG_REGISTERED_TIMESTAMP_COLUMN_NAME, getLongValueFromTimestamp(entity.getRegisteredTimestamp()));
        databaseTableRecord.setIntegerValue(CommunicationsNetworkNodeP2PDatabaseConstants.NODES_CATALOG_LATE_NOTIFICATION_COUNTER_COLUMN_NAME, entity.getLateNotificationsCounter());
        databaseTableRecord.setIntegerValue(CommunicationsNetworkNodeP2PDatabaseConstants.NODES_CATALOG_OFFLINE_COUNTER_COLUMN_NAME, entity.getOfflineCounter());

        return databaseTableRecord;
    }

    /**
     * Method that list ten nodes with less late_notification_counter and offline_counter
     *
     * @return All entities.
     *
     * @throws CantReadRecordDataBaseException if something goes wrong.
     */
    public final List<NodesCatalog> getNodeCatalogueListToShare(String identityPublicKey) throws CantReadRecordDataBaseException {

        try {

            // load the data base to memory
            DatabaseTable table = getDatabaseTable();

            table.addFilterOrder(CommunicationsNetworkNodeP2PDatabaseConstants.NODES_CATALOG_LATE_NOTIFICATION_COUNTER_COLUMN_NAME, DatabaseFilterOrder.ASCENDING);
            table.addFilterOrder(CommunicationsNetworkNodeP2PDatabaseConstants.NODES_CATALOG_OFFLINE_COUNTER_COLUMN_NAME          , DatabaseFilterOrder.ASCENDING);

            table.addStringFilter(CommunicationsNetworkNodeP2PDatabaseConstants.NODES_CATALOG_IDENTITY_PUBLIC_KEY_COLUMN_NAME, identityPublicKey, DatabaseFilterType.NOT_EQUALS);
            table.setFilterTop("10");
            table.loadToMemory();

            final List<DatabaseTableRecord> records = table.getRecords();

            final List<NodesCatalog> list = new ArrayList<>();

            // Convert into entity objects and add to the list.
            for (DatabaseTableRecord record : records)
                list.add(getEntityFromDatabaseTableRecord(record));

            return list;

        } catch (final CantLoadTableToMemoryException e) {

            throw new CantReadRecordDataBaseException(e, "Table Name: " + getTableName(), "The data no exist");
        } catch (final InvalidParameterException e) {

            throw new CantReadRecordDataBaseException(e, "Table Name: " + getTableName(), "Invalid parameter found, maybe the enum is wrong.");
        }
    }

}
