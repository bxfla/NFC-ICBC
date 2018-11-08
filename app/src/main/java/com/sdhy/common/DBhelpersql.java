package com.sdhy.common;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class DBhelpersql extends SQLiteOpenHelper {

	private static final String DB_NAME = "cpucard"; //数据库名称  
    private static final int version = 1; //数据库版本  
       
    public DBhelpersql(Context context) {  
        super(context, DB_NAME, null, version);  
        // TODO Auto-generated constructor stub  
    }    
    public DBhelpersql(Context context, String name, CursorFactory factory,int version) {
		super(context, name, factory, version);
		// TODO Auto-generated constructor stub
	}
	public DBhelpersql(Context context, String name) {
		this(context, name, version);
		// TODO Auto-generated constructor stub
	}
	
	public DBhelpersql(Context context, String name, int version) {
		this(context, name, null, version);
		// TODO Auto-generated constructor stub
	}
    /**
     * 老版本覆盖 安装不执行这里
     * */
	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		//创建交接数据表
		String sql1="CREATE TABLE Recharge  (id Integer NOT NULL,cardNo varchar(50),date varchar(50),Cmoney varchar(50),orderid varchar(50),status varchar(2) ,YEcz varchar(50),CONSTRAINT [PK_jjbus] PRIMARY KEY ([id]))";
		String sql2="CREATE TABLE Board (id Integer NOT NULL,cardNo varchar(50),BDdate varchar(50),BDmoney varchar(50),statusBD varchar(50),YEBD varchar(50),CONSTRAINT [PK_xgdata] PRIMARY KEY ([id]))";
		db.execSQL(sql1);
		db.execSQL(sql2);
		
	}
//数据库表有改动时，改变顶上数据库版本号
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		String alterSql="alter table xgjh add column planStatus varchar(2) default '0'";
		db.execSQL(alterSql);
		db.endTransaction();
	}
	
	/**
	 * 清空数据
	 * @param db
	 */
	public void deleteAll(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		String sql1="delete from Recharge";
		String sql2="delete from Board";
		db.execSQL(sql1);
		db.execSQL(sql2);
		db.endTransaction();
	//	db.execSQL(sql6);
	}
	
}