package android.job.blescan;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.TimeUnit;

public class SQLiteDataBaseGoSafe extends SQLiteOpenHelper {
    DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReferenceFromUrl("https://gosafe-2ed5c-default-rtdb.firebaseio.com/");

    SQLiteDatabase db;
    //Database name in String variable
    private static final String DATABASE_NAME="gosafe";

    //Database version
    private static final int DATABASE_VERSION=1;

    //Table names
    private static final String TABLE_PERSONINCONTACT="contact_list";

    //Column names
    public static final String KEY_USERID="userid";
    public static final String KEY_CONTID="contactedid";
    public static final String KEY_DATE="date";
    public static final String KEY_TIME="time";


    public SQLiteDataBaseGoSafe(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //creating table
        String CREATE_CONTACT_LIST_TABLE= "CREATE TABLE " + TABLE_PERSONINCONTACT + "( " + KEY_USERID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                                    + KEY_CONTID + " TEXT, " + KEY_DATE + " TEXT,"+KEY_TIME+" TEXT);";
        //execute query
        db.execSQL(CREATE_CONTACT_LIST_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_PERSONINCONTACT);
        onCreate(db);

    }

    public long insertcontactlist(String contactedid,String date,String time )
    {
        //get permission to write into table/database
        db=this.getWritableDatabase();
        //class used to insert values into database
        ContentValues cv=new ContentValues();

        cv.put(KEY_CONTID,contactedid);
        cv.put(KEY_DATE,date);
        cv.put(KEY_TIME,time);
        return db.insert(TABLE_PERSONINCONTACT,null,cv);

    }

    public String getData(String uni_val) throws InterruptedException {
        db=this.getWritableDatabase();
        Cursor c=db.rawQuery("Select * from "+TABLE_PERSONINCONTACT,null);

        String result="";
        int id_user=c.getColumnIndex(KEY_USERID);
        int id_contacted=c.getColumnIndex(KEY_CONTID);
        int date=c.getColumnIndex(KEY_DATE);
        int time=c.getColumnIndex(KEY_TIME);


        for(c.moveToFirst();!c.isAfterLast();c.moveToNext())
        {
            String contaced_id=""+c.getString(id_contacted);
            String user=""+c.getString(id_user);
            String d=""+c.getString(date);
            String t=""+c.getString(time);
            result=result+" "+c.getString(id_user)+ " "+c.getString(id_contacted)+ " "+ c.getString(date)+" "+ c.getString(time)+ "\n";
            DatabaseReference uniqueKeyRef = databaseReference.child("contacted_users").child(uni_val).child(contaced_id).child(user);
            uniqueKeyRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    uniqueKeyRef.child("Date").setValue(d);
                    uniqueKeyRef.child("Time").setValue(t);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }

            });


        }
        db.execSQL("delete from "+ TABLE_PERSONINCONTACT);
        db.close();
        return result;

    }

}
