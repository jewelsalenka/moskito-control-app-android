package com.moskito;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import com.example.moskito_control_app_android.R;
import com.stub.entity.Application;

/**
 * User: Olenka Shemshey
 * Date: 25.07.13
 */
public class HistoryActivity extends Activity {
    private Application currentApp;

    public void onCreate(Bundle bundle){
        super.onCreate(bundle);
        setContentView(R.layout.history);
        Bundle extras = getIntent().getExtras();
        if (extras != null){
            currentApp = (Application) extras.get(ApplicationsListActivity.KEY_APP);
        }
        obtainView();
        initializeList();
    }

    private void obtainView(){
        View header = findViewById(R.id.header);
        header.setBackground(getResources().getDrawable(currentApp.getColor().getColorId()));
        TextView history = (TextView) findViewById(R.id.history_button);
        history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HistoryActivity.this, ApplicationActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable(ApplicationsListActivity.KEY_APP, currentApp);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }

    private void initializeList(){
        HistoryAdapter historyAdapter = new HistoryAdapter(this, currentApp.getHistory());
        ListView lvSimple = (ListView) findViewById(R.id.history_list);
        lvSimple.setAdapter(historyAdapter);
        lvSimple.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


            }
        });

    }
}
