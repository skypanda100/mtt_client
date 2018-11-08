package gzt.mtt.Animator;

import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.TranslateAnimation;

import java.util.ArrayList;
import java.util.List;

import gzt.mtt.Util.ScreenUtil;

public class FlyItemAnimator extends SimpleItemAnimator {
    List<RecyclerView.ViewHolder> mAddHolders = new ArrayList<>();
    List<RecyclerView.ViewHolder> mAddAnimators = new ArrayList<>();

    List<RecyclerView.ViewHolder> mChangeHolders = new ArrayList<>();
    List<RecyclerView.ViewHolder> mChangeAnimators = new ArrayList<>();

    List<RecyclerView.ViewHolder> mRemoveHolders = new ArrayList<>();
    List<RecyclerView.ViewHolder> mRemoveAnimators = new ArrayList<>();

    List<RecyclerView.ViewHolder> mMoveHolders = new ArrayList<>();
    List<RecyclerView.ViewHolder> mMoveAnimators = new ArrayList<>();

    @Override
    public boolean animateRemove(RecyclerView.ViewHolder viewHolder) {
        this.mRemoveHolders.add(viewHolder);
        return true;
    }

    @Override
    public boolean animateAdd(RecyclerView.ViewHolder viewHolder) {
        int height = ScreenUtil.getScreenHeight(viewHolder.itemView.getContext());
        viewHolder.itemView.setTranslationY(height + 500 * this.mAddHolders.size());
        this.mAddHolders.add(viewHolder);
        return true;
    }

    @Override
    public boolean animateMove(RecyclerView.ViewHolder viewHolder, int i, int i1, int i2, int i3) {
        this.mMoveHolders.add(viewHolder);
        return true;
    }

    @Override
    public boolean animateChange(RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder viewHolder1, int i, int i1, int i2, int i3) {
        this.mChangeHolders.add(viewHolder);
        return true;
    }

    @Override
    public void runPendingAnimations() {
        if(!this.mAddHolders.isEmpty()){
            for(RecyclerView.ViewHolder holder : this.mAddHolders) {
                this.add(holder);
            }
            this.mAddHolders.clear();
        }

        if(!this.mMoveHolders.isEmpty()){
            for(RecyclerView.ViewHolder holder : this.mMoveHolders) {
                this.move(holder);
            }
            this.mMoveHolders.clear();
        }

        if(!this.mRemoveHolders.isEmpty()){
            for(RecyclerView.ViewHolder holder : this.mRemoveHolders) {
                this.remove(holder);
            }
            this.mRemoveHolders.clear();
        }
    }

    @Override
    public void endAnimation(@NonNull RecyclerView.ViewHolder viewHolder) {

    }

    @Override
    public void endAnimations() {

    }

    @Override
    public boolean isRunning() {
        return !(this.mAddHolders.isEmpty() && this.mAddAnimators.isEmpty()
                && this.mChangeHolders.isEmpty() && this.mChangeAnimators.isEmpty()
                && this.mRemoveHolders.isEmpty() && this.mRemoveAnimators.isEmpty()
                && this.mMoveHolders.isEmpty() && this.mMoveAnimators.isEmpty());
    }

    private void add(final RecyclerView.ViewHolder holder) {
        this.mAddAnimators.add(holder);
        float height = holder.itemView.getTranslationY();

        ObjectAnimator animator = ObjectAnimator.ofFloat(holder.itemView,
                "translationY", height, 0);
        animator.setDuration(700);
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(android.animation.Animator animation) {
                dispatchAddStarting(holder);
            }

            @Override
            public void onAnimationEnd(android.animation.Animator animation) {
                dispatchAddFinished(holder);
                mAddAnimators.remove(holder);
                if(!isRunning()) dispatchAnimationsFinished();
            }
        });
        animator.setInterpolator(new DecelerateInterpolator(3.f));
        animator.start();
    }

    private void remove(final RecyclerView.ViewHolder holder){
        this.mRemoveAnimators.add(holder);
        TranslateAnimation animation = new TranslateAnimation(0, 1000, 0, 0);
        animation.setDuration(500);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                dispatchRemoveStarting(holder);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mRemoveAnimators.remove(holder);
                dispatchRemoveFinished(holder);
                if(!isRunning()){
                    dispatchAnimationsFinished();
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        holder.itemView.startAnimation(animation);
    }

    private void move(final RecyclerView.ViewHolder holder){
        this.mMoveAnimators.add(holder);
        ObjectAnimator animator = ObjectAnimator.ofFloat(holder.itemView,
                "translationY", holder.itemView.getTranslationY(), 0);
        animator.setDuration(500);
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(android.animation.Animator animation) {
                dispatchMoveStarting(holder);
            }

            @Override
            public void onAnimationEnd(android.animation.Animator animation) {
                dispatchMoveFinished(holder);
                mMoveAnimators.remove(holder);
                if(!isRunning()) dispatchAnimationsFinished();
            }
        });
        animator.start();
    }
}
