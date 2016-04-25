package com.materialnotes.view;

import android.support.v7.app.ActionBar;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.materialnotes.R;

import com.shamanland.fab.FloatingActionButton;
import com.shamanland.fab.ScrollDetector;

/**
 * Hides and shows a FloatingActionButton and ActionBar when you scroll up or down
 **
 */
public class ShowHideOnScroll extends ScrollDetector implements Animation.AnimationListener  {

    private final FloatingActionButton fab;
    private final ActionBar actionBar;

    /**
     * Constructor.
     *
     * @param fab  FloatingActionButton
     * @param actionBar  ActionBar
     */
    public ShowHideOnScroll(FloatingActionButton fab, ActionBar actionBar) {
        super(fab.getContext());
        this.fab = fab;
        this.actionBar = actionBar;
    }

    /** {@inheritDoc} */
    @Override
    public void onScrollDown() {
        if (!areViewsVisible()) {
            fab.setVisibility(View.VISIBLE);
            actionBar.show();
            animateFAB(R.anim.floating_action_button_show);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void onScrollUp() {
        if (areViewsVisible()) {
            fab.setVisibility(View.GONE);
            actionBar.hide();
            animateFAB(R.anim.floating_action_button_hide);
        }
    }

    /** @return {@code true} if the FAB and the ActionBar are visible; {@code false} if not. */
    private boolean areViewsVisible() {
        return fab.getVisibility() == View.VISIBLE && actionBar.isShowing();
    }

    /**
     * Animated FAB
     *
     * @param anim the animation.
     */
    private void animateFAB(int anim) {
        Animation a = AnimationUtils.loadAnimation(fab.getContext(), anim);
        a.setAnimationListener(this);
        fab.startAnimation(a);
        setIgnore(true);
    }

    /** {@inheritDoc} */
    @Override
    public void onAnimationStart(Animation animation) {
        // Nada
    }

    /** {@inheritDoc} */
    @Override
    public void onAnimationEnd(Animation animation) {
        setIgnore(false);
    }

    /** {@inheritDoc} */
    @Override
    public void onAnimationRepeat(Animation animation) {
        // Nada
    }
}