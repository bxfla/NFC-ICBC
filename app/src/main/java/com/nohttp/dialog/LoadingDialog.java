package com.nohttp.dialog;





import com.alpha.live.R;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;

/**
 * Dialog
 * 
 * @author xm
 */
public class LoadingDialog extends AlertDialog {



    public LoadingDialog(Context context) {
        super(context);
    }

    public LoadingDialog(Context context, String message) {
        super(context);
        this.setCancelable(false);
    }

    public LoadingDialog(Context context, int theme, String message) {
        super(context, theme);
        this.setCancelable(false);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.progress_dialog);
    }

    public void setText(String message) {
    }

    public void setText(int resId) {
        setText(getContext().getResources().getString(resId));
    }

}
