package com.buyi.dex65536;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.lang.reflect.Method;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        new Thread() {
            @Override
            public void run() {
                Class<?>[] cs = new Class[] {
                        com.buyi.dex65536.methodpools.A.class,
                        com.buyi.dex65536.methodpools.B.class,
                        com.buyi.dex65536.methodpools.C.class,
                        com.buyi.dex65536.methodpools.D.class,
                        com.buyi.dex65536.methodpools.E.class,
                        com.buyi.dex65536.methodpools.F.class,
                        com.buyi.dex65536.methodpools.A.class };

                for (Class<?> c : cs) {
                    curC = c;
                    try {
                        Object instance = c.newInstance();
                        for (curI = 0; curI < 10000; curI++) {
                            Method m = c.getDeclaredMethod("method_" + curI);
                            m.invoke(instance);
                        }
                    } catch (Exception e) {
                        throw new RuntimeException("Fail to load class " + c, e);
                    }
                }

                curI = -1;
            }
        }.start();

        new Handler() {
            @Override
            public void handleMessage(Message msg) {
                TextView text = (TextView) findViewById(R.id.progress);
                if (curI != -1) {
                    text.setText(curC + " / " + curI);
                    sendEmptyMessageDelayed(1, 100);
                } else {
                    text.setText("DONE");
                }
            }
        }.sendEmptyMessage(1);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    Class<?> curC;
    int curI;
}



