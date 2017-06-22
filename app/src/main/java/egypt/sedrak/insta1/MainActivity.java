package egypt.sedrak.insta1;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Arrays;


public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToogle;
    private NavigationView mNavigationView;

    private DatabaseReference mDatabaseUsers;
    private DatabaseReference mDatabasePhotographers;
    private DatabaseReference mDatabaseCategories;

    private DatabaseReference mDatabaseWeddingCategorie;
    private DatabaseReference mDatabaseBirthdayCategorie;
    private DatabaseReference mDatabaseGraduationCategorie;
    private DatabaseReference mDatabasePhotoBoothCategorie;

    private DatabaseReference mQueryDatabaseWedding;
    private DatabaseReference mQueryDatabaseBirthday;
    private DatabaseReference mQueryDatabaseGraduation;
    private DatabaseReference mQueryDatabasePhotoBooth;

    private com.google.firebase.database.Query mWeddingQuery;
    private com.google.firebase.database.Query mBirthdayQuery;
    private com.google.firebase.database.Query mGraduationQuery;
    private com.google.firebase.database.Query mPhotoBoothQuery;
    private com.google.firebase.database.Query mainQuery;



    private Button areYouPhotographerBtn;

    private RecyclerView mPhotographerList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("Msawraty");



        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener(){
            @Override
            public void onAuthStateChanged(FirebaseAuth firebaseAuth) {

                if(firebaseAuth.getCurrentUser() == null){
                    Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
                    loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(loginIntent);
                    MainActivity.this.finish();
                }



            }
        };


        mDatabaseUsers = FirebaseDatabase.getInstance().getReference().child("Users");
        mDatabasePhotographers = mDatabaseUsers.child("Photographers");
        mDatabaseCategories = FirebaseDatabase.getInstance().getReference().child("Categories");

        mDatabaseWeddingCategorie = mDatabaseCategories.child("Wedding_and_Engagement");
        mDatabaseBirthdayCategorie = mDatabaseCategories.child("Birthday");
        mDatabaseGraduationCategorie = mDatabaseCategories.child("Graduation");
        mDatabasePhotoBoothCategorie = mDatabaseCategories.child("PhotoBooth");


        mQueryDatabaseWedding = mDatabaseUsers.child("Photographers");
        mQueryDatabaseBirthday = mDatabaseUsers.child("Photographers");
        mQueryDatabaseGraduation = mDatabaseUsers.child("Photographers");
        mQueryDatabasePhotoBooth = mDatabaseUsers.child("Photographers");

        mainQuery = mDatabasePhotographers.orderByChild("name");
        mWeddingQuery = mQueryDatabaseWedding.orderByChild("Wedding_and_Engagement").equalTo("True");
        mBirthdayQuery = mQueryDatabaseBirthday.orderByChild("Birthday").equalTo("True");
        mGraduationQuery = mQueryDatabaseGraduation.orderByChild("Graduation").equalTo("True");
        mPhotoBoothQuery = mQueryDatabasePhotoBooth.orderByChild("PhotoBooth").equalTo("True");

        areYouPhotographerBtn = (Button) findViewById(R.id.areYouPhotographer);
        areYouPhotographerBtn.setVisibility(View.INVISIBLE);
        areYouPhotographerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, PhotographerFormActivity.class));
            }
        });


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mNavigationView = (NavigationView) findViewById(R.id.navigation_view);
        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                if(item.getItemId() == R.id.action_logout){
                    Log.i("logout", "logged out ");
                    item.setChecked(true);
                    logout();
                }

                if(areYouPhotographerBtn.getVisibility() == View.INVISIBLE){


                    if (item.getItemId() == R.id.action_myProfile){

                        Log.i("My profile", "user "+mAuth.getCurrentUser().getDisplayName()+" 's profile");
                        mDrawerLayout.closeDrawers();
                        startActivity(new Intent(MainActivity.this, PhotographerActivity.class));
                    }
                }

                if(item.getItemId() == R.id.action_home){
                    resetRecyclerView();
                    mPhotographerList.setAdapter(myFirebaseRecyclerViewAdapter(mainQuery));
                    mDrawerLayout.closeDrawers();
                }

                if(item.getItemId() == R.id.action_myRequests){
                    mDrawerLayout.closeDrawers();
                    startActivity(new Intent(MainActivity.this, RequestsActivity.class));
/*
                    LoginManager.getInstance().logInWithPublishPermissions(MainActivity.this, Arrays.asList("manage_pages"));
*/
                }

                if (item.getItemId() == R.id.action_wedding){
                    resetRecyclerView();
                    mPhotographerList.setAdapter(myFirebaseRecyclerViewAdapter(mWeddingQuery));
                    mDrawerLayout.closeDrawers();}

                if(item.getItemId() == R.id.action_birthday){
                    resetRecyclerView();
                    mPhotographerList.setAdapter(myFirebaseRecyclerViewAdapter(mBirthdayQuery));
                    mDrawerLayout.closeDrawers();}

                if(item.getItemId() == R.id.action_graduation){
                    resetRecyclerView();
                    mPhotographerList.setAdapter(myFirebaseRecyclerViewAdapter(mGraduationQuery));
                    mDrawerLayout.closeDrawers();}

                if(item.getItemId() == R.id.action_photoBooth){
                    resetRecyclerView();
                    mPhotographerList.setAdapter(myFirebaseRecyclerViewAdapter(mPhotoBoothQuery));
                    mDrawerLayout.closeDrawers();}



                return false;
            }
        });

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        mToogle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open, R.string.close);

        mDrawerLayout.addDrawerListener(mToogle);
        mToogle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mPhotographerList = (RecyclerView) findViewById(R.id.recyclerView_photographers);
        mPhotographerList.setHasFixedSize(true);
        mPhotographerList.setLayoutManager(new LinearLayoutManager(this));



    }

    @Override
    protected void onStart() {
        super.onStart();

        photographerOrNot();

        mAuth.addAuthStateListener(mAuthListener);

        resetRecyclerView();
        mPhotographerList.setAdapter(myFirebaseRecyclerViewAdapter(mainQuery));


    }

    private void resetRecyclerView(){

        mPhotographerList.getRecycledViewPool().clear();
        mPhotographerList.removeAllViewsInLayout();
        mPhotographerList.removeAllViews();
    }

    private FirebaseRecyclerAdapter<Photographer, PhotographerViewHolder> myFirebaseRecyclerViewAdapter(com.google.firebase.database.Query mQuery
    ){

        FirebaseRecyclerAdapter<Photographer, PhotographerViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Photographer, PhotographerViewHolder>(

                Photographer.class,
                R.layout.main_row,
                PhotographerViewHolder.class,
                mQuery
        ) {
            @Override
            protected void populateViewHolder(final PhotographerViewHolder viewHolder, Photographer model, int position) {

                final String userUID = getRef(position).getKey();

                viewHolder.setPhotographerName(model.getName());
                viewHolder.setProfilePicture(getApplicationContext(), model.getProfile_picture());
                TextView mCategorieField = (TextView) viewHolder.mView.findViewById(R.id.row_categorie);
                Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/AftaSansThin-Regular.otf");
                mCategorieField.setTypeface(typeface);

                mCategorieField.setText(null);
                MyApplication.addCategorieValueEventListener(mDatabaseWeddingCategorie, userUID, mCategorieField);
                MyApplication.addCategorieValueEventListener(mDatabaseBirthdayCategorie, userUID, mCategorieField);
                MyApplication.addCategorieValueEventListener(mDatabaseGraduationCategorie, userUID, mCategorieField);
                MyApplication.addCategorieValueEventListener(mDatabasePhotoBoothCategorie, userUID, mCategorieField);


                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent photographerActivityIntent = new Intent(MainActivity.this, PhotographerActivity.class);
                        photographerActivityIntent.putExtra("userUID", userUID);
                        startActivity(photographerActivityIntent);
                        Toast.makeText(MainActivity.this, userUID, Toast.LENGTH_SHORT).show();
                    }
                });

            }
        };

        firebaseRecyclerAdapter.notifyDataSetChanged();
        return firebaseRecyclerAdapter;

    }



    public static class PhotographerViewHolder extends RecyclerView.ViewHolder{

        View mView;

        public PhotographerViewHolder(View mView) {
            super(mView);
            this.mView = mView;
        }

        public void setPhotographerName(String photographerName){
            TextView name = (TextView) mView.findViewById(R.id.row_photographerName);
            Typeface typeface = Typeface.createFromAsset(mView.getContext().getAssets(), "fonts/AftaSansThin-Regular.otf");
            name.setTypeface(typeface);
            name.setText(photographerName);
        }

        public void setProfilePicture (Context ctx , String profilePicture){
            ImageView image = (ImageView) mView.findViewById(R.id.row_imgView);
            Picasso.with(ctx).load(profilePicture).into(image);
        }




    }

    private void photographerOrNot() {
        mDatabasePhotographers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(mAuth.getCurrentUser().getUid())) {
                    areYouPhotographerBtn.setVisibility(View.INVISIBLE);
                    Menu menu = mNavigationView.getMenu();
                    MenuItem myProfile =menu.findItem(R.id.action_myProfile);
                    myProfile.setVisible(true);

                } else {
                    areYouPhotographerBtn.setVisibility(View.VISIBLE);

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (mToogle.onOptionsItemSelected(item)){
            return true;
        }



        return super.onOptionsItemSelected(item);
    }

    private void logout() {
        mAuth.addAuthStateListener(mAuthListener);
        mAuth.signOut();
        LoginManager.getInstance().logOut();
    }

}
