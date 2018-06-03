package collection.toll.online.com.onlinetollcollection.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class SQLiteAdapter {


	//Database name
	public static final String MYDATABASE_NAME = "DB";

	//database version
	public static final int MYDATABASE_VERSION = 1;


	private SQLiteHelper sqLiteHelper;
	private SQLiteDatabase sqLiteDatabase;

	private Context context;
  

    private final ArrayList<Toll> TollList = new ArrayList<Toll>();

	public SQLiteAdapter(Context c) {
		context = c;
	}

	public SQLiteAdapter openToRead() throws android.database.SQLException {
		sqLiteHelper = new SQLiteHelper(context, MYDATABASE_NAME, null,
				MYDATABASE_VERSION);
		sqLiteDatabase = sqLiteHelper.getReadableDatabase();
		return this;
	}

	public SQLiteAdapter openToWrite() throws android.database.SQLException {
		sqLiteHelper = new SQLiteHelper(context, MYDATABASE_NAME, null,
				MYDATABASE_VERSION);
		sqLiteDatabase = sqLiteHelper.getWritableDatabase();
		return this;
	}

	public void close() {
		sqLiteHelper.close();
	}


//insert Toll details in databse
	public long insertToll(String name, String tollId, String status, String amount, String userId) {

		Calendar c = Calendar.getInstance();
		System.out.println("Current time => " + c.getTime());

		SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
		String formattedDate = df.format(c.getTime());

		ContentValues contentValues = new ContentValues();

		contentValues.put("name", name);
		contentValues.put("tollId", tollId);
		contentValues.put("status", status);

		contentValues.put("amount", amount);
		contentValues.put("userId", userId);
		contentValues.put("date", formattedDate);




		return sqLiteDatabase.insert("Toll", null, contentValues);
	}



	//get user data from database

	public ArrayList<Toll> retrieveAllToll() {
		
		
	TollList.clear();
	Cursor cursor = sqLiteDatabase.rawQuery(
			"select * from Toll;", null);
	try {
	  if (cursor.moveToFirst()) {
 		do {
 			Toll contact = new Toll();


			contact.setId(cursor.getString(5));
 		    contact.setName(cursor.getString(2));
 		   contact.setTollId(cursor.getString(1));
 		   contact.setAmount(cursor.getString(4));
			contact.setStatus(cursor.getString(3));
			contact.setDate(cursor.getString(6));


 		   
 		   TollList.add(contact);
 		  //  consumer_metadata_details.add(field_type1);
 		} while (cursor.moveToNext());
 	    }

 	    // return contact list
 	    cursor.close();
 	  //  db.close();
 	    return     TollList;
 	} catch (Exception e) {
 	    // TODO: handle exception
 	    Log.e("all_contact", "" + e);
 	}finally {
 	    cursor.close();
 	}

 	return     TollList;
}



//get toll id
	public String getTollID() {
	
	String TollId = "-1";
	try {
		Cursor cursor = sqLiteDatabase.rawQuery(
				"select tollId from Toll;", null);
		if (cursor != null && cursor.moveToFirst()) {
			TollId =cursor.getString(0);
		}
		
		return TollId;
	} catch (SQLiteException e) {
		//Log.e(TAG, "Get Last" + e);
		return "0";
	} finally {
		//Log.i(TAG, "Last Toll Id=" + USerID);
	
	}
}

	//delete Toll data
	public int deleteToll(String tollId) {

		int k = sqLiteDatabase.delete("Toll", "tollId=?", new String[] {tollId});
		return k;
	}



	//get total Toll
	 public int Get_Total_Toll() {
			
			
			Cursor cursor = sqLiteDatabase.rawQuery(
					"select * from Toll;", null);
			int count = cursor.getCount();
		    cursor.close();
		  
		    return count;
		    }
	
	 




         //update status
	public void update_TollStatus(String id,String status) {
		// TODO Auto-generated method stub

		sqLiteDatabase.execSQL("update Toll set status='" + status + "'  where tollId="+id);
		//

	}


	
	public class SQLiteHelper extends SQLiteOpenHelper {
		/*
		 * Constructor called its super class
		 */
		public SQLiteHelper(Context context, String name,
							CursorFactory factory, int version) {
			super(context, name, factory, version);
		}


                //create tables
		@Override
		public void onCreate(SQLiteDatabase db) {

			
			db.execSQL("create table IF NOT EXISTS Toll(id INTEGER PRIMARY KEY,tollId text,name text,status text,amount text,userId text,date text);");

			Log.d("Log", "Database Created");
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// TODO Auto-generated method stub
		}
		@Override
		public synchronized void close() {
		    if(sqLiteDatabase != null){
		    	sqLiteDatabase.close();
		    super.close();
		    }   
		}
	}

}