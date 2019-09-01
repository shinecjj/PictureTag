package com.example.picturetag;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.picturetag.bean.TagBean;

import static com.example.picturetag.MainActivity.BUNDLE_TAG_BEAN;
import static com.example.picturetag.MainActivity.RESULT_CODE_GET_TAG;

public class TagsActivity extends AppCompatActivity {

    private ListView mListView;
    private String data[] = {"围脖","口红","书籍","毛笔","发髻"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tags);
        initView();
    }

    private void initView(){
        mListView = findViewById(R.id.recycleview);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,android.R.layout.simple_list_item_1, data);
        mListView.setAdapter(adapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                TagBean tagBean = new TagBean(1, data[position], "", -1, -1, -1);
                Intent intent = new Intent();
                intent.putExtra(BUNDLE_TAG_BEAN, tagBean);
                setResult(RESULT_CODE_GET_TAG, intent);
                finish();
            }
        });
    }

}
