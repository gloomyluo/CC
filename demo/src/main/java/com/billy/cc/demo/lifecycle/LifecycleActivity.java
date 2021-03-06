package com.billy.cc.demo.lifecycle;

import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.billy.cc.core.component.CC;
import com.billy.cc.core.component.CCResult;
import com.billy.cc.core.component.IComponentCallback;
import com.billy.cc.demo.JsonFormat;
import com.billy.cc.demo.R;

/**
 * 测试activity.onDestroy和fragment.onDestroy时，CC自动cancel
 * @author billy.qi
 * @since 17/12/9 11:06
 */
public class LifecycleActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView textView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lifecycle);
        textView = (TextView) findViewById(R.id.console);
        addOnClickListeners(R.id.start_cc
                            , R.id.finish_activity
                            , R.id.replace_fragment
                            );

    }
    private void showResult(CCResult result) {
        String text = JsonFormat.format(result.toString());
        textView.setText(text);
        Toast.makeText(CC.getApplication(), text, Toast.LENGTH_SHORT).show();
    }

    private void addOnClickListeners(@IdRes int... ids) {
        if (ids != null) {
            for (@IdRes int id : ids) {
                findViewById(id).setOnClickListener(this);
            }
        }
    }

    @Override
    public void onClick(View v) {

        textView.setText("");
        switch (v.getId()) {
            case R.id.start_cc:
                CC.obtainBuilder("ComponentB")
                        .setActionName("getNetworkData")
                        .cancelOnDestroyWith(this)
                        .build()
                        .callAsyncCallbackOnMainThread(printResultCallback);
                break;
            case R.id.finish_activity:
                finish();
                break;
            case R.id.replace_fragment:
                Fragment fragment = new LifecycleFragment();
                FragmentTransaction trans = getSupportFragmentManager().beginTransaction();
                trans.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left);
                trans.replace(R.id.fragment, fragment);
                trans.commit();
                break;
            default:
                break;
        }
    }

    IComponentCallback printResultCallback = new IComponentCallback() {
        @Override
        public void onResult(CC cc, CCResult result) {
            showResult(result);
        }
    };
}
