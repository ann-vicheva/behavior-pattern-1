package com.example.pp_lab3_poved;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void iterator(View view){
        Intent intent = new Intent(MainActivity.this, IteratorActivity.class);
        startActivity(intent);
    }

    public void mediator(View view){
        Intent intent = new Intent(MainActivity.this, MediatorActivity.class);
        startActivity(intent);
    }

    public void listener(View view){
        Intent intent = new Intent(MainActivity.this, ListenerActivity.class);
        startActivity(intent);
    }

    public void strategy(View view){
        Intent intent = new Intent(MainActivity.this, StrategyActivity.class);
        startActivity(intent);
    }

    public void chain(View view){
        Intent intent = new Intent(MainActivity.this, ChainActivity.class);
        startActivity(intent);
    }

    public void visitor(View view){
        Intent intent = new Intent(MainActivity.this, VisitorActivity.class);
        startActivity(intent);
    }
}
