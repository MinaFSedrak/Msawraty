package egypt.sedrak.insta1;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener{

    EditText mNameField;
    EditText mEmailField;
    EditText mPasswordField;


    Button mRegisterBtn;
    TextView mAlreadyHaveAccBtn;
    TextView msignupTittle;
    TextView mHaveAccount;

    FirebaseAuth mAuth;

    DatabaseReference mDatabaseUsers;

    ProgressDialog mProgressDialog;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        setTitle("Registration");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mAuth = FirebaseAuth.getInstance();

        mDatabaseUsers = FirebaseDatabase.getInstance().getReference().child("Users");

        mProgressDialog = new ProgressDialog(this);

        mNameField = (EditText) findViewById(R.id.nameField);
        mEmailField = (EditText) findViewById(R.id.emailField);
        mPasswordField = (EditText) findViewById(R.id.passwordField);


        mRegisterBtn = (Button) findViewById(R.id.registerBtn);
        mRegisterBtn.setOnClickListener(this);

        mAlreadyHaveAccBtn = (TextView) findViewById(R.id.alreadyHaveAccount);
        msignupTittle = (TextView) findViewById(R.id.signup_tittle);
        mHaveAccount = (TextView) findViewById(R.id.haveAccount);
        mAlreadyHaveAccBtn.setOnClickListener(this);

        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/AftaSansThin-Regular.otf");
        mRegisterBtn.setTypeface(typeface);
        msignupTittle.setTypeface(typeface);
        mHaveAccount.setTypeface(typeface);

        Typeface typeface1 = Typeface.createFromAsset(getAssets(), "fonts/AftaSansThin-Italic.otf");
        mEmailField.setTypeface(typeface1);
        mPasswordField.setTypeface(typeface1);
        mNameField.setTypeface(typeface1);



    }

    private void startRegister() {
        final String name = mNameField.getText().toString().trim();
        final String email = mEmailField.getText().toString().trim();
        final String password = mPasswordField.getText().toString().trim();

        mProgressDialog.setMessage("Signing Up...");
        mProgressDialog.show();
        mProgressDialog.setCancelable(false);
        mProgressDialog.setCanceledOnTouchOutside(false);

        if(!TextUtils.isEmpty(name) && !TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)){
            mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(Task<AuthResult> task) {
                    if (task.isSuccessful()) {


                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                .setDisplayName(name)
                                .build();
                        FirebaseAuth.getInstance().getCurrentUser().updateProfile(profileUpdates);

                        String user_id = mAuth.getCurrentUser().getUid();

                        DatabaseReference mDatabaseCustomers = mDatabaseUsers.child("Customers");
                        DatabaseReference current_user = mDatabaseCustomers.child(user_id);

                        current_user.child("name").setValue(name);
                        current_user.child("email").setValue(email);
                        mProgressDialog.dismiss();
                        startActivity(new Intent(RegisterActivity.this, MainActivity.class));


                    } else {
                        Toast.makeText(RegisterActivity.this, task.getException().toString(), Toast.LENGTH_LONG).show();
                        mProgressDialog.dismiss();

                    }

                }
            });

        }else {
            Toast.makeText(this, "Invalid Input", Toast.LENGTH_LONG).show();
            mProgressDialog.dismiss();
        }

    }



    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.registerBtn){
            boolean error = false;

            if(mNameField.getText().toString().length() == 0){
                mNameField.setError("Please enter a name");
                error = true;}

            if (mEmailField.getText().toString().length() == 0 ){
                mEmailField.setError("A valid email is required");
                error = true;}

            if (mPasswordField.getText().toString().length() == 0 ){
                mPasswordField.setError("Password must be at least 6 characters");
                error = true;}

            if(error == false){
                startRegister();
            }


        }

        if(v.getId() == R.id.alreadyHaveAccount){
            Intent loginIntent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(loginIntent);
            finish();

        }
    }


}
