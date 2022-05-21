package android.job.blescan;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.Manifest;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.text.format.Time;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import no.nordicsemi.android.support.v18.scanner.BluetoothLeScannerCompat;
import no.nordicsemi.android.support.v18.scanner.ScanCallback;
import no.nordicsemi.android.support.v18.scanner.ScanResult;
import no.nordicsemi.android.support.v18.scanner.ScanSettings;

import static android.job.blescan.Blutoothsetup.advertise;

public class Analytics_activity extends AppCompatActivity {
    SQLiteDataBaseGoSafe db;
    SimpleDateFormat formatter = new SimpleDateFormat("mm");
    List<ScanResult> scandevices = new ArrayList<>();
    ArrayList<String> maclist = new ArrayList<>();
    HashMap<String, Integer> forTime = new HashMap<String, Integer>();
    HashMap<String, Integer> firstTime = new HashMap<String, Integer>();

    private int LOCATION_SETTINGS_REQUEST = 123;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analytics);
        db = new SQLiteDataBaseGoSafe(this);
        String[] perms = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
        ActivityCompat.requestPermissions(this, perms, 2222);
        enableLoc();
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (!bluetoothAdapter.isEnabled()) {
            Intent turnOn = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(turnOn, 0);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions

                String usermacAdress = bluetoothAdapter.getAddress().toString();
                bluetoothAdapter.setName(usermacAdress);
                advertise(bluetoothAdapter);
                return;
            }

        } else {

            String userMacAdress = bluetoothAdapter.getAddress().toString();
            bluetoothAdapter.setName(userMacAdress);

            advertise(bluetoothAdapter);


            Toast.makeText(getApplicationContext(), "Already on", Toast.LENGTH_LONG).show();
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    void compatscan() {
        BluetoothLeScannerCompat scanner = BluetoothLeScannerCompat.getScanner();
        ScanSettings settings = new ScanSettings.Builder()
                .setScanMode(ScanSettings.SCAN_MODE_BALANCED)
                .build();
        ScanCallback scanCallback = new ScanCallback() {

            @Override
            public void onScanResult(int callbackType, @NonNull ScanResult result) {
                super.onScanResult(callbackType, result);

                if (!maclist.contains(result.getDevice().getAddress())) {

                    double bleDistance = Math.pow(10, ((-69 - (result.getRssi())) / (10 * 2)));
                    if (ActivityCompat.checkSelfPermission(Analytics_activity.this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.

                        if (bleDistance <= 1.8288) {
                            Date date = new Date();
                            scandevices.add(result);

                            maclist.add(result.getDevice().getAddress());
                            firstTime.put(result.getDevice().getAddress(),Integer.parseInt(formatter.format(date)));
                            forTime.put(result.getDevice().getAddress(),Integer.parseInt(formatter.format(date)));
                        }

                        return;
                    }


                }
                else{
                    Date date = new Date();
                    int a= Integer.parseInt(formatter.format(date));
                    forTime.put(result.getDevice().getAddress(),a);
                    workingfortime(result.getDevice().getAddress());
                }

            }
        };
        scanner.startScan(null, settings, scanCallback);
    }

    private void enableLoc() {

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(30 * 1000);
        locationRequest.setFastestInterval(5 * 1000);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);

        builder.setAlwaysShow(true);

        Task<LocationSettingsResponse> result =
                LocationServices.getSettingsClient(this).checkLocationSettings(builder.build());

        result.addOnCompleteListener(new OnCompleteListener<LocationSettingsResponse>() {

            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onComplete(Task<LocationSettingsResponse> task) {
                try {
                    LocationSettingsResponse response = task.getResult(ApiException.class);

                    compatscan();

                } catch (ApiException exception) {
                    switch (exception.getStatusCode()) {
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:

                            try {

                                ResolvableApiException resolvable = (ResolvableApiException) exception;

                                resolvable.startResolutionForResult(
                                        Analytics_activity.this,
                                        LOCATION_SETTINGS_REQUEST);
                            } catch (IntentSender.SendIntentException e) {

                            } catch (ClassCastException e) {

                            }
                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:

                            break;
                    }
                }

            }
        });

    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode==LOCATION_SETTINGS_REQUEST){
            compatscan();
        }
        else
            enableLoc();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onStart() {
        super.onStart();
        compatscan();
    }
    public void workingfortime(String re){
        int b=forTime.get(re);
        int a=firstTime.get(re);

        if(a!=b && b-a>=1)
        {
         //justformac.setText(re);
        System.out.print("in working");
        }
    }
}