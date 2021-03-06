package com.bitdubai.fermat_cbp_plugin.layer.actor_connection.crypto_broker.developer.bitdubai.version_1.database;

import com.bitdubai.fermat_api.layer.actor_connection.common.database_abstract_classes.ActorConnectionDao;
import com.bitdubai.fermat_api.layer.actor_connection.common.database_common_classes.ActorConnectionDatabaseConstants;
import com.bitdubai.fermat_api.layer.actor_connection.common.enums.ConnectionState;
import com.bitdubai.fermat_api.layer.actor_connection.common.exceptions.CantGetProfileImageException;
import com.bitdubai.fermat_api.layer.all_definition.enums.Actors;
import com.bitdubai.fermat_api.layer.all_definition.exceptions.InvalidParameterException;
import com.bitdubai.fermat_api.layer.osa_android.database_system.DatabaseTableRecord;
import com.bitdubai.fermat_api.layer.osa_android.database_system.PluginDatabaseSystem;
import com.bitdubai.fermat_api.layer.osa_android.file_system.PluginFileSystem;
import com.bitdubai.fermat_api.layer.osa_android.file_system.exceptions.FileNotFoundException;
import com.bitdubai.fermat_cbp_api.layer.actor_connection.crypto_broker.utils.CryptoBrokerActorConnection;
import com.bitdubai.fermat_cbp_api.layer.actor_connection.crypto_broker.utils.CryptoBrokerLinkedActorIdentity;

import java.util.UUID;

/**
 * The class <code>com.bitdubai.fermat_cbp_plugin.layer.actor_connection.crypto_broker.developer.bitdubai.version_1.database.CryptoBrokerActorConnectionDao</code>
 * contains all the methods that interact with the database.
 * Manages the actor connections by storing them on a Database Table.
 * <p/>
 * Created by Leon Acosta - (laion.cj91@gmail.com) on 09/12/2015.
 *
 * @author lnacosta
 * @version 1.0
 * @since Java JDK 1.7
 */
public class CryptoBrokerActorConnectionDao extends ActorConnectionDao<CryptoBrokerLinkedActorIdentity, CryptoBrokerActorConnection> {

    public CryptoBrokerActorConnectionDao(final PluginDatabaseSystem pluginDatabaseSystem,
                                          final PluginFileSystem     pluginFileSystem    ,
                                          final UUID                 pluginId            ) {

        super(
                pluginDatabaseSystem,
                pluginFileSystem    ,
                pluginId
        );
    }

    protected CryptoBrokerActorConnection buildActorConnectionNewRecord(final DatabaseTableRecord record) throws InvalidParameterException {

        try {
            UUID   connectionId                  = record.getUUIDValue  (ActorConnectionDatabaseConstants.ACTOR_CONNECTIONS_CONNECTION_ID_COLUMN_NAME             );
            String linkedIdentityPublicKey       = record.getStringValue(ActorConnectionDatabaseConstants.ACTOR_CONNECTIONS_LINKED_IDENTITY_PUBLIC_KEY_COLUMN_NAME);
            String linkedIdentityActorTypeString = record.getStringValue(ActorConnectionDatabaseConstants.ACTOR_CONNECTIONS_LINKED_IDENTITY_ACTOR_TYPE_COLUMN_NAME);
            String publicKey                     = record.getStringValue(ActorConnectionDatabaseConstants.ACTOR_CONNECTIONS_PUBLIC_KEY_COLUMN_NAME                );
            String alias                         = record.getStringValue(ActorConnectionDatabaseConstants.ACTOR_CONNECTIONS_ALIAS_COLUMN_NAME                     );
            String connectionStateString         = record.getStringValue(ActorConnectionDatabaseConstants.ACTOR_CONNECTIONS_CONNECTION_STATE_COLUMN_NAME          );
            long   creationTime                  = record.getLongValue(ActorConnectionDatabaseConstants.ACTOR_CONNECTIONS_CREATION_TIME_COLUMN_NAME);
            long   updateTime                    = record.getLongValue  (ActorConnectionDatabaseConstants.ACTOR_CONNECTIONS_UPDATE_TIME_COLUMN_NAME               );

            ConnectionState connectionState         = ConnectionState.getByCode(connectionStateString);

            Actors          linkedIdentityActorType = Actors         .getByCode(linkedIdentityActorTypeString);

            CryptoBrokerLinkedActorIdentity actorIdentity = new CryptoBrokerLinkedActorIdentity(
                    linkedIdentityPublicKey,
                    linkedIdentityActorType
            );


            byte[] profileImage;

            try {
                profileImage = getProfileImage(publicKey);
            } catch (FileNotFoundException e) {
                profileImage = new byte[0];
            }

            return new CryptoBrokerActorConnection(
                    connectionId   ,
                    actorIdentity  ,
                    publicKey      ,
                    alias          ,
                    profileImage   ,
                    connectionState,
                    creationTime   ,
                    updateTime
            );

    } catch (final CantGetProfileImageException e) {

        throw new InvalidParameterException(
                e,
                "",
                "Problem trying to get the profile image."
        );
    }
    }

}
