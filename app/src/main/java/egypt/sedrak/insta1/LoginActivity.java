package egypt.sedrak.insta1;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthCredential;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.Version;
import com.restfb.types.User;

import org.json.JSONObject;

import java.util.Arrays;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText mloginEmail;
    private EditText mloginPassword;

    private Button mloginBtn;
    private TextView mloginNewAccBtn;
    private TextView mTittle;
    private TextView mdontHaveAccount;
    private TextView mOr;

    private Button mGoogleBtn;

    private static final int RC_SIGN_IN = 1;
    private static final String TAG = "LoginActivity";
    private GoogleApiClient mGoogleApiClient;

    private FirebaseAuth mAuth;

    private DatabaseReference mDatabaseUsers;
    private DatabaseReference mDatabaseCustomers;
    private DatabaseReference mDatabasePhotographers;

    private ProgressDialog mProgress;

    private CallbackManager mCallBackManager;
    private LoginButton mFacebookBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setTitle("Login");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mAuth = FirebaseAuth.getInstance();

        /*Drawable icon = ContextCompat.getDrawable(getApplicationContext(), R.drawable.common_google_signin_btn_icon_light);
        int WIcon = icon.getIntrinsicWidth();
        int HIcon = icon.getIntrinsicHeight();
        icon.setBounds(0,0, WIcon/10,HIcon/10);
        mGoogleBtn.setCompoundDrawables(icon, null, null, null);*/




        mCallBackManager = CallbackManager.Factory.create();
        mFacebookBtn = (LoginButton) findViewById(R.id.facebookLoginBtn);


        mDatabaseUsers = FirebaseDatabase.getInstance().getReference().child("Users");
        mDatabaseCustomers = mDatabaseUsers.child("Customers");
        mDatabasePhotographers = mDatabaseUsers.child("Photographers");

        mProgress = new ProgressDialog(this);

        mloginEmail = (EditText) findViewById(R.id.loginEmailField);
        mloginPassword = (EditText) findViewById(R.id.loginPasswordField);

        mloginBtn = (Button) findViewById(R.id.loginBtn);
        mloginNewAccBtn = (TextView) findViewById(R.id.newAccountBtn);
        mTittle = (TextView) findViewById(R.id.signin_tittle);
        mdontHaveAccount = (TextView) findViewById(R.id.dontHaveAccount);
        mOr = (TextView) findViewById(R.id.or);
        mGoogleBtn = (Button) findViewById(R.id.googleBtn);

        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/AftaSansThin-Regular.otf");
        mFacebookBtn.setTypeface(typeface);
        mGoogleBtn.setTypeface(typeface);
        mTittle.setTypeface(typeface);
        mloginBtn.setTypeface(typeface);
        mdontHaveAccount.setTypeface(typeface);
        mloginNewAccBtn.setTypeface(typeface);
        mOr.setTypeface(typeface);

        Typeface typeface1 = Typeface.createFromAsset(getAssets(), "fonts/AftaSansThin-Italic.otf");
        mloginEmail.setTypeface(typeface1);
        mloginPassword.setTypeface(typeface1);

        mloginBtn.setOnClickListener(this);
        mloginNewAccBtn.setOnClickListener(this);
        mGoogleBtn.setOnClickListener(this);



        // Facebook Sign in
        mFacebookBtn.setReadPermissions(Arrays.asList("email"));
        mFacebookBtn.registerCallback(mCallBackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Toast.makeText(getApplicationContext(), "Facebook Authentication canceled", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onError(FacebookException error) {
                Toast.makeText(getApplicationContext(), "Facebook Authentication error "+ error, Toast.LENGTH_SHORT).show();

            }
        });

        // Google Sign in
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .requestProfile()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(ConnectionResult connectionResult) {
                        String error = connectionResult.getErrorMessage();
                        Log.e("GoogleSign", error);
                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

    }

    @Override
    protected void onStart() {
        super.onStart();


        // Getting User's manage pages on Facebook but app doesn't has permission
        /*LoginManager.getInstance().logInWithPublishPermissions(this, Arrays.asList("publish_pages","manage_pages"));

        LoginManager.getInstance().registerCallback(mCallBackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {

                        GraphRequest request = GraphRequest.newMeRequest(
                                loginResult.getAccessToken(),
                                new GraphRequest.GraphJSONObjectCallback() {
                                    @Override
                                    public void onCompleted(
                                            JSONObject object,
                                            GraphResponse response) {
                                        System.out.println(object.toString());

                                    }
                                });
                        Bundle parameters = new Bundle();
                        parameters.putString("fields", "email");
                        request.setParameters(parameters);
                        request.executeAsync();
                    }

                    @Override
                    public void onCancel() {
                        // App code
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        // App code
                    }
                });
*/

        /*String accessToken = "EAACEdEose0cBAFdEQQYW9t0a5ZCQGnOIBNDY8IlT1cSZCNdmKRuZAEvftEJZBezXuXFSYOZBfzyhwAa1uX9fp48yZB2EzVj6N4Ws0wOlI15uBxgxZBFjamZAJPTks2gINmxYBZBETMIFWiS0UJZCw0BgYtOU4QuhYY18Q1DnTGZAcvkQDeczNHUiiJUlxpPSOtU7EIZD";
        final FacebookClient mFacbookClient = new DefaultFacebookClient(accessToken, Version.LATEST);

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                try {
                User me = mFacbookClient.fetchObject("me", User.class);
                Log.i("Facebook",me.getName() + me.getGender());
                } catch(com.restfb.exception.FacebookException ex) {
                    Log.i("Facebook error", "" + ex.getMessage());
                }

            }
        });
*/

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mCallBackManager.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);

            mProgress.setMessage("Google Signing in...");
            mProgress.show();
            mProgress.setCancelable(false);
            mProgress.setCanceledOnTouchOutside(false);

            if (result.isSuccess()) {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
                Toast.makeText(this,"Google SignIn Successful",Toast.LENGTH_LONG).show();

            } else {
                // Google Sign In failed, update UI appropriately
                // ...
                Toast.makeText(this,"Google SignIn failed "+ result.getStatus(),Toast.LENGTH_LONG).show();
                mProgress.dismiss();
            }
        }
    }

    // Google Sign In
    private void googleSignIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void firebaseAuthWithGoogle(final GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithCredential", task.getException());
                            Toast.makeText(getApplicationContext(), "Authentication failed "+ task.getException(),
                                    Toast.LENGTH_SHORT).show();
                            mProgress.dismiss();

                        }else{

                            mDatabasePhotographers.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (!dataSnapshot.hasChild(mAuth.getCurrentUser().getUid())) {
                                        DatabaseReference current_user = mDatabaseCustomers.child(mAuth.getCurrentUser().getUid());

                                        current_user.child("name").setValue(mAuth.getCurrentUser().getDisplayName());
                                        current_user.child("email").setValue(mAuth.getCurrentUser().getEmail());
                                        checkUserExist("Customer");
                                    }
                                    else if(dataSnapshot.hasChild(mAuth.getCurrentUser().getUid())) {
                                        checkUserExist("Photographer");
                                    }
                                    else{
                                        Toast.makeText(getApplicationContext(),"You aren't User Setup your account",Toast.LENGTH_LONG).show();

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

    // Facebook Sign In

    private void handleFacebookAccessToken(AccessToken accessToken) {

        mProgress.setMessage("Facebook Signing in...");
        mProgress.show();
        mProgress.setCancelable(false);
        mProgress.setCanceledOnTouchOutside(false);
        AuthCredential credential = FacebookAuthProvider.getCredential(accessToken.getToken());
        mAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(!task.isSuccessful()){
                    Log.w(TAG, "signInWithCredential", task.getException());
                    Toast.makeText(LoginActivity.this, "Authentication failed.",
                            Toast.LENGTH_SHORT).show();
                    mProgress.dismiss();
                }
                else{


                    mDatabasePhotographers.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (!dataSnapshot.hasChild(mAuth.getCurrentUser().getUid())) {
                                DatabaseReference current_user = mDatabaseCustomers.child(mAuth.getCurrentUser().getUid());

                                current_user.child("name").setValue(mAuth.getCurrentUser().getDisplayName());
                                current_user.child("email").setValue(mAuth.getCurrentUser().getEmail());
                                checkUserExist("Customer");
                            }
                            else if(dataSnapshot.hasChild(mAuth.getCurrentUser().getUid())) {
                                checkUserExist("Photographer");
                            }
                            else{
                                Toast.makeText(LoginActivity.this,"You aren't User Setup your account",Toast.LENGTH_LONG).show();

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



    //Authentication validation
    private void checkLogin() {
        String email = mloginEmail.getText().toString().trim();
        String password = mloginPassword.getText().toString().trim();
        mProgress.setMessage("Signing in...");
        mProgress.show();
        mProgress.setCancelable(false);
        mProgress.setCanceledOnTouchOutside(false);


        if(!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)){

            mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(Task<AuthResult> task) {

                    if(task.isSuccessful()){
                        checkUserExist("Both");
                        mProgress.dismiss();
                    }
                    else {
                        Toast.makeText(LoginActivity.this, "Incorrect email & password", Toast.LENGTH_LONG).show();
                        mProgress.dismiss();
                    }
                }
            });



        }
        else {
            Toast.makeText(LoginActivity.this, "Invalid inputs", Toast.LENGTH_LONG).show();
            mProgress.dismiss();

        }


    }

    //Database validation
    private void checkUserExist(String userType) {

        final String user_id = mAuth.getCurrentUser().getUid();
        if(userType.equals("Customer") || userType.equals("Both")){
            mDatabaseCustomers.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if (dataSnapshot.hasChild(user_id)) {

                        Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
                        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(mainIntent);
                    } else {

                        Log.i("LoginType", "You are not a Customer Setup your account");

                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }

        if (userType.equals("Photographer") || userType.equals("Both") ){
            mDatabasePhotographers.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if (dataSnapshot.hasChild(user_id)){
                        Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
                        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(mainIntent);
                    }
                    else {
                        Log.i("LoginType", "You are not a Photographer Setup your account");
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }




    }


    @Override
    public void onClick(View v) {

        if(v.getId() == R.id.loginBtn){
            boolean error = false;

            if(mloginEmail.getText().toString().length() == 0){
                mloginEmail.setError("A valid email is required");
                error = true;}

            if (mloginPassword.getText().toString().length() == 0){
                mloginPassword.setError("Password must be at least 6 characters");
                error = true;}

            if(error == false){
                checkLogin();
            }


        }

        if(v.getId() == R.id.newAccountBtn){
            startActivity(new Intent(LoginActivity.this,RegisterActivity.class));
        }

        if(v.getId() == R.id.googleBtn){
            googleSignIn();
        }


    }


}
