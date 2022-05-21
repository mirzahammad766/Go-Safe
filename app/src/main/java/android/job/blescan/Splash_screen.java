package android.job.blescan;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;


public class Splash_screen extends AppCompatActivity {
    TextView Welcome , SaveYourLife;
    ImageView splashimageview;
    private static int splash_timeout=5000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        Welcome=findViewById(R.id.splash_textview1);
        SaveYourLife=findViewById(R.id.splash_textView2);
        splashimageview=findViewById(R.id.splashid_imageView);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent splashintent =new Intent(Splash_screen.this ,Home_page.class);
                startActivity(splashintent);
                finish();
            }
        }, splash_timeout);
//        Animation Myanimation= AnimationUtils.loadAnimation(Splash_screen.this ,R.anim.animation_1);
//        Welcome.startAnimation(Myanimation);
//        Animation Myanimation2= AnimationUtils.loadAnimation(Splash_screen.this ,R.anim.animation_1);
//        SaveYourLife.startAnimation(Myanimation2);
//        Animation Myanimation3= AnimationUtils.loadAnimation(Splash_screen.this ,R.anim.animation_2);
//        splashimageview.startAnimation(Myanimation3);
    }
}