package collection.toll.online.com.onlinetollcollection;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.zxing.WriterException;

import org.json.JSONException;
import org.json.JSONObject;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;
import collection.toll.online.com.onlinetollcollection.database.SQLiteAdapter;

import static android.R.attr.bitmap;

public class QRcodeActivity extends AppCompatActivity {
    String j;
    ImageView ivQRcode;
    Bitmap bitmap;
    Button delete;
    SQLiteAdapter dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcode);
        setTitle("QR Code");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        dbHelper=new SQLiteAdapter(getApplicationContext());
        ivQRcode= (ImageView) findViewById(R.id.ivQRcode);
delete= (Button) findViewById(R.id.delete);
        j=getIntent().getStringExtra("j");

        QRGEncoder qrgEncoder = new QRGEncoder(j, null, QRGContents.Type.TEXT, 300);
        try {
            // Getting QR-Code as Bitmap
            bitmap = qrgEncoder.encodeAsBitmap();
            // Setting Bitmap to ImageView
            ivQRcode.setImageBitmap(bitmap);
        } catch (WriterException e) {
            Log.v("#####", e.toString());
        }

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(QRcodeActivity.this)
                        .setTitle("Delete")
                        .setMessage("Are you sure you want to Delete?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // continue with delete
                                String tollId="0";
                                try {
                                    JSONObject json=new JSONObject(j);
                                    tollId=json.getString("tollId");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
if(tollId.equals("0")){
    Toast.makeText(QRcodeActivity.this,"Cant be delete",Toast.LENGTH_SHORT).show();
}else {
    dbHelper.openToWrite();

    dbHelper.deleteToll(tollId);
}
                                dialog.dismiss();
                                Intent i=new Intent(QRcodeActivity.this,MainActivity.class);
                                startActivity(i);                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                                dialog.dismiss();
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==android.R.id.home){
            finish();
        }

        return true;
    }
}
