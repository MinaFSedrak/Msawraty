package egypt.sedrak.insta1;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class RequestInfoActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;


    private TextView mAddressField;
    private TextView mCategorieField;
    private TextView mPriceField;
    private TextView mDateField;
    private TextView mFromField;
    private TextView mToField;
    private TextView mStatusField;
    private TextView mPhoneField;

    private Button mAcceptBtn;
    private Button mRefuseBtn;

    private DatabaseReference mDatabaseUsers;
    private DatabaseReference mDatabasePhotographers;
    private DatabaseReference mDatabaseRequests;
    private DatabaseReference mDatabasePackages;

  /*
    String expire_date;
    String event_date;
    String categorie;
    String price;
    String address;*/
    String name;
    String from;
    String to;
    String telephoneNumber;
    String requestUID;
    String status;
    String packageUID;

    private Intent mIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_info);
        setTitle("Order Details");
        mAuth = FirebaseAuth.getInstance();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.mipmap.ic_arrow_back_white_18dp);
        setSupportActionBar(toolbar);


        mAddressField = (TextView) findViewById(R.id.info_address);
        mCategorieField = (TextView) findViewById(R.id.info_categorie);
        mPriceField = (TextView) findViewById(R.id.info_price);
        mDateField = (TextView) findViewById(R.id.info_eventDate);
        mFromField = (TextView) findViewById(R.id.info_from);
        mToField = (TextView) findViewById(R.id.info_to);
        mStatusField = (TextView) findViewById(R.id.info_status);
        mPhoneField = (TextView) findViewById(R.id.info_phone);

        mAcceptBtn = (Button) findViewById(R.id.info_acceptBtn);
        mRefuseBtn = (Button) findViewById(R.id.info_refuseBtn);

        mIntent = getIntent();

      /*
        expire_date = mIntent.getStringExtra("expire_date");
        event_date = mIntent.getStringExtra("event_date");
        categorie = mIntent.getStringExtra("categorie");
        price = mIntent.getStringExtra("price");
        address = mIntent.getStringExtra("address");*/
        name = mIntent.getStringExtra("name");
        from = mIntent.getStringExtra("from");
        to = mIntent.getStringExtra("to");
        telephoneNumber = mIntent.getStringExtra("telephoneNumber");
        requestUID = mIntent.getStringExtra("requestUID");
        status = mIntent.getStringExtra("status");
        packageUID = mIntent.getStringExtra("packageUID");
        //Log.i("intent", name + "   " + to + "   " + event_date);

       /* mAddressField.setText(address);
        mCategorieField.setText(categorie);
        mPriceField.setText(price);
        mDateField.setText(event_date);
       ;*/
        mFromField.setText("Customer Name:  "+ name);
        mStatusField.setText("Status: "+ status);

        mDatabaseUsers = FirebaseDatabase.getInstance().getReference().child("Users");
        mDatabasePhotographers = FirebaseDatabase.getInstance().getReference().child("Users").child("Photographers");
        mDatabaseRequests = FirebaseDatabase.getInstance().getReference().child("Requests");
        mDatabasePackages = FirebaseDatabase.getInstance().getReference().child("Packages");

        if(to.equals(mAuth.getCurrentUser().getUid()) && status.equals("Waiting")){
            mAcceptBtn.setVisibility(View.VISIBLE);
            mRefuseBtn.setVisibility(View.VISIBLE);
        }

        if(to.equals(mAuth.getCurrentUser().getUid()) && status.equals("Accepted") ){
            mPhoneField.setVisibility(View.VISIBLE);
            mPhoneField.setText("Customer Mobile: "+ telephoneNumber);
        }

        if(from.equals(mAuth.getCurrentUser().getUid()) && status.equals("Accepted")){
            mPhoneField.setVisibility(View.VISIBLE);
            mDatabasePhotographers.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.hasChild(to)) {
                        mPhoneField.setText("Photographer Mobile: "+ dataSnapshot.child(to).child("mobile").getValue(String.class));
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

        mAcceptBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(to.equals(mAuth.getCurrentUser().getUid())) {

                    mDatabaseRequests.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.hasChild(requestUID) &&
                                    status.equals(dataSnapshot.child(requestUID).child("status").getValue(String.class))) {

                                DatabaseReference current_request = mDatabaseRequests.child(requestUID);
                                current_request.child("status").setValue("Accepted");

                                mAcceptBtn.setVisibility(View.INVISIBLE);
                                mRefuseBtn.setVisibility(View.INVISIBLE);
                                Toast.makeText(RequestInfoActivity.this, "Request Accepted check customer's phone number for communication"
                                        , Toast.LENGTH_LONG).show();

                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }
        });

        mRefuseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(to.equals(mAuth.getCurrentUser().getUid())) {

                    mDatabaseRequests.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.hasChild(requestUID) &&
                                    status.equals(dataSnapshot.child(requestUID).child("status").getValue(String.class))) {

                                DatabaseReference current_request = mDatabaseRequests.child(requestUID);
                                current_request.child("status").setValue("Refused");

                                mAcceptBtn.setVisibility(View.INVISIBLE);
                                mRefuseBtn.setVisibility(View.INVISIBLE);
                                Toast.makeText(RequestInfoActivity.this, "Request Refused", Toast.LENGTH_LONG).show();

                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();



        mDatabasePhotographers.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(to)) {
                    mToField.setText("Photographer Name: " + dataSnapshot.child(to).child("name").getValue(String.class));

                } else {
                    mToField.setError("Photographer doesn't exist");
                    Toast.makeText(RequestInfoActivity.this, "Photographer doesn't anymore exist ", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mDatabaseUsers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.child("Customers").hasChild(from) || !dataSnapshot.child("Photographers").hasChild(to)) {
                    mDatabaseRequests.child(requestUID).removeValue();
                } else {
                    mDatabaseRequests.child(requestUID).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {


                            mDatabasePackages.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if(dataSnapshot.hasChild(packageUID)){
                                        mCategorieField.setText("Categorie: "+ dataSnapshot.child(packageUID).child("categorie").getValue(String.class));
                                        mPriceField.setText("Price: "+ dataSnapshot.child(packageUID).child("price").getValue(String.class)+ " LE");
                                    }else{
                                        Toast.makeText(RequestInfoActivity.this, "Package not found", Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });

                            mAddressField.setText("Address: "+ dataSnapshot.child("address").getValue(String.class));
                            mDateField.setText("Event Date: "+ dataSnapshot.child("event_date").getValue(String.class));
                            mFromField.setText("Customer Name: "+ dataSnapshot.child("name").getValue(String.class));
                            mStatusField.setText("Status: "+ dataSnapshot.child("status").getValue(String.class));
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                }
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
                startActivity(new Intent(RequestInfoActivity.this, RequestsActivity.class));
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
