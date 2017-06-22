package egypt.sedrak.insta1;

import android.app.Application;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by lenovov on 25-Feb-17.
 */
public class MyApplication extends Application {


    @Override
    public void onCreate() {
        super.onCreate();
    }

    public static void addCategorieValueEventListener(final DatabaseReference mCategorie, final String userUID, final TextView mCategorieField){


        mCategorie.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(userUID)) {

                    mCategorieField.append(mCategorie.getKey().trim() + " - ");

                    Log.i("checkCategorie",mCategorie.getKey().trim() + "\n");



                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    public static void PackageListener(com.google.firebase.database.Query mQuery, final List<Package> mPackageList ,
                                       final List<String> mPackagesKeys, final Spinner mCategorieSpinner, final Context context) {


        mQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Log.v("PackageCount", String.valueOf(dataSnapshot.getChildrenCount()));
                Iterator<DataSnapshot> iterator = dataSnapshot.getChildren().iterator();
                Iterator<DataSnapshot> iteratorKeys = dataSnapshot.getChildren().iterator();
                int length = (int) dataSnapshot.getChildrenCount();

                if(length <= 0){
                    Log.v("PackageCount", "no packages founded");
                }else {
                    for (int i=0; i<length; i++){

                        mPackageList.add(iterator.next().getValue(Package.class));
                        mPackagesKeys.add(iteratorKeys.next().getKey());

                        //Log.v("iteratorKey",iteratorKeys.next().getKey());
                        Log.v("packagePrice", mPackageList.get(i).getPrice());
                    }
                    RequestSpinnerAdapter requestSpinnerAdapter = new RequestSpinnerAdapter(context, mPackageList);
                    mCategorieSpinner.setAdapter(requestSpinnerAdapter);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }



   /* public static void addCategorieValueEventListener(final DatabaseReference mCategorie, final String userUID, final RadioButton mRadioBtn){

        mCategorie.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(userUID)) {
                    mRadioBtn.setVisibility(View.VISIBLE);

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }*/

}
