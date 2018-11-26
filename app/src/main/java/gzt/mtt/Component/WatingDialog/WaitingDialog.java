package gzt.mtt.Component.WatingDialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;

import fr.tvbarthel.lib.blurdialogfragment.BlurDialogFragment;
import gzt.mtt.R;

public class WaitingDialog extends BlurDialogFragment {
    private Dialog mDialog;
    private GradientDrawable mGradientDrawable;
    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        this.mDialog = null;
        this.mGradientDrawable = null;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        if (this.mDialog == null) {
            this.mGradientDrawable = new GradientDrawable();
            this.mGradientDrawable.setCornerRadius(getResources().getDimensionPixelSize(R.dimen.corner_radius));
            this.mGradientDrawable.setColor(getResources().getColor(R.color.colorPrimary));

            this.mDialog = new Dialog(getActivity());
            this.mDialog.setContentView(R.layout.component_waiting_dialog);
            this.mDialog.findViewById(R.id.waitingBackGround).setBackground(this.mGradientDrawable);
        }
        return this.mDialog;
    }

    @Override
    protected float getDownScaleFactor() {
        // Allow to customize the down scale factor.
        return 5.0f;
    }

    @Override
    protected int getBlurRadius() {
        // Allow to customize the blur radius factor.
        return 10;
    }

    @Override
    protected boolean isActionBarBlurred() {
        // Enable or disable the blur effect on the action bar.
        // Disabled by default.
        return true;
    }

    @Override
    protected boolean isDimmingEnable() {
        // Enable or disable the dimming effect.
        // Disabled by default.
        return true;
    }

    @Override
    protected boolean isRenderScriptEnable() {
        // Enable or disable the use of RenderScript for blurring effect
        // Disabled by default.
        return true;
    }

    @Override
    protected boolean isDebugEnable() {
        // Enable or disable debug mode.
        // False by default.
        return false;
    }
}
