package android.job.blescan;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class Home_page extends AppCompatActivity {
private Button button;
    private static int splash_timeout=5000;

    String get_phoneno,get_useruuid,get_uniquevalue;
    String DEFAULT="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                SharedPreferences sharedPreferences = getSharedPreferences("MyData", MODE_PRIVATE);
                get_phoneno = sharedPreferences.getString("phone_no", DEFAULT);
                get_useruuid = sharedPreferences.getString("user_uuid", DEFAULT);
                get_uniquevalue = sharedPreferences.getString("unique_value", DEFAULT);
                if (get_phoneno.equals(DEFAULT) || get_useruuid.equals(DEFAULT) || get_uniquevalue.equals(DEFAULT)) {
                    Intent splashintent =new Intent(Home_page.this ,Log_in_activity.class);
                    startActivity(splashintent);
                    finish();
                } else {
                    Intent intent = new Intent(Home_page.this,MainActivity.class);
                    startActivity(intent);
                }

            }
        }, splash_timeout);

    }
}