package com.bitbudai.fermat_cht_android_sub_app_chat_identity_bitdubai.fragments;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.bitbudai.fermat_cht_android_sub_app_chat_identity_bitdubai.util.GeolocationIdentityExecutor;
import com.bitdubai.fermat_android_api.layer.definition.wallet.AbstractFermatFragment;
import com.bitdubai.fermat_android_api.layer.definition.wallet.interfaces.ReferenceAppFermatSession;
import com.bitdubai.fermat_api.FermatException;
import com.bitdubai.fermat_api.layer.all_definition.common.system.interfaces.ErrorManager;
import com.bitdubai.fermat_api.layer.all_definition.common.system.interfaces.error_manager.enums.UnexpectedSubAppExceptionSeverity;
import com.bitdubai.fermat_api.layer.all_definition.common.system.interfaces.error_manager.enums.UnexpectedUIExceptionSeverity;
import com.bitdubai.fermat_api.layer.all_definition.enums.UISource;
import com.bitdubai.fermat_api.layer.all_definition.exceptions.InvalidParameterException;
import com.bitdubai.fermat_api.layer.all_definition.navigation_structure.enums.Activities;
import com.bitdubai.fermat_api.layer.dmp_engine.sub_app_runtime.enums.SubApps;
import com.bitdubai.fermat_cht_android_sub_app_chat_identity_bitdubai.R;
import com.bitdubai.fermat_cht_api.all_definition.enums.Frecuency;
import com.bitdubai.fermat_cht_api.all_definition.exceptions.CHTException;
import com.bitdubai.fermat_cht_api.layer.identity.exceptions.CantGetChatIdentityException;
import com.bitdubai.fermat_cht_api.layer.sup_app_module.interfaces.identity.ChatIdentityModuleManager;
import com.bitdubai.fermat_cht_api.layer.sup_app_module.interfaces.identity.ChatIdentityPreferenceSettings;
import com.bitdubai.fermat_pip_api.layer.network_service.subapp_resources.SubAppResourcesProviderManager;

import java.util.ArrayList;
import java.util.List;

import static com.bitbudai.fermat_cht_android_sub_app_chat_identity_bitdubai.util.CreateChatIdentityExecutor.SUCCESS;




/**
 * FERMAT-ORG
 * Developed by Lozadaa on 04/04/16.
 */

public class GeolocationChatIdentityFragment  extends AbstractFermatFragment<ReferenceAppFermatSession<ChatIdentityModuleManager>, SubAppResourcesProviderManager> {

    ChatIdentityModuleManager moduleManager;
    ErrorManager errorManager;
    EditText accuracy;
    Spinner frequency;
    Toolbar toolbar;
    int acurracydata;
    Frecuency frecuencydata;

    private ChatIdentityPreferenceSettings chatIdentitySettings;

    public static GeolocationChatIdentityFragment newInstance() {
        return new GeolocationChatIdentityFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            moduleManager = appSession.getModuleManager();
            errorManager = appSession.getErrorManager();
            chatIdentitySettings = null;
            try {
                chatIdentitySettings = moduleManager.loadAndGetSettings(appSession.getAppPublicKey());
                //chatIdentitySettings = moduleManager.getSettingsManager().loadAndGetSettings(appSession.getAppPublicKey());
            }catch(Exception e){
                chatIdentitySettings = null;
            }
            if (chatIdentitySettings == null) {
                try {
                    moduleManager.persistSettings(appSession.getAppPublicKey(), chatIdentitySettings);
                    //moduleManager.getSettingsManager().persistSettings(appSession.getAppPublicKey(), chatIdentitySettings);
                } catch (Exception e) {
                    errorManager.reportUnexpectedSubAppException(SubApps.CHT_CHAT, UnexpectedSubAppExceptionSeverity.DISABLES_SOME_FUNCTIONALITY_WITHIN_THIS_FRAGMENT, e);
                }
            }
        } catch (Exception e) {
            errorManager.reportUnexpectedUIException(UISource.ACTIVITY, UnexpectedUIExceptionSeverity.UNSTABLE, FermatException.wrapException(e));

        }
        toolbar = getToolbar();
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.cht_ic_back_buttom));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootLayout = inflater.inflate(R.layout.fragment_cht_identity_geolocation, container, false);
        initViews(rootLayout);
        return rootLayout;
    }

    /**
     * Initializes the views of this Fragment
     *
     * @param layout layout of this Fragment containing the views
     */

    private void initViews(View layout) {

        // Spinner Drop down elements
        List<Frecuency> dataspinner = new ArrayList<Frecuency>();
        dataspinner.add(Frecuency.LOW);
        dataspinner.add(Frecuency.NORMAL);
        dataspinner.add(Frecuency.HIGH);

        // Spinner element
        accuracy = (EditText) layout.findViewById(R.id.accuracy);
        frequency = (Spinner) layout.findViewById(R.id.spinner_frequency);

        try {
                setValues();
            // frequency.setBackgroundColor(new Color.parseColor("#d1d1d1"));
            frequency.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    try {
                        frecuencydata = Frecuency.getByCode(parent.getItemAtPosition(position).toString());
                    } catch (InvalidParameterException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });
        } catch (CantGetChatIdentityException e) {
            e.printStackTrace();
        }

        ArrayAdapter<Frecuency> dataAdapter = new ArrayAdapter<Frecuency>(getActivity(), android.R.layout.simple_spinner_item, dataspinner);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        frequency.setAdapter(dataAdapter);
    }


    @Override
    public void onCreateOptionsMenu(final Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }
    public void saveAndGoBack(){
        try {
            if(ExistIdentity()){
                saveIdentityGeolocation("onBack");
            }else{

            }
        } catch (CHTException e) {

        }
    }

    @Override
    public void onBackPressed(){
        saveAndGoBack();
        changeActivity(Activities.CHT_CHAT_CREATE_IDENTITY, appSession.getAppPublicKey());
        //super.onBackPressed();
    }

    private void saveIdentityGeolocation(String donde) throws CantGetChatIdentityException {
        GeolocationIdentityExecutor executor = null;
            try {
                if (accuracy.getText().length() == 0) {
                    Toast.makeText(getActivity(), "Acuraccy is empty, please add a value", Toast.LENGTH_SHORT).show();
                } else {
                    acurracydata = Integer.parseInt(accuracy.getText().toString());
                    executor = new GeolocationIdentityExecutor(appSession, moduleManager.getIdentityChatUser().getPublicKey(), moduleManager.getIdentityChatUser().getAlias(), moduleManager.getIdentityChatUser().getImage(), moduleManager.getIdentityChatUser().getConnectionState(), moduleManager.getIdentityChatUser().getCountry(), moduleManager.getIdentityChatUser().getState(), moduleManager.getIdentityChatUser().getCity(), frecuencydata, acurracydata);
                    int resultKey = executor.execute();
                    switch (resultKey) {
                        case SUCCESS:
                            if (donde.equalsIgnoreCase("onClick")) {
                                Toast.makeText(getActivity(), "Chat Identity Geolocation Update.", Toast.LENGTH_LONG).show();
                                getActivity().onBackPressed();
                            } else if (donde.equalsIgnoreCase("onBack")) {
                                Toast.makeText(getActivity(), "Chat Identity Geolocation Update.", Toast.LENGTH_LONG).show();
                            }
                            break;
                        }

                 }
            }catch(CHTException e){
                errorManager.reportUnexpectedUIException(UISource.ACTIVITY, UnexpectedUIExceptionSeverity.UNSTABLE, FermatException.wrapException(e));

            }
        }


    public boolean ExistIdentity() throws CHTException {
        try {
            if (!moduleManager.getIdentityChatUser().getAlias().isEmpty()) {
                Log.i("CHT EXIST IDENTITY", "TRUE");
                return true;
            }
        } catch (Exception e) {
            return false;
        }
        Log.i("CHT EXIST IDENTITY", "FALSE");
        return false;
    }

    public void setValues() throws CantGetChatIdentityException {
            accuracy.setText(""+moduleManager.getIdentityChatUser().getAccuracy());
    }


}