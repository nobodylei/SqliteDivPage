package com.lei.sqlitedivpage;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yanle on 2018/2/8.
 */

public class DbManager {
    private static MySqliteHelper helper;

    public static MySqliteHelper getIntance(Context context) {
        if(helper == null) {
            helper = new MySqliteHelper(context);
        }
        return helper;
    }

    /**
     * 根据sql语句查询cursor对象
     * @param db 数据库对象
     * @param sql 查询的sql语句
     * @param selectionArgs 查询条件的占位符
     * @return 查询结果
     */
    public static Cursor selectDataBySql(SQLiteDatabase db,String sql,String[] selectionArgs) {
        Cursor cursor = null;
        if(db != null) {
            cursor = db.rawQuery(sql,selectionArgs);
        }
        return cursor;
    }

    /**
     * 将查询的Cursor对象转换为List对集合
     * @param cursor 游标对象
     * @return 集合对象
     */
    public static List<Person> cursorToList(Cursor cursor) {
        List<Person> list = new ArrayList<Person>();
        //moveToNext() 如果返回true表示下一条记录存在，否则表示游标中的数据读取完毕
        while(cursor.moveToNext()) {
            //getColumnIndex(String columnName)根据参数中指定的字段名称，获取字段下标
            int columnIndex = cursor.getColumnIndex(Constant._ID);
            //getInt(int columnIndex) 根据参数中指定的字段下标，
            //获取对应int类型的value
            int _id = cursor.getInt(columnIndex);

            String name = cursor.getString(cursor.getColumnIndex(Constant.NAME));
            int age = cursor.getInt(cursor.getColumnIndex(Constant.AGE));

            Person person = new Person(_id,name,age);
            list.add(person);
        }
        cursor.close();
        return list;
    }

    /**
     * 根据数据库以及数据表名称获取表中数据总条目
     * @param db  数据库对象
     * @param tableName  数据表名称
     * @return  数据总条目
     */
    public static int getDataCount(SQLiteDatabase db,String tableName) {
        int count = 0;
        if(db != null) {
            Cursor cursor = db.rawQuery("select * from " + tableName, null);
            count = cursor.getCount();//获取cursor中的数据总数
        }
        return count;
    }


    /**
     * 根据当前页码查询获取该页码对应的集合数据
     * @param db     数据库对象
     * @param tableName 数据表名称
     * @param currentPage 当前页码
     * @param pageSize 每页条数
     * @return  当前页对应的集合
     *
     * select * from person ?,?
     * 如何根据当前页码获取该页数据
     *
     */
    public static List<Person> getListByCurrentPage(SQLiteDatabase db,String tableName,
                                                    int currentPage,int pageSize) {
        //当前页码减1乘以每页条数
        int index = (currentPage - 1) * pageSize;//获取当前页码第一条数据下标
        Cursor cursor = null;
        if(db != null) {
            String sql = "select * from " + Constant.TABLE_NAME + " limit ?,?";
            cursor = db.rawQuery(sql,new String[]{index + "",pageSize + ""});
        }
        return cursorToList(cursor);
    }
}
