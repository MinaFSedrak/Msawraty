package egypt.sedrak.insta1;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class VerifiedRequest extends AppCompatActivity {

    private final int VERIFIED_SCREEN_LENGTH = 5000;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verified_request);
        setTitle("Verification");

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                /* Create an Intent that will start the Menu-Activity. */
                Intent mainIntent = new Intent(VerifiedRequest.this, MainActivity.class);
                mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                VerifiedRequest.this.startActivity(mainIntent);
                VerifiedRequest.this.finish();
            }
        }, VERIFIED_SCREEN_LENGTH);
    }

}
