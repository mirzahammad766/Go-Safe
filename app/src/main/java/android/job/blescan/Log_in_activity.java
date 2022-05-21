package android.job.blescan;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class Log_in_activity extends AppCompatActivity {
    private Button button;
   private TextView textView;
   EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        textView=(TextView)  findViewById(R.id.Log_in_register_Pge);
        editText=findViewById(R.id.input_phonenumber);

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Log_in_activity.this, Register_activity.class);
                startActivity(intent);
            }
        });

            button=(Button) findViewById(R.id.verify_login_button);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String inp=editText.getText().toString();
                    if(inp.isEmpty() || inp.length()!=10){
                        Toast.makeText(Log_in_activity.this,"Add Number Without +92 or 0",Toast.LENGTH_SHORT).show();
                        editText.setError("Add Valid Number Without +92 or 0");


                    }
                    else{
                        Intent intent = new Intent(Log_in_activity.this,login_otp.class);
                        intent.putExtra("phone_no",inp);
                        startActivity(intent);
                    }

                }
            });
    }}