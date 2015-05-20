package myaplication.tfg.org.myapplication;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.TextView;


public class MainActivity extends ActionBarActivity {
    private TextView txtView = null;
    private Button but1 = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        View view= View.inflate(this,R.layout.activity_main,null);
        super.onCreate(savedInstanceState);

        setContentView(view);
        AlphaAnimation aa = new AlphaAnimation(0.3f,1.0f);
        aa.setDuration(1500);
        view.startAnimation(aa);
        aa.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationEnd(Animation arg0) {
                redirectTo();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationStart(Animation animation) {
            }

        });

    }

    private void redirectTo() {
        Intent intent = new Intent(this, FisrtPage.class);
        startActivity(intent);
        finish();
    }

}
