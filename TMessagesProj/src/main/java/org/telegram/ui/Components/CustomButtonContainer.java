package org.telegram.ui.Components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;

import org.telegram.messenger.AndroidUtilities;

/**
 * Custom ViewGroup that holds 1-4 icon-only buttons in a horizontal row.
 * Features:
 * - Auto-adjusting button widths (equal distribution)
 * - Transparent container background
 * - White semi-transparent button backgrounds (50% alpha)
 * - Rounded button corners (moderate radius)
 * - SVG/vector icon support
 * - Click callback support
 * - Adjustable container height in DP
 */
public class CustomButtonContainer extends LinearLayout {
    
    private int containerHeightDp = 48; // Default height in DP
    private final int buttonCornerRadiusDp = 10; // Moderate corner radius
    private final int buttonMarginDp = 8; // Margin between buttons
    private final int iconSizeDp = 24; // Icon size in DP
    
    public CustomButtonContainer(Context context) {
        super(context);
        initializeContainer();
    }
    
    private void initializeContainer() {
        setOrientation(HORIZONTAL);
        setGravity(Gravity.CENTER_VERTICAL);
        
        // Set container layout parameters
        setLayoutParams(new LayoutParams(
            LayoutParams.MATCH_PARENT, 
            AndroidUtilities.dp(containerHeightDp)
        ));
        
        // Transparent background for container
        setBackgroundColor(Color.TRANSPARENT);
    }
    
    /**
     * Add a button with icon and click listener
     * @param iconResId Resource ID for the icon (supports SVG/vector drawables)
     * @param clickListener Click listener for the button
     */
    public void addButton(int iconResId, OnClickListener clickListener) {
        if (getChildCount() >= 4) {
            throw new IllegalStateException("CustomButtonContainer can hold maximum 4 buttons");
        }
        
        View button = createButton(iconResId, clickListener);
        addView(button, createButtonLayoutParams());
        redistributeButtonWidths();
    }
    
    /**
     * Add a button with drawable and click listener
     * @param drawable Drawable for the icon
     * @param clickListener Click listener for the button
     */
    public void addButton(Drawable drawable, OnClickListener clickListener) {
        if (getChildCount() >= 4) {
            throw new IllegalStateException("CustomButtonContainer can hold maximum 4 buttons");
        }
        
        View button = createButton(drawable, clickListener);
        addView(button, createButtonLayoutParams());
        redistributeButtonWidths();
    }
    
    /**
     * Remove all buttons from the container
     */
    public void removeAllButtons() {
        removeAllViews();
    }
    
    /**
     * Set the container height in DP
     * @param heightDp Height in DP
     */
    public void setContainerHeight(int heightDp) {
        this.containerHeightDp = heightDp;
        
        LayoutParams layoutParams = (LayoutParams) getLayoutParams();
        if (layoutParams != null) {
            layoutParams.height = AndroidUtilities.dp(heightDp);
            setLayoutParams(layoutParams);
        }
        
        // Update all button heights
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            LayoutParams childParams = (LayoutParams) child.getLayoutParams();
            childParams.height = LayoutParams.MATCH_PARENT;
            child.setLayoutParams(childParams);
        }
    }

    public void setTopMargin(int heightDp) {
        this.containerHeightDp = heightDp;

        LayoutParams layoutParams = (LayoutParams) getLayoutParams();
        if (layoutParams != null) {
            layoutParams.height = AndroidUtilities.dp(heightDp);
            setLayoutParams(layoutParams);
        }
    }
    
    /**
     * Get current container height in DP
     * @return Height in DP
     */
    public int getContainerHeightDp() {
        return containerHeightDp;
    }
    
    /**
     * Get number of buttons currently in container
     * @return Number of buttons
     */
    public int getButtonCount() {
        return getChildCount();
    }
    
    private View createButton(int iconResId, OnClickListener clickListener) {
        Drawable drawable;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            drawable = getContext().getResources().getDrawable(iconResId, getContext().getTheme());
        } else {
            drawable = getContext().getResources().getDrawable(iconResId);
        }
        return createButton(drawable, clickListener);
    }
    
    private View createButton(Drawable drawable, OnClickListener clickListener) {
        View button = new View(getContext()) {
            @Override
            protected void onDraw(Canvas canvas) {
                super.onDraw(canvas);
                
                // Draw icon centered in the button
                if (drawable != null) {
                    int iconSize = AndroidUtilities.dp(iconSizeDp);
                    int left = (getWidth() - iconSize) / 2;
                    int top = (getHeight() - iconSize) / 2;
                    int right = left + iconSize;
                    int bottom = top + iconSize;
                    
                    drawable.setBounds(left, top, right, bottom);
                    drawable.draw(canvas);
                }
            }
        };
        
        // Set white background with 50% alpha and rounded corners
        GradientDrawable background = new GradientDrawable();
        background.setColor(Color.argb(25, 0, 0, 0)); // 50% alpha white
        background.setCornerRadius(AndroidUtilities.dp(buttonCornerRadiusDp));
        button.setBackground(background);
        
        // Set click listener
        button.setOnClickListener(clickListener);
        
        // Add touch feedback
        button.setClickable(true);
        button.setFocusable(true);
        
        return button;
    }
    
    private LayoutParams createButtonLayoutParams() {
        LayoutParams params = new LayoutParams(
            0, // Width will be set by weight
            LayoutParams.MATCH_PARENT,
            1.0f // Equal weight distribution
        );
        
        // Add margin between buttons (except for first button)
        if (getChildCount() > 0) {
            params.leftMargin = AndroidUtilities.dp(buttonMarginDp);
        }
        
        return params;
    }
    
    private void redistributeButtonWidths() {
        int buttonCount = getChildCount();
        if (buttonCount == 0) return;
        
        // Update layout weights and margins for all buttons
        for (int i = 0; i < buttonCount; i++) {
            View child = getChildAt(i);
            LayoutParams params = (LayoutParams) child.getLayoutParams();
            
            // Set equal weight for all buttons
            params.weight = 1.0f;
            params.width = 0;
            
            // Set margins (first button has no left margin)
            params.leftMargin = i > 0 ? AndroidUtilities.dp(buttonMarginDp) : 0;
            
            child.setLayoutParams(params);
        }
    }
}
