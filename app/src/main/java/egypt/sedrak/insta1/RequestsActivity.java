package egypt.sedrak.insta1;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class RequestsActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    private TextView noRequestView;

    private Button mDetailsBtn;

    private DatabaseReference mDatabaseCustomers;
    private DatabaseReference mDatabasePhotographers;
    private DatabaseReference mDatabaseRequests;
    private DatabaseReference mDatabasePackages;

    private DatabaseReference mQueryPhotographerDatabaseRequests;
    private DatabaseReference mQueryCustomerDatabaseRequests;

    private com.google.firebase.database.Query photographerQuery;
    private com.google.firebase.database.Query customerQuery;

    private RecyclerView mRequestList;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_requests);
        setTitle("Orders");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.mipmap.ic_arrow_back_white_18dp);
        setSupportActionBar(toolbar);

        mAuth = FirebaseAuth.getInstance();


        mDatabasePhotographers = FirebaseDatabase.getInstance().getReference().child("Users").child("Photographers");
        mDatabaseCustomers = FirebaseDatabase.getInstance().getReference().child("Users").child("Customers");
        mDatabaseRequests = FirebaseDatabase.getInstance().getReference().child("Requests");
        mDatabasePackages = FirebaseDatabase.getInstance().getReference().child("Packages");

        noRequestView = (TextView) findViewById(R.id.noRequests);

        mRequestList = (RecyclerView) findViewById(R.id.request_recyclerView);
        mRequestList.setHasFixedSize(true);
        mRequestList.setLayoutManager(new LinearLayoutManager(this));


    }



    @Override
    protected void onStart() {
        super.onStart();

        resetRecyclerView();
        photographerOrCustomerRequests();



    }

    private void resetRecyclerView(){

        mRequestList.getRecycledViewPool().clear();
        mRequestList.removeAllViewsInLayout();
        mRequestList.removeAllViews();
    }

    private FirebaseRecyclerAdapter<Request, RequestViewHolder> myFirebaseRecyclerViewAdapter(com.google.firebase.database.Query mQuery
    ){

        FirebaseRecyclerAdapter<Request, RequestViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Request, RequestViewHolder>(

                Request.class,
                R.layout.request_row,
                RequestViewHolder.class,
                mQuery
        ) {
            @Override
            protected void populateViewHolder(final RequestViewHolder viewHolder, final Request model, int position) {

                final String requestUID = getRef(position).getKey();

                viewHolder.setStatusType(model.getStatus());
                viewHolder.setEventDate(model.getEvent_date());

                mDatabasePackages.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.hasChild(model.getPackageId())){
                            viewHolder.setCategorieType(dataSnapshot.child(model.getPackageId()).child("categorieLogo").getValue(String.class));
                        }else{
                            Toast.makeText(RequestsActivity.this, "Package not found", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });



                mDetailsBtn = (Button) viewHolder.mView.findViewById(R.id.requestRow_details);
                mDetailsBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mDatabaseRequests.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if(dataSnapshot.hasChild(requestUID)){
                                    Intent InfoIntent = new Intent(RequestsActivity.this, RequestInfoActivity.class);
                                   /*
                                    InfoIntent.putExtra("expire_date",dataSnapshot.child(requestUID).child("expire_date").getValue().toString());
                                    InfoIntent.putExtra("event_date",dataSnapshot.child(requestUID).child("event_date").getValue().toString());
                                    InfoIntent.putExtra("categorie",dataSnapshot.child(requestUID).child("categorie").getValue().toString());
                                    InfoIntent.putExtra("price",dataSnapshot.child(requestUID).child("price").getValue().toString());
                                    InfoIntent.putExtra("address",dataSnapshot.child(requestUID).child("address").getValue().toString());
                                    */
                                    InfoIntent.putExtra("name",dataSnapshot.child(requestUID).child("name").getValue().toString());
                                    InfoIntent.putExtra("status",dataSnapshot.child(requestUID).child("status").getValue().toString());
                                    InfoIntent.putExtra("from",dataSnapshot.child(requestUID).child("from").getValue().toString());
                                    InfoIntent.putExtra("to",dataSnapshot.child(requestUID).child("to").getValue().toString());
                                    InfoIntent.putExtra("telephoneNumber",dataSnapshot.child(requestUID).child("telephoneNumber").getValue().toString());
                                    InfoIntent.putExtra("requestUID", requestUID);
                                    InfoIntent.putExtra("packageUID",model.getPackageId());
                                    startActivity(InfoIntent);
                                    RequestsActivity.this.finish();
                                }
                                else {
                                    Toast.makeText(RequestsActivity.this,"request not found",Toast.LENGTH_LONG).show();
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                    }
                });


            }

        };

        firebaseRecyclerAdapter.notifyDataSetChanged();
        return firebaseRecyclerAdapter;

    }


    public static class RequestViewHolder extends RecyclerView.ViewHolder{

        View mView;

        public RequestViewHolder(View mView) {
            super(mView);
            this.mView = mView;
        }

        public void setCategorieType(String categorieType){
            ImageView categorie = (ImageView) mView.findViewById(R.id.requestRow_categorieType);
            categorie.setImageResource(mView.getContext().getResources().getIdentifier("egypt.sedrak.insta1:mipmap/"
            +categorieType, null, null));
        }

        public void setEventDate (String eventDate){
            TextView event = (TextView) mView.findViewById(R.id.requestRow_date);
            event.setText(eventDate);
        }

        public void setStatusType (String statusType){
            TextView status = (TextView) mView.findViewById(R.id.requestRow_status);
            status.append(statusType);
        }


    }

    private void photographerOrCustomerRequests() {
        mDatabasePhotographers.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(mAuth.getCurrentUser().getUid())) {

                    mQueryPhotographerDatabaseRequests = FirebaseDatabase.getInstance().getReference().child("Requests");
                    photographerQuery = mQueryPhotographerDatabaseRequests.orderByChild("to").equalTo(mAuth.getCurrentUser().getUid());
                    photographerQuery.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            long numOfPhotographerRequests = dataSnapshot.getChildrenCount();

                            if (numOfPhotographerRequests == 0) {
                                resetRecyclerView();
                                noRequestView.setVisibility(View.VISIBLE);
                            } else {
                                noRequestView.setVisibility(View.INVISIBLE);
                                resetRecyclerView();
                                mRequestList.setAdapter(myFirebaseRecyclerViewAdapter(photographerQuery));
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });


                } else {
                    // Toast.makeText(RequestsActivity.this, "not found in photographers", Toast.LENGTH_SHORT).show();


                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mDatabaseCustomers.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild(mAuth.getCurrentUser().getUid())){

                    mQueryCustomerDatabaseRequests = FirebaseDatabase.getInstance().getReference().child("Requests");
                    customerQuery = mQueryCustomerDatabaseRequests.orderByChild("from").equalTo(mAuth.getCurrentUser().getUid());
                    customerQuery.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            long numOfCustomerRequests = dataSnapshot.getChildrenCount();

                            if (numOfCustomerRequests == 0) {
                                resetRecyclerView();
                                noRequestView.setVisibility(View.VISIBLE);
                            } else {
                                noRequestView.setVisibility(View.INVISIBLE);
                                resetRecyclerView();
                                mRequestList.setAdapter(myFirebaseRecyclerViewAdapter(customerQuery));
                            }

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                }else {
                    //Toast.makeText(RequestsActivity.this, "not found in customers", Toast.LENGTH_SHORT).show();
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
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
