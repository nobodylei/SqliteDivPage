package com.lei.sqlitedivpage;

import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.AbsListView;
import android.widget.ListView;

import java.util.List;

/**
 * 演示数据分页
 * <p>
 * select * from person limit 0,15;当前页第一条数据的下标，每页显示的数据条目
 * <p>
 * 总条目 201
 * 每页条数 20
 * 总页数
 * 页码
 * <p>
 * 1，页码为一时在listView中展示对应的数据
 * 2，listView加载完本页数据分页加载下一页数据
 */

public class MainActivity extends AppCompatActivity {
    private ListView lv;
    private SQLiteDatabase db;
    private MySqliteHelper helper;
    private int totalNum;//表示当前控件加载数据的总条目
    private int pageSize = 20;//表示每页展示的数据条目
    private int pageNum;//表示总的页码
    private int currentPage = 1;//表示当前页码
    private List<Person> totalList;//表示数据源
    private MyBaseAdapter adapter;//适配器
    private boolean isDivPage;//是否分页

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        lv = findViewById(R.id.lv);
        helper = DbManager.getIntance(this);
        db = helper.getWritableDatabase();

        //获取数据的总条目
        totalNum = DbManager.getDataCount(db,Constant.TABLE_NAME);
        //根据总条目与每页展示数据条目 获得总页数
        pageNum = (int)Math.ceil(totalNum / (double)pageSize);
        if(currentPage == 1) {
            totalList = DbManager.getListByCurrentPage(db,Constant.TABLE_NAME,currentPage,pageSize);
        }
        adapter = new MyBaseAdapter(this,totalList);
        lv.setAdapter(adapter);

        lv.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if(isDivPage && AbsListView.OnScrollListener.SCROLL_STATE_IDLE == scrollState) {//停止滚动了
                    if(currentPage < pageNum) {
                        currentPage ++;
                        //根据最新的页码加载获取集合储存到数据源中
                        totalList.addAll(DbManager.getListByCurrentPage(db,
                                Constant.TABLE_NAME,currentPage,pageSize));
                        adapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                isDivPage = (firstVisibleItem + visibleItemCount) == totalItemCount;
            }
        });

//        for (int i = 1; i <= 100; i++) {
//            String sql = "insert into " + Constant.TABLE_NAME + " values(" +
//                    i + ",'zhangsan" + i + "',12 + "+ i +")";
//            db.execSQL(sql);
//        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        db.close();
    }
}
