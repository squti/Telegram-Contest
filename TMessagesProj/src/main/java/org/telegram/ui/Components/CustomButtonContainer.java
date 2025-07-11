package org.telegram.ui.Components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.text.TextPaint;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;

import org.telegram.messenger.AndroidUtilities;

/**
 * Custom ViewGroup that holds 1-4 buttons with icons and text labels in a horizontal row.
 * Features:
 * - Auto-adjusting button widths (equal distribution)
 * - Transparent container background
 * - White semi-transparent button backgrounds (50% alpha)
 * - Rounded button corners (moderate radius)
 * - SVG/vector icon support with text labels
 * - Click callback support
 * - Adjustable container height in DP
 */
public class CustomButtonContainer extends LinearLayout {
    
    private int containerHeightDp = 60; // Increased height to accommodate text
    private final int buttonCornerRadiusDp = 10; // Moderate corner radius
    private final int buttonMarginDp = 8; // Margin between buttons
    private final int iconSizeDp = 20; // Slightly smaller icon size to make room for text
    private final int textSizeDp = 11; // Text size in DP
    private final int iconTextSpacingDp = 4; // Space between icon and text
    
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
     * Add a button with icon, text and click listener
     * @param iconResId Resource ID for the icon (supports SVG/vector drawables)
     * @param text Text label to display under the icon
     * @param clickListener Click listener for the button
     */
    public void addButton(int iconResId, String text, OnClickListener clickListener) {
        if (getChildCount() >= 4) {
            throw new IllegalStateException("CustomButtonContainer can hold maximum 4 buttons");
        }
        
        View button = createButton(iconResId, text, clickListener);
        addView(button, createButtonLayoutParams());
        redistributeButtonWidths();
    }
    
    /**
     * Add a button with drawable, text and click listener
     * @param drawable Drawable for the icon
     * @param text Text label to display under the icon
     * @param clickListener Click listener for the button
     */
    public void addButton(Drawable drawable, String text, OnClickListener clickListener) {
        if (getChildCount() >= 4) {
            throw new IllegalStateException("CustomButtonContainer can hold maximum 4 buttons");
        }
        
        View button = createButton(drawable, text, clickListener);
        addView(button, createButtonLayoutParams());
        redistributeButtonWidths();
    }
    
    /**
     * Add a button with icon and click listener (no text)
     * @param iconResId Resource ID for the icon (supports SVG/vector drawables)
     * @param clickListener Click listener for the button
     */
    public void addButton(int iconResId, OnClickListener clickListener) {
        addButton(iconResId, "", clickListener);
    }
    
    /**
     * Add a button with drawable and click listener (no text)
     * @param drawable Drawable for the icon
     * @param clickListener Click listener for the button
     */
    public void addButton(Drawable drawable, OnClickListener clickListener) {
        addButton(drawable, "", clickListener);
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
    
    /**
     * Set the background color for all buttons in the container
     * @param backgroundColor The new background color (including alpha)
     */
    public void setButtonBackgroundColor(int backgroundColor) {
        for (int i = 0; i < getChildCount(); i++) {
            View button = getChildAt(i);
            
            // Create new gradient drawable with updated color
            GradientDrawable background = new GradientDrawable();
            background.setColor(backgroundColor);
            background.setCornerRadius(AndroidUtilities.dp(buttonCornerRadiusDp));
            
            button.setBackground(background);
        }
    }
    
    private View createButton(int iconResId, String text, OnClickListener clickListener) {
        Drawable drawable;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            drawable = getContext().getResources().getDrawable(iconResId, getContext().getTheme());
        } else {
            drawable = getContext().getResources().getDrawable(iconResId);
        }
        return createButton(drawable, text, clickListener);
    }
    
    private View createButton(Drawable drawable, String text, OnClickListener clickListener) {
        View button = new View(getContext()) {
            private TextPaint textPaint;
            
            {
                // Initialize text paint
                textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
                textPaint.setColor(Color.WHITE);
                textPaint.setTextSize(AndroidUtilities.dp(textSizeDp));
                textPaint.setTypeface(Typeface.DEFAULT);
                textPaint.setTextAlign(Paint.Align.CENTER);
            }
            
            @Override
            protected void onDraw(Canvas canvas) {
                super.onDraw(canvas);
                
                // Calculate positions
                int iconSize = AndroidUtilities.dp(iconSizeDp);
                int iconTextSpacing = AndroidUtilities.dp(iconTextSpacingDp);
                
                // Draw icon centered horizontally and positioned higher up
                if (drawable != null) {
                    int iconLeft = (getWidth() - iconSize) / 2;
                    int iconTop = AndroidUtilities.dp(8); // Position icon higher up
                    int iconRight = iconLeft + iconSize;
                    int iconBottom = iconTop + iconSize;
                    
                    drawable.setBounds(iconLeft, iconTop, iconRight, iconBottom);
                    drawable.draw(canvas);
                }
                
                // Draw text below icon
                if (text != null && !text.isEmpty()) {
                    float textX = getWidth() / 2f;
                    float textY = AndroidUtilities.dp(8) + iconSize + iconTextSpacing + 
                                 Math.abs(textPaint.getFontMetrics().ascent);
                    
                    canvas.drawText(text, textX, textY, textPaint);
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
