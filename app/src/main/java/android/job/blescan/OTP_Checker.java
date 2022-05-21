package android.job.blescan;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.concurrent.TimeUnit;

public class OTP_Checker extends AppCompatActivity {
    DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReferenceFromUrl("https://gosafe-2ed5c-default-rtdb.firebaseio.com/");
    private Button button;
    boolean otpsent=false;
    private EditText edittext_otp;
    TextView error;
    String id="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp_checker);
        FirebaseApp.initializeApp(this);
        FirebaseAuth firebaseAuth=FirebaseAuth.getInstance();
        edittext_otp=findViewById(R.id.input_otp);
        button=(Button) findViewById(R.id.verify_otp_button);
        error=findViewById(R.id.textView4_error);

           Intent i = getIntent();
             String fname=i.getExtras().getString("first_name");
             String lname=i.getExtras().getString("last_name");
             String phoneno=i.getExtras().getString("phone_no");
             String location=i.getExtras().getString("location");
             String unique_value=i.getExtras().getString("unique_value");



        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if(otpsent){
                    final String otpp=edittext_otp.getText().toString();

                if(id.isEmpty()){
                    Toast.makeText(OTP_Checker.this,"Unable to verify",Toast.LENGTH_SHORT).show();
                    error.setText("Unable to verify");
                }
                else{
                    PhoneAuthCredential phoneAuthCredential=PhoneAuthProvider.getCredential(id,otpp);
                    firebaseAuth.signInWithCredential(phoneAuthCredential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            FirebaseUser userdetail=task.getResult().getUser();
                            Toast.makeText(OTP_Checker.this,"Verification Successfull",Toast.LENGTH_SHORT).show();
                            error.setText("verified");

                            String useruuid=userdetail.getUid().toString();









                           DatabaseReference uniqueKeyRef = databaseReference.child("user_info").child(useruuid);
                             DatabaseReference uniqueKeyRef2 = databaseReference.child("phone_identifier").child(phoneno);
                             databaseReference.child("phone_identifier").addListenerForSingleValueEvent(new ValueEventListener() {
                                 @Override
                                 public void onDataChange(@NonNull DataSnapshot snapshot) {
                                          uniqueKeyRef2.child("unique_value").setValue(unique_value);
                                          uniqueKeyRef2.child("user_id").setValue(useruuid);
                                          uniqueKeyRef2.child("phone_no").setValue(phoneno);

                                 }

                                 @Override
                                 public void onCancelled(@NonNull DatabaseError error) {

                                 }
                             });
                           databaseReference.child("user_info").addListenerForSingleValueEvent(new ValueEventListener(){
                               @Override
                               public void onDataChange(@NonNull DataSnapshot snapshot) {

                                    uniqueKeyRef.child("user_id").setValue(useruuid);
                                    uniqueKeyRef.child("first_name").setValue(fname);
                                    uniqueKeyRef.child("last_name").setValue(lname);
                                    uniqueKeyRef.child("phone_no").setValue(phoneno);
                                    uniqueKeyRef.child("location").setValue(location);
                                    uniqueKeyRef.child("unique_value").setValue(unique_value);

                               }

                               @Override
                               public void onCancelled(@NonNull DatabaseError error) {

                               }
                           }  );

                            SharedPreferences sharedPreference=getSharedPreferences("MyData",MODE_PRIVATE);
                            SharedPreferences.Editor editor=sharedPreference.edit();
                            editor.putString("phone_no",phoneno);
                            editor.putString("user_uuid",useruuid);
                            editor.putString("unique_value",unique_value);
                            editor.commit();



                            Intent intent = new Intent(OTP_Checker.this,Setup_activity.class);
                            startActivity(intent);



                        }
                        else{
                            Toast.makeText(OTP_Checker.this,"Something went wrong",Toast.LENGTH_SHORT).show();

                        }
                        }
                    });
                }
                }
                else {
                    final String mobileno="+923107977598";

                    PhoneAuthOptions options =
                            PhoneAuthOptions.newBuilder(firebaseAuth)
                                    .setPhoneNumber("+92"+phoneno)       // Phone number to verify
                                    .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                                    .setActivity(OTP_Checker.this)                 // Activity (for callback binding)
                                    .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks(){
                                        @Override
                                        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                                            Toast.makeText(OTP_Checker.this,"Successfully sent",Toast.LENGTH_SHORT).show();
                                            error.setText("successfully sent");
                                        }

                                        @Override
                                        public void onVerificationFailed(@NonNull FirebaseException e) {
                                            Toast.makeText(OTP_Checker.this,"Something went wrong=",Toast.LENGTH_SHORT).show();
                                            error.setText("Something went wrong="+ e);
                                        }

                                        @Override
                                        public void onCodeSent(@NonNull String s,@NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken){
                                            super.onCodeSent(s,forceResendingToken);
                                            edittext_otp.setVisibility(View.VISIBLE);
                                            button.setText("Verify OTP");
                                            id = s;
                                            otpsent=true;
                                        }
                                    })          // OnVerificationStateChangedCallbacks
                                    .build();
                    PhoneAuthProvider.verifyPhoneNumber(options);


                }
            }
        });


    


    }
}