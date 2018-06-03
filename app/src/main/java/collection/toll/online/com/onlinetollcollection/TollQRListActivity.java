package collection.toll.online.com.onlinetollcollection;

import android.graphics.Movie;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;

import collection.toll.online.com.onlinetollcollection.database.SQLiteAdapter;
import collection.toll.online.com.onlinetollcollection.database.Toll;
import collection.toll.online.com.onlinetollcollection.java_class.TollAdapter;


public class TollQRListActivity extends AppCompatActivity {
RecyclerView recyclerView;
TollAdapter mAdapter;
    ArrayList<Toll> tollList;
    SQLiteAdapter dbAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_toll_qrlist);
        setTitle("Toll List");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        recyclerView= (RecyclerView) findViewById(R.id.recyclerView);
        dbAdapter=new SQLiteAdapter(getApplicationContext());
        dbAdapter.openToRead();
        tollList=dbAdapter.retrieveAllToll();
        dbAdapter.close();
        mAdapter = new TollAdapter(tollList,TollQRListActivity.this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(mAdapter);

       /* recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Toll toll = tollList.get(position);
                Toast.makeText(getApplicationContext(), toll.getName()+ " is selected!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));*/
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==android.R.id.home){
            finish();
        }

        return true;
    }
}
