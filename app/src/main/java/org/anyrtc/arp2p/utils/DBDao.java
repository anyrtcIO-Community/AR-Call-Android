package org.anyrtc.arp2p.utils;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import org.anyrtc.arp2p.model.CallRecord;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liuxiaozhong on 2017-05-22.
 */

public class DBDao {
    private Context mContext;
    private DBHelper mDBHelper;
    private SQLiteDatabase mSQLiteDatabase;

    public DBDao(Context context) {
        this.mContext = context;
        mDBHelper = new DBHelper(mContext);
        mSQLiteDatabase = mDBHelper.getWritableDatabase();
    }

    public void Add(CallRecord bean) {
        String sql = "insert into call_records (userid,time,date,mode,state) values(?,?,?,?,?)";
        mSQLiteDatabase.execSQL(sql, new Object[]{bean.getUserid(),bean.getTime(), bean.getData(),
                bean.getMode(), bean.getState()});
    }


    public void Delete(String Id){
        String sql="delete from call_records where Id=?";
        mSQLiteDatabase.execSQL(sql,new Object[]{Id});
    }


    public List<CallRecord> GetCallRecordList(){
        List<CallRecord> list=new ArrayList<>();
        Cursor cursor=mSQLiteDatabase.rawQuery("select * from call_records order by Id desc",null);
            for (int i=0;i<cursor.getCount();i++){
                cursor.moveToNext();
                CallRecord bean=new CallRecord();
                bean.setId(cursor.getString(1));
                bean.setTime(cursor.getString(2));
                bean.setData(cursor.getString(3));
                bean.setMode(cursor.getInt(4));
                bean.setState(cursor.getInt(5));
                list.add(bean);
        }
        cursor.close();
        return list;
    }

    public void Destory(){
        if (mSQLiteDatabase!=null){
            mSQLiteDatabase.close();
            mSQLiteDatabase=null;
        }
    }
}
