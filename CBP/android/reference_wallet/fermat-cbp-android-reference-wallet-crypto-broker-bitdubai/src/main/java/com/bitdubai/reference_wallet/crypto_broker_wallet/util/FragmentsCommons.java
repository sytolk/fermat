package com.bitdubai.reference_wallet.crypto_broker_wallet.util;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import com.bitdubai.fermat_android_api.layer.definition.wallet.views.FermatTextView;
import com.bitdubai.fermat_cbp_api.layer.identity.crypto_broker.interfaces.CryptoBrokerIdentity;
import com.bitdubai.fermat_ccp_api.layer.module.intra_user.exceptions.CantGetActiveLoginIdentityException;
import com.bitdubai.reference_wallet.crypto_broker_wallet.R;
import com.squareup.picasso.Picasso;

/**
 * Created by nelson on 16/11/15.
 */
public class FragmentsCommons {

    public static final String NEGOTIATION_DATA = "negotiation_data";
    public static final String CONTRACT_DATA = "contract_data";
    public static final String CONFIGURED_DATA = "configured_data";
    public static final String EXCHANGE_RATES = "EXCHANGE_RATES";

    public static int getClauseNumberImageRes(int clauseNumber) {
        switch (clauseNumber) {
            case 1:
                return R.drawable.bg_detail_number_01;
            case 2:
                return R.drawable.bg_detail_number_02;
            case 3:
                return R.drawable.bg_detail_number_03;
            case 4:
                return R.drawable.bg_detail_number_04;
            case 5:
                return R.drawable.bg_detail_number_05;
            case 6:
                return R.drawable.bg_detail_number_06;
            case 7:
                return R.drawable.bg_detail_number_07;
            case 8:
                return R.drawable.bg_detail_number_08;
            default:
                return R.drawable.bg_detail_number_09;
        }
    }

    public static View setUpHeaderScreen(LayoutInflater inflater, Context activity, CryptoBrokerIdentity identity) throws CantGetActiveLoginIdentityException {
        View view = inflater.inflate(R.layout.cbw_navigation_view_header, null, true);
        try {
            ImageView imageView = (ImageView) view.findViewById(R.id.cbw_image_view_profile);
            if (identity != null) {
                if (identity.getProfileImage() != null) {
                    if (identity.getProfileImage().length > 0) {
                        imageView.setImageBitmap((BitmapFactory.decodeByteArray(identity.getProfileImage(), 0, identity.getProfileImage().length)));
                    } else
                        Picasso.with(activity).load(R.drawable.profile_image_standard).into(imageView);
                }
                FermatTextView fermatTextView = (FermatTextView) view.findViewById(R.id.txt_name);
                fermatTextView.setText(identity.getAlias());
            } else {
                Picasso.with(activity).load(R.drawable.profile_image_standard).into(imageView);
                FermatTextView fermatTextView = (FermatTextView) view.findViewById(R.id.txt_name);
                fermatTextView.setText(R.string.cbw_identity_alias_default_text);
            }

            return view;
        } catch (OutOfMemoryError outOfMemoryError) {
            Toast.makeText(activity, "Error: out of memory ", Toast.LENGTH_SHORT).show();
        }
        return view;
    }
}
