package egypt.sedrak.insta1;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class PhotographerActivity extends AppCompatActivity {
    private DatabaseReference mDatabasePhotographers;
    private DatabaseReference mDatabaseCategories;

    private DatabaseReference mDatabaseWeddingCategorie;
    private DatabaseReference mDatabaseBirthdayCategorie;
    private DatabaseReference mDatabaseGraduationCategorie;
    private DatabaseReference mDatabasePhotoBoothCategorie;

    private FirebaseAuth mAuth;

    private Intent mIntent;
    private ListView listView;

    private Button mReserveNowBtn;

    ImageView profile_picture;


    private List<Package> mPackagesList;
    private DatabaseReference mDatabasePackages;
    private com.google.firebase.database.Query mUserPackagesQuery;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photographer);
        setTitle("Photographer Profile");
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.mipmap.ic_arrow_back_white_18dp);
        setSupportActionBar(toolbar);

        mAuth = FirebaseAuth.getInstance();
        mDatabasePhotographers = FirebaseDatabase.getInstance().getReference().child("Users").child("Photographers");

        final String userUID = mAuth.getCurrentUser().getUid();
        photographerOrNot(userUID);

        ViewPager viewPager= (ViewPager) findViewById(R.id.pager);
        PagerAdapter pagerAdapter = new PagerAdapter(getSupportFragmentManager(), PhotographerActivity.this);
        viewPager.setAdapter(pagerAdapter);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);

        /*PageViewPagerAdapter adapter = new PageViewPagerAdapter(PhotographerActivity.this,mPackagesList);
        listView.setAdapter(adapter);*/



        mIntent = getIntent();
        mDatabasePackages = FirebaseDatabase.getInstance().getReference().child("Packages");
        mUserPackagesQuery = mDatabasePackages.orderByChild("owner").equalTo(mIntent.getStringExtra("userUID"));
        mPackagesList = new ArrayList<>();
        //MyApplication.PackageListener(mUserPackagesQuery, mPackagesList);

        mDatabaseCategories = FirebaseDatabase.getInstance().getReference().child("Categories");


        mDatabaseWeddingCategorie = mDatabaseCategories.child("Wedding_and_Engagement");
        mDatabaseBirthdayCategorie = mDatabaseCategories.child("Birthday");
        mDatabaseGraduationCategorie = mDatabaseCategories.child("Graduation");
        mDatabasePhotoBoothCategorie = mDatabaseCategories.child("PhotoBooth");


        profile_picture = (ImageView) findViewById(R.id.imageSelect);

        mReserveNowBtn = (Button) findViewById(R.id.reserveBtn);
        mReserveNowBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mIntent.getStringExtra("userUID") != null){
                    Intent intent= new Intent(PhotographerActivity.this,RequestFormActivity.class);
                    intent.putExtra("userUID",mIntent.getStringExtra("userUID"));
                    startActivity(intent);
                }


            }
        });


        // my Profile
        if(mIntent.getStringExtra("userUID") == null){
             addListenerForSingleValueEvent(mAuth.getCurrentUser().getUid());


        }
        // SetOnClickListener
        else{
            addListenerForSingleValueEvent(mIntent.getStringExtra("userUID"));}


    }


    @Override
    protected void onStart() {
        super.onStart();





        Log.i("PHActUserPackages", String.valueOf(mPackagesList.size()));
        /*Log.i("PHActUserPackages", String.valueOf(mPackagesList.get(1).getPrice()));
        Log.i("PHActUserPackages", String.valueOf(mPackagesList.get(2).getPrice()));
        Log.i("PHActUserPackages", String.valueOf(mPackagesList.get(5).getPrice()));*/

    }


    private void photographerOrNot (final String userUID){

        mDatabasePhotographers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild(userUID)){
                    mReserveNowBtn.setVisibility(View.INVISIBLE);

                }else {
                    mReserveNowBtn.setVisibility(View.VISIBLE);}
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }


    private void addListenerForSingleValueEvent (final String userUID){

        mDatabasePhotographers.child(userUID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(com.google.firebase.database.DataSnapshot dataSnapshot) {
                setTitle(dataSnapshot.child("name").getValue(String.class));
                String downloadUri = (String) dataSnapshot.child("profile_picture").getValue();
                Picasso.with(getApplicationContext()).load(downloadUri).into(profile_picture);



            }

            @Override
            public void onCancelled(DatabaseError databaseError) {


            }
        });

    }


        @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
