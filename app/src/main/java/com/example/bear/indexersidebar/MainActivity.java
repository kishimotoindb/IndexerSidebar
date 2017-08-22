package com.example.bear.indexersidebar;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final TextView tvShow = (TextView) findViewById(R.id.tvShow);
        IndexerSideBar indexer = (IndexerSideBar) findViewById(R.id.indexer);
        indexer.setOnIndexerChangeListener(new IndexerSideBar.OnTouchIndexerListener() {
            @Override
            public void onIndexerChange(String indexer) {
                tvShow.setText(indexer);
            }

            @Override
            public void onFingerUp() {
                tvShow.setText("finger up");
            }
        });
    }
}
