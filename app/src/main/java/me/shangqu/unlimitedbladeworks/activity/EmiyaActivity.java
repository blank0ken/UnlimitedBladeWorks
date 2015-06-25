package me.shangqu.unlimitedbladeworks.activity;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import me.shangqu.unlimitedbladeworks.R;
import me.shangqu.unlimitedbladeworks.widget.MGAutoCompleteTextView;


public class EmiyaActivity extends ActionBarActivity {


    private MGAutoCompleteTextView mUname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emiya);
        setupViews();
    }

    private void setupViews() {
        mUname = (MGAutoCompleteTextView) findViewById(R.id.login_input_autocomplete);
        mUname.setNeedAutoComplete(true);
        mUname.setHint(getString(R.string.account_hint));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_emiya, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
