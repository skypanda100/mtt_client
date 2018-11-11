package gzt.mtt.Component.WatingDialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.TextView;

import gzt.mtt.R;

public class WaitingDialog extends Dialog {
    private TextView mContentTextView;

    public WaitingDialog(Context context) {
        super(context);
        this.initData();
        this.initView();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private void initData() {

    }

    private void initView() {
        this.setContentView(R.layout.component_waiting_dialog);
        this.mContentTextView = this.findViewById(R.id.content);
    }

    public void setContentText(String text) {
        this.mContentTextView.setText(text);
    }
}
