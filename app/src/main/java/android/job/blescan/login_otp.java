package android.job.blescan;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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

import java.util.concurrent.TimeUnit;

public class login_otp extends AppCompatActivity {
    DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReferenceFromUrl("https://gosafe-2ed5c-default-rtdb.firebaseio.com/");
    private Button button;
    boolean otpsent=false;
    private String check="123";
    private EditText edittext_otp;
    DatabaseReference reference;
    TextView error;
    String id="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_otp);
        FirebaseApp.initializeApp(this);
        FirebaseAuth firebaseAuth=FirebaseAuth.getInstance();
        edittext_otp=findViewById(R.id.input_otp);
        button=(Button) findViewById(R.id.verify_otp_button);
        error=findViewById(R.id.textView4_error);


        Intent i = getIntent();
        String phoneno=i.getExtras().getString("phone_no");

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                error.setText(phoneno);

                if(otpsent){
                    final String otpp=edittext_otp.getText().toString();

                    if(id.isEmpty()){
                        Toast.makeText(login_otp.this,"Unable to verify",Toast.LENGTH_SHORT).show();
                        error.setText("Unable to verify");
                    }
                    else{
                        PhoneAuthCredential phoneAuthCredential= PhoneAuthProvider.getCredential(id,otpp);
                        firebaseAuth.signInWithCredential(phoneAuthCredential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()){
                                    FirebaseUser userdetail=task.getResult().getUser();
                                    error.setText("verified");
                                    String userrrid=userdetail.getUid();
                                    reference= FirebaseDatabase.getInstance().getReference().child("user_info");

                                    reference.addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                                            for(DataSnapshot ds : snapshot.getChildren()) {

                                                if (ds.child("user_id").getValue().equals(userrrid)) {
                                                    check="321";
                                                    String pno = ds.child("phone_no").getValue().toString();
                                                    String ui = ds.child("user_id").getValue().toString();
                                                    String uv = ds.child("unique_value").getValue().toString();

                                                    Toast.makeText(login_otp.this,"Verification SuccesssFull",Toast.LENGTH_SHORT).show();
                                                    SharedPreferences sharedPreference=getSharedPreferences("MyData",MODE_PRIVATE);
                                                    SharedPreferences.Editor editor=sharedPreference.edit();
                                                    editor.putString("phone_no",pno);
                                                    editor.putString("user_uuid",ui);
                                                    editor.putString("unique_value",uv);
                                                    editor.commit();
                                                    Intent intent = new Intent(login_otp.this,Setup_activity.class);
                                                    startActivity(intent);
                                                    finish();
                                                }

                                            }
                                            if(check=="123"){
                                                Toast.makeText(login_otp.this,"You Don't Have Account Yet Kindly Register First",Toast.LENGTH_SHORT).show();
                                                Intent intent = new Intent(login_otp.this,Register_activity.class);
                                                startActivity(intent);
                                                finish();
                                            }
                                        }
                                        //this method will be called if unable to fetch the record
                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {
                                            Toast.makeText(getApplicationContext(),"Error fetching the data", Toast.LENGTH_SHORT).show();
                                        }
                                    });









                                }
                                else{
                                    Toast.makeText(login_otp.this,"Something went wrong",Toast.LENGTH_SHORT).show();
                                    error.setText("Something went wrong");
                                }
                            }
                        });
                    }
                }
                else {


                    PhoneAuthOptions options =
                            PhoneAuthOptions.newBuilder(firebaseAuth)
                                    .setPhoneNumber("+92"+phoneno)       // Phone number to verify
                                    .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                                    .setActivity(login_otp.this)                 // Activity (for callback binding)
                                    .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks(){
                                        @Override
                                        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                                            Toast.makeText(login_otp.this,"Successfully sent",Toast.LENGTH_SHORT).show();
                                            error.setText("successfully sent");
                                        }

                                        @Override
                                        public void onVerificationFailed(@NonNull FirebaseException e) {
                                            Toast.makeText(login_otp.this,"Something went wrong="+ e,Toast.LENGTH_SHORT).show();
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