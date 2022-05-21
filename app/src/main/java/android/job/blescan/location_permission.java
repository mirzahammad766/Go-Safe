package android.job.blescan;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Toast;

public class location_permission extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_permission);
        if (ContextCompat.checkSelfPermission(location_permission.this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){


            if (ActivityCompat.shouldShowRequestPermissionRationale(location_permission.this,
                    Manifest.permission.ACCESS_FINE_LOCATION)){
                ActivityCompat.requestPermissions(location_permission.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);


            }else{
                ActivityCompat.requestPermissions(location_permission.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);


            }
        }
        else{

            Intent intent = new Intent(location_permission.this, locationlistner.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(location_permission.this,
                            Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(i);
                        Intent intent = new Intent(location_permission.this, locationlistner.class);
                        startActivity(intent);
                        finish();



                    }
                } else {

                    Intent intent = new Intent(location_permission.this, Register_activity.class);
                    startActivity(intent);
                }


                return;
            }

        }
    }
}