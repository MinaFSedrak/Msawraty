package egypt.sedrak.insta1;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.ArrayList;
import java.util.List;

public class PhotographerFormActivity extends AppCompatActivity implements View.OnClickListener{

    private Button msubmitBtn;

    private EditText mMobileField;
    private EditText mFacebookField;

    private ImageButton photographerProfileImg;
    private ImageView photographerLogoView;

    private static final int GALLERY_REQUEST = 1;
    private static final int PACKAGE_REQUEST = 2;
    private Uri mImageUri = null;

    private List<Package> mPackages;
    private GridView mGridView;

    private ProgressDialog mProgressDialog;

    private DatabaseReference mDatabase;
    private DatabaseReference mDatabaseCustomers;
    private DatabaseReference mDatabaseCategories;
    private DatabaseReference mDatabasePhotographers;
    private DatabaseReference mDatabasePackages;

    private StorageReference mStorage;

    private FirebaseAuth mAuth;

    private Intent mIntent;
    private TextView mAddBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photographer_form);
        setTitle("Photographer Form");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.mipmap.ic_arrow_back_white_18dp);
        setSupportActionBar(toolbar);


        mIntent = getIntent();
        mAddBtn = (TextView) findViewById(R.id.Package);
        mAddBtn.setOnClickListener(this);

        mMobileField = (EditText) findViewById(R.id.mobileNumberField);
        mFacebookField = (EditText) findViewById(R.id.facebook_page);

        mPackages = new ArrayList<Package>();

        mGridView = (GridView) findViewById(R.id.packageGridView);


        mAuth = FirebaseAuth.getInstance();

        mStorage = FirebaseStorage.getInstance().getReference();

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabaseCustomers = mDatabase.child("Users").child("Customers");
        mDatabasePhotographers = mDatabase.child("Users").child("Photographers");
        mDatabaseCategories = mDatabase.child("Categories");
        mDatabasePackages = mDatabase.child("Packages");

        mProgressDialog = new ProgressDialog(this);

        photographerProfileImg = (ImageButton) findViewById(R.id.myprofile_imgBtn);
        photographerLogoView = (ImageView) findViewById(R.id.image_logo);
        msubmitBtn = (Button) findViewById(R.id.submitBtn);



        photographerProfileImg.setOnClickListener(this);
        msubmitBtn.setOnClickListener(this);




    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_REQUEST && resultCode == RESULT_OK)
        {
            mImageUri = data.getData();
            CropImage.activity(mImageUri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(2,1)
                    .start(this);

        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {
                mImageUri = result.getUri();
                photographerLogoView.setImageURI(mImageUri);

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Toast.makeText(PhotographerFormActivity.this, error.toString(),Toast.LENGTH_LONG).show();
            }
        }

        if(requestCode == PACKAGE_REQUEST && resultCode == RESULT_OK){

            Package newPackage = new Package();

            /*Toast.makeText(this, data.getStringExtra("categorie"), Toast.LENGTH_SHORT).show();
            Toast.makeText(this, data.getStringExtra("price"), Toast.LENGTH_SHORT).show();
            Toast.makeText(this, data.getStringExtra("duration"), Toast.LENGTH_SHORT).show();
            Toast.makeText(this, data.getStringExtra("noOfPh"), Toast.LENGTH_SHORT).show();
            Toast.makeText(this, data.getStringExtra("transportation"), Toast.LENGTH_SHORT).show();*/

            if(data.getStringExtra("extraDescription") != null){
                newPackage.setExtraDescription(data.getStringExtra("extraDescription"));
                Toast.makeText(this, data.getStringExtra("extraDescription"), Toast.LENGTH_SHORT).show();
            }else {
                newPackage.setExtraDescription("nothing");
            }

            // adding new package
            newPackage.setCategorieLogo(data.getStringExtra("categorieLogo"));
            newPackage.setCategorie(data.getStringExtra("categorie"));
            newPackage.setPrice(data.getStringExtra("price"));
            newPackage.setDuration(data.getStringExtra("duration"));
            newPackage.setNumOfPhotographers(data.getStringExtra("noOfPh"));

            // appending package
            mPackages.add(newPackage);
            PackageAdapter packageAdapter = new PackageAdapter(getBaseContext(), mPackages);
            mGridView.setAdapter(packageAdapter);










        }


    }

    private void addPhotographer() {

        final String mobile_value = mMobileField.getText().toString().trim();
        final String facebookPage_value = mFacebookField.getText().toString().trim();


        if (!TextUtils.isEmpty(mobile_value) && !TextUtils.isEmpty(facebookPage_value) && mImageUri != null) {


            mProgressDialog.setMessage("Photographer Signing...");
            mProgressDialog.show();
            mProgressDialog.setCancelable(false);
            mProgressDialog.setCanceledOnTouchOutside(false);
            StorageReference filepath = mStorage.child("Profile_pictures").child(mImageUri.getLastPathSegment());
            Toast.makeText(this, filepath.toString(), Toast.LENGTH_LONG).show();

            filepath.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    final Uri downloadUrl = taskSnapshot.getDownloadUrl();

                    // Validation just be sure that he's already a Customer
                    mDatabaseCustomers.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.hasChild(mAuth.getCurrentUser().getUid())) {


                                // adding to photographers
                                DatabaseReference current_user = mDatabasePhotographers.child(mAuth.getCurrentUser().getUid());
                                current_user.child("name").setValue(mAuth.getCurrentUser().getDisplayName());
                                current_user.child("email").setValue(mAuth.getCurrentUser().getEmail());
                                current_user.child("profile_picture").setValue(downloadUrl.toString());
                                current_user.child("facebook").setValue(facebookPage_value);
                                current_user.child("mobile").setValue(mobile_value);

                                // Creating User Packages
                                for(int i=0; i<mPackages.size(); i++){
                                    current_user.child(mPackages.get(i).getCategorie()).setValue("True");

                                    DatabaseReference current_userPackage = mDatabasePackages.push();
                                    current_userPackage.child("owner").setValue(mAuth.getCurrentUser().getUid());
                                    current_userPackage.child("categorie").setValue(mPackages.get(i).getCategorie());
                                    current_userPackage.child("price").setValue(mPackages.get(i).getPrice());
                                    current_userPackage.child("duration").setValue(mPackages.get(i).getDuration());
                                    current_userPackage.child("numOfPhotographers").setValue(mPackages.get(i).getNumOfPhotographers());
                                    current_userPackage.child("extraDescription").setValue(mPackages.get(i).getExtraDescription());
                                    current_userPackage.child("categorieLogo").setValue(mPackages.get(i).getCategorieLogo());

                                    // Creating Categorie
                                    DatabaseReference current_userCategorie = mDatabaseCategories.child(mPackages.get(i).getCategorie());
                                    current_userCategorie.child(mAuth.getCurrentUser().getUid()).setValue("True");
                                }



                                // removing from customer
                                mDatabaseCustomers.child(mAuth.getCurrentUser().getUid()).removeValue();

                                Intent photographerIntent = new Intent(PhotographerFormActivity.this, PhotographerActivity.class);
                                photographerIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(photographerIntent);
                                PhotographerFormActivity.this.finish();


                            }


                            mProgressDialog.dismiss();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Toast.makeText(getApplicationContext(), "Photographer isn't added \n" + databaseError, Toast.LENGTH_LONG).show();
                            mProgressDialog.dismiss();
                        }
                    });

                }
            });

        }else{
            Toast.makeText(this, "Empty Fields", Toast.LENGTH_SHORT).show();
        }


    }



    @Override
    public void onClick(View v) {

        if(v.getId() == R.id.submitBtn){

            boolean error = false;

            if(mMobileField.getText().toString().length() == 0){
               mMobileField.setError("Enter your mobile number");
               error = true;}

            if(mFacebookField.getText().toString().length() == 0){
               mFacebookField.setError("Enter your Facebook Url page");
               error = true;}

            if(mPackages.isEmpty()){
                Toast.makeText(this, "Enter at least one package", Toast.LENGTH_SHORT).show();
                error = true;}


            if (error == false){
                addPhotographer();}

        }

        if(v.getId() == R.id.myprofile_imgBtn)
        {
            Intent gallery = new Intent(Intent.ACTION_GET_CONTENT);
            gallery.setType("image/*");
            startActivityForResult(gallery,GALLERY_REQUEST);
        }


        if(v.getId() == R.id.Package){
            Intent  packageIntent = new Intent(PhotographerFormActivity.this, PackageActivity.class);
            startActivityForResult(packageIntent, PACKAGE_REQUEST);

        }

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
