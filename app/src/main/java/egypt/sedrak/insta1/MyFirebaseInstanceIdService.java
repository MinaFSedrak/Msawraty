package egypt.sedrak.insta1;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by lenovov on 13-Mar-17.
 */

public class MyFirebaseInstanceIdService extends FirebaseInstanceIdService {

    public static final String TAG = "Notification";

    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();

        Log.v(TAG, "Token: " + FirebaseInstanceId.getInstance().getToken());

    }
}
