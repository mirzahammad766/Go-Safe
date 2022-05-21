package android.job.blescan;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Register_activity extends AppCompatActivity {
    DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReferenceFromUrl("https://gosafe-2ed5c-default-rtdb.firebaseio.com/");
DatabaseReference reference;
    private TextView textView,locat;
private Button reg_button;
private EditText first_name,last_name,phone_no;
public  String location="";
public  String DEFAULT="";
Button locationaccess;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        textView = (TextView) findViewById(R.id.Register_log_inpage);
        first_name=(EditText) findViewById(R.id.input_first);
        last_name=(EditText) findViewById(R.id.input_lastname);
        locat=(TextView) findViewById(R.id.textView4);
        phone_no=(EditText) findViewById(R.id.input_register_phonenumber);
        locationaccess=(Button) findViewById(R.id.getlocation);

        locationaccess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(location==""){

              Intent intent = new Intent(Register_activity.this, location_permission.class);
               startActivity(intent);


                }
                if(location!=""){
                    Intent intent = new Intent(Register_activity.this, locationlistner.class);
                    startActivity(intent);
                }

            }
        });
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                    Intent intent = new Intent(Register_activity.this, Log_in_activity.class);
                    startActivity(intent);


            }
        });
        reg_button = (Button) findViewById(R.id.register_button);

        reg_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String fname=first_name.getText().toString().trim();
                String lname=last_name.getText().toString().trim();
                String phoneno=phone_no.getText().toString();

                SharedPreferences sharedPreferences = getSharedPreferences("LOCAT", MODE_PRIVATE);
                location = sharedPreferences.getString("LOCATION", DEFAULT);


                    if(location.isEmpty()){
                        locat.setError("Add Location");
                    }
                    else{
                        if(fname.isEmpty()){
                            first_name.setError("Enter First Name");
                        }
                        else{
                            if(lname.isEmpty()){
                                last_name.setError("Enter Last Name");
                            }
                            else{
                                if(phoneno.isEmpty()){
                                    phone_no.setError("Enter Phone No");
                                }
                                else{
                                    if(phoneno.length()!=10){
                                     phone_no.setError("Enter Valid Phone No without +92 or 0 ");
                                    }
                                    else{
                                        SharedPreferences sharedPreference=getSharedPreferences("LOCAT",MODE_PRIVATE);
                                        SharedPreferences.Editor editor=sharedPreference.edit();
                                        editor.putString("LOCATION","");
                                        editor.commit();
                                        databaseReference.child("phone_identifier").addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                if (snapshot.hasChild(phoneno))
                                                {
                                                    Toast.makeText(Register_activity.this,"Already Registerd",Toast.LENGTH_SHORT).show();
                                                }
                                                else{
                                                    String value=phoneno;
                                                    List<String> letters = Arrays.asList(value. split(""));
                                                    Collections. shuffle(letters);
                                                    String shuffled = "";
                                                    for (String letter : letters) {
                                                        shuffled += letter;
                                                    }

                                                    System.out.println(shuffled);
                                                    Intent intent = new Intent(Register_activity.this,OTP_Checker.class);
                                                    intent.putExtra("unique_value",shuffled);
                                                    intent.putExtra("first_name",fname);
                                                    intent.putExtra("last_name",lname);
                                                    intent.putExtra("phone_no",phoneno);
                                                    intent.putExtra("location",location);
                                                    startActivity(intent);
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });





                                    }

                                }
                            }
                        }
                    }



            }
        });
    }
}