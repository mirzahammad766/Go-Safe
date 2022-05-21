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
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import no.nordicsemi.android.support.v18.scanner.BluetoothLeScannerCompat;
import no.nordicsemi.android.support.v18.scanner.ScanCallback;
import no.nordicsemi.android.support.v18.scanner.ScanResult;
import no.nordicsemi.android.support.v18.scanner.ScanSettings;
import static android.job.blescan.Blutoothsetup.advertise;
public class MainActivity extends AppCompatActivity {

    private static final String DEFAULT = "";
    SQLiteDataBaseGoSafe db;
    Recycleradater adapter;
    SimpleDateFormat formatter = new SimpleDateFormat("mm");
    List<ScanResult> scandevices = new ArrayList<>();
    ArrayList<String> maclist = new ArrayList<>();
    HashMap<String, Integer> forTime = new HashMap<String, Integer>();
    HashMap<String, Integer> firstTime = new HashMap<String, Integer>();
    private RecyclerView recyclerView;
    Button showalldata;
    TextView justformac;
    String bluetoothAdapterName = "";
    private int LOCATION_SETTINGS_REQUEST = 123;
    String get_phoneno,get_useruuid,get_uniquevalue;
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        justformac = findViewById(R.id.textView);
        SharedPreferences sharedPreferences = getSharedPreferences("MyData", MODE_PRIVATE);
         get_phoneno = sharedPreferences.getString("phone_no", DEFAULT);
         get_useruuid = sharedPreferences.getString("user_uuid", DEFAULT);
         get_uniquevalue = sharedPreferences.getString("unique_value", DEFAULT);
        if (get_phoneno.equals(DEFAULT) || get_useruuid.equals(DEFAULT) || get_uniquevalue.equals(DEFAULT)) {

        } else {

            justformac.setText(get_useruuid);

        }

        bluetoothAdapterName = get_uniquevalue;

        showalldata = (Button) findViewById(R.id.button_showdatasqlite);
        showalldata.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    String s=db.getData(get_uniquevalue);


                } catch (InterruptedException e) {
                    e.printStackTrace();
                }


            }
        });
        String[] perms = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
        ActivityCompat.requestPermissions(this, perms, 2222);
        enableLoc();
        db = new SQLiteDataBaseGoSafe(this);
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (!bluetoothAdapter.isEnabled()) {
            Intent turnOn = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(turnOn, 0);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions

                bluetoothAdapter.setName(bluetoothAdapterName);
                advertise(bluetoothAdapter);
                recyclerView = findViewById(R.id.recycler);
                recyclerView.setLayoutManager(new LinearLayoutManager(this));
                adapter = new Recycleradater(scandevices);
                recyclerView.setAdapter(adapter);
                return;
            }

        } else {

            bluetoothAdapter.setName(bluetoothAdapterName);
            advertise(bluetoothAdapter);
            recyclerView = findViewById(R.id.recycler);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            adapter = new Recycleradater(scandevices);
            recyclerView.setAdapter(adapter);
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


                        if (bleDistance <= 1.8288) {
                            Date date = new Date();
                            scandevices.add(result);
                            Dialog d = new Dialog(MainActivity.this);
                            d.setTitle("Error Msg");
                            TextView tv = new TextView(MainActivity.this);
                            tv.setText("" + result);
                            d.setContentView(tv);

                            maclist.add(result.getDevice().getAddress());
                            recyclerView.setAdapter(new Recycleradater(scandevices));
                            firstTime.put(result.getDevice().getAddress(), Integer.parseInt(formatter.format(date)));
                            forTime.put(result.getDevice().getAddress(), Integer.parseInt(formatter.format(date)));
                        }


                    } else {
                        Date date = new Date();
                        int a = Integer.parseInt(formatter.format(date));
                        forTime.put(result.getDevice().getAddress(), a);

                        String allvalues = result.getScanRecord().getDeviceName();

                        if (allvalues != null && allvalues.length()==10) {
                            workingfortime(result.getDevice().getAddress(), allvalues);
                        }
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
                                        MainActivity.this,
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
    public void workingfortime(String re,String na){
        int b=forTime.get(re);
        int a=firstTime.get(re);
        if(a!=b && b-a>=5)
        {
            justformac.setText(re);


            try{

                int userid =123;
                String contactid = re;
                Date c = Calendar.getInstance().getTime();
                SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
                String formattedDate = df.format(c);
                String date= formattedDate;
                String time= new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
                db.insertcontactlist(na,date,time);
                 Dialog d = new Dialog(this);
                 d.setTitle("Success inserted");
                 TextView tv=new TextView(this);
                 tv.setText("MAc="+re);
                 d.setContentView(tv);

                if (isConnected()) {

                    try {
                        db.getData(get_uniquevalue);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } else {

                }

            }
            catch (Exception e) {
                String error = e.toString();
                Dialog d = new Dialog(this);
                d.setTitle("Error Msg");
                TextView tv = new TextView(this);
                tv.setText(error);
                d.setContentView(tv);

            } finally {
                firstTime.put(re,b);
            }
        }
    }
    public boolean isConnected() {
        boolean connected = false;
        try {
            ConnectivityManager cm = (ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo nInfo = cm.getActiveNetworkInfo();
            connected = nInfo != null && nInfo.isAvailable() && nInfo.isConnected();
            return connected;
        } catch (Exception e) {
            Log.e("Connectivity Exception", e.getMessage());
        }
        return connected;
    }
}