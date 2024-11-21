package wendu.dsbridge.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.TextView;

public class StartActivity extends AppCompatActivity {

    private TextView time;

    CountDownTimer countDownTimer = new CountDownTimer(3300, 1000) {
        @Override
        public void onTick(long millisUntilFinished) {
            time.setText((millisUntilFinished/1000)+"秒");
        }

        @Override
        public void onFinish() {
            startToMain();
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//强制竖屏

        super.onCreate(savedInstanceState);

        countDownTimer.start();

    }


    public void jumpClick(View view){
        countDownTimer.onFinish();
    }

    public void startToMain(){
//        startActivity(new Intent(StartActivity.this,MainActivity.class));
        finish();
    }
}
