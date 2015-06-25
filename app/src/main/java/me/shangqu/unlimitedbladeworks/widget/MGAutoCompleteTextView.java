package me.shangqu.unlimitedbladeworks.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ListPopupWindow;
import android.widget.ListView;
import android.widget.TextView;


import java.lang.reflect.Field;

import me.shangqu.unlimitedbladeworks.R;

/**
 * Created by blank_ken on 14-3-25.
 *
 * @modify dalin 2014-11-17从MGWidget迁入
 */
public class MGAutoCompleteTextView extends AutoCompleteTextView {

    private static final String TAG = "MGAutoCompleteTextView";
    private String[] emailSuffixs = new String[]{"@qq.com", "@163.com", "@sina.com", "@126.com", "@sohu.com", "@yeah.net", "@hotmail.com", "@yahoo.cn", "@gmail.com", "tom.com"};
    private boolean mIsInitPopWindow = false;
    private ListView mPopListView;
    private boolean mbNeedAutoComplete = true;

    public interface OnListItemClickExtraListener {
        public void onListItemClickExtra();
    }

    public void setOnListItemClickExtraListener(final OnListItemClickExtraListener onListItemClickExtraListener) {
        if (null != onListItemClickExtraListener) {
            final AdapterView.OnItemClickListener oldListener = getOnItemClickListener();
            this.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if (null != oldListener) {
                        oldListener.onItemClick(parent, view, position, id);
                    }
                    onListItemClickExtraListener.onListItemClickExtra();
                }
            });
        }
    }

    public MGAutoCompleteTextView(Context context) {
        super(context);
        init(context);
    }

    public MGAutoCompleteTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public MGAutoCompleteTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    public void setAdapterString(String[] strings) {
        if (null != strings && strings.length > 0) {
            this.emailSuffixs = strings;
        }
    }

    public void setNeedAutoComplete(boolean bAutoComplete) {
        mbNeedAutoComplete = bAutoComplete;
    }

    @Override
    public boolean enoughToFilter() {
        return (super.enoughToFilter() && mbNeedAutoComplete);
    }

    private void init(Context context) {
        // 可以使用默认数据初始化adapter，也可以通过set方法设置
        this.setAdapter(new MGAutoCompleteAdapter(context, R.layout.login_widget_auto_complete_list_item, emailSuffixs));
        this.setDropDownBackgroundResource(R.color.color_fff4f4f4);
        // 输入一个字符之后开始补全
        this.setThreshold(1);
        this.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    String text = MGAutoCompleteTextView.this.getText().toString();
                    // 当前区域重新获得焦点时，重新开启自动完成
                    if (!TextUtils.isEmpty(text)) {
                        performFiltering(text, 0);
                    }
                } else {
                    // 丢失焦点之后，可以进行正则检查
                }
            }
        });
        // 设置键盘回车键动作
        setImeOptions(EditorInfo.IME_ACTION_NEXT);
    }

    //    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    private void initDropDownWindow() {
        // level10以上（4.1开始）的AutoCompleteTextView有ListPopWindow这个类，封装了PopWindow和DropDownListView，但是10以下的是分开的。所以先反射去取成员变量
        // mDropDownList，如果没有，才去取mPopup并把它当做ListPopWindow
        if (Integer.parseInt(Build.VERSION.SDK) <= 10) {
            // 小于10，有mDropDownList
            Field dropListField = getField(AutoCompleteTextView.class.getName(), "mDropDownList");
            if (null != dropListField) {
                // 有这个成员，是10以下的，直接用即可
                dropListField.setAccessible(true);
                Object dropListObj = null;
                try {
                    dropListObj = dropListField.get(this);
                    if (null != dropListObj) {
                        mPopListView = (ListView) dropListObj;
                        initPopListView();
                        mIsInitPopWindow = true;
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        } else {
            // 是10以上的
            // 因为AutoCompleteTextView成员变量ListPopupWindow为private且没有提供设置方法，只能反射获得
            Field popField = getField(AutoCompleteTextView.class.getName(), "mPopup");
            if (null != popField) {
                popField.setAccessible(true);
                Object popupObj = null;
                try {
                    popupObj = popField.get(this);
                    if (null != popupObj) {
                        ListPopupWindow listPopupWindow = (ListPopupWindow) popupObj;
//                    listPopupWindow.setListSelector(new BitmapDrawable());
//                    ListView listView = listPopupWindow.getListView();
//                    if (null != listView){
//                        listView.setVerticalScrollBarEnabled(true);
//                    }
                        Field listField = getField(ListPopupWindow.class.getName(), "mDropDownList");
                        if (null != listField) {
                            listField.setAccessible(true);
                            Object listObj = null;
                            try {
                                listObj = listField.get(listPopupWindow);
                                if (null != listObj) {
                                    mPopListView = (ListView) listObj;
                                    initPopListView();
                                    mIsInitPopWindow = true;
                                }
                            } catch (IllegalAccessException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    private void initPopListView() {
        mPopListView.setVerticalScrollBarEnabled(false);
        mPopListView.setSelector(new BitmapDrawable());
        mPopListView.setDivider(new BitmapDrawable());
        mPopListView.setVerticalFadingEdgeEnabled(false);
        mPopListView.setHorizontalFadingEdgeEnabled(false);
        if (Integer.parseInt(Build.VERSION.SDK) >= 9) {
            mPopListView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        }
        this.setDropDownBackgroundResource(R.color.color_fff4f4f4);
    }

    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    @Override
    public void showDropDown() {
        super.showDropDown();
        if (!mIsInitPopWindow) {
            initDropDownWindow();
        } else {
            // 因为源码里变态的在showDropDown最后调了设置OverScrollMode为always，不得不这样做- -
            if (null != mPopListView) {
                initPopListView();
            }
        }
    }

    @Override
    protected void replaceText(CharSequence text) {
        // 重载该方法，使得点击下拉列表里的项的时候不是用该项替换输入框里的内容，而是输入框里的内容加上该项
        String string = this.getText().toString();
        int index = string.indexOf("@");
        if (-1 != index) {
            string = string.substring(0, index);
        }
        super.replaceText(string + text);
    }

    @Override
    protected void performFiltering(CharSequence text, int keyCode) {
        // 在输入框中输入后该方法被调用
        // 将已输入的数据和adapter中的模板比较，如果匹配前半部分，则在下拉框中出现
        initDropDownWindow();
        String string = text.toString();
        // 用户输入以字母数字下划线开始，而模板中都以@开始，所以调用父类时传入@开头的字符串
        int index = string.indexOf("@");
        if (-1 == index) {
            if (string.matches("^[a-zA-Z0-9_]+$")) {
                super.performFiltering("@", keyCode);
            } else {
                // 输入非法字符，关闭下拉框
                this.dismissDropDown();
            }
        } else {
            super.performFiltering(string.substring(index), keyCode);
        }
    }

    private class MGAutoCompleteAdapter extends ArrayAdapter<String> {

        public MGAutoCompleteAdapter(Context context, int textViewResourceId, String[] strings) {
            super(context, textViewResourceId, strings);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = convertView;
            if (null == view) {
                view = LayoutInflater.from(getContext()).inflate(R.layout.login_widget_auto_complete_list_item, null);
            }
            TextView tv = (TextView) view.findViewById(R.id.text_view);
            String text = MGAutoCompleteTextView.this.getText().toString();
            int index = text.indexOf("@");
            if (-1 != index) {
                text = text.substring(0, index);
            }
            // 将用户输入的文本域adapter中的后缀拼接，在下拉框中显示
            tv.setText(text + getItem(position));
            return view;
        }
    }

//    /**
//     * 获得类的成员变量值，包括私有成员
//     * @param instance 被调用的类
//     * @param variableName 成员变量名
//     * @return<span></span> */
//    public Object get(Object instance, String variableName)
//    {
//        String targetClass = instance.toString();
//        targetClass = targetClass.split(" ")[1];
//        Field field;
//        try {
//            field = getField(targetClass, variableName);
//            if (null != field){
//                field.setAccessible(true);//访问私有必须调用
////                return field.get(instance);
//                if (instance instanceof AutoCompleteTextView){
//                    return field.get(AutoCompleteTextView.class);
//                }else if(instance instanceof ListPopupWindow){
//                    return field.get(ListPopupWindow.class);
//                }else{
//                    return null;
//                }
//            }else{
//                return null;
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            return null;
//        }
//    }

    private Field getField(String targetClass, String variableName) {
        Class<?> obj = null;
        Field listPopWindowField = null;
        try {
            obj = Class.forName(targetClass);
            listPopWindowField = obj.getDeclaredField(variableName);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        return listPopWindowField;
    }
}
