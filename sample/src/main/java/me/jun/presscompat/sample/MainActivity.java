package me.jun.presscompat.sample;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import me.jun.presscompat.PressCompat;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView button = (TextView) findViewById(R.id.textView);
        PressCompat.pressableBackground(button);

        Button button1 = (Button) findViewById(R.id.button);
        PressCompat.pressableBackground(button1);

        View view = findViewById(R.id.imageView);
        PressCompat.pressableBackground(view);
    }
}
