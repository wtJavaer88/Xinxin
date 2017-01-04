package com.wnc.xinxin;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;

/**
 * An indicator of progress, similar to Android's ProgressBar. Can be used in
 * 'spin mode' or 'increment mode'
 * 
 * @author Todd Davies
 *         <p/>
 *         Licensed under the Creative Commons Attribution 3.0 license see:
 *         http://creativecommons.org/licenses/by/3.0/
 */
public class ProgressWheel extends View
{

    // Sizes (with defaults)
    private int layout_height = 0;
    private int layout_width = 0;
    private int fullRadius = 100;
    private int circleRadius = 80;
    private int barLength = 60;
    private int barWidth = 20;
    private int rimWidth = 20;
    private int textSize = 20;
    private float contourSize = 0;

    // Padding (with defaults)
    private int paddingTop = 5;
    private int paddingBottom = 5;
    private int paddingLeft = 5;
    private int paddingRight = 5;

    // Colors (with defaults)
    private int barColor = 0xAA000000;
    private int contourColor = 0xAA000000;
    private int circleColor = 0x00000000;
    private int rimColor = 0xAADDDDDD;
    private int textColor = 0xFF000000;

    // Paints
    private Paint barPaint = new Paint();
    private Paint circlePaint = new Paint();
    private Paint rimPaint = new Paint();
    private Paint textPaint = new Paint();
    private Paint contourPaint = new Paint();

    // Rectangles
    @SuppressWarnings("unused")
    private RectF rectBounds = new RectF();
    private RectF circleBounds = new RectF();
    private RectF circleOuterContour = new RectF();
    private RectF circleInnerContour = new RectF();

    // Animation
    // The amount of pixels to move the bar by on each draw
    private int spinSpeed = 2;
    // The number of milliseconds to wait inbetween each draw
    private int delayMillis = 0;
    int progress = 0;
    boolean isSpinning = false;

    // Other
    private String text = "";
    private String[] splitText =
    {};

    /**
     * The constructor for the ProgressWheel
     * 
     * @param context
     * @param attrs
     */
    public ProgressWheel(Context context, AttributeSet attrs)
    {
        super(context, attrs);

        parseAttributes(context.obtainStyledAttributes(attrs,
                R.styleable.ProgressWheel));
    }

    // ----------------------------------
    // Setting up stuff
    // ----------------------------------

    /*
     * When this is called, make the view square. From:
     * http://www.jayway.com/2012
     * /12/12/creating-custom-android-views-part-4-measuring
     * -and-how-to-force-a-view-to-be-square/
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        // The first thing that happen is that we call the superclass
        // implementation of onMeasure. The reason for that is that measuring
        // can be quite a complex process and calling the super method is a
        // convenient way to get most of this complexity handled.
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        // We can’t use getWidth() or getHight() here. During the measuring
        // pass the view has not gotten its final size yet (this happens first
        // at the start of the layout pass) so we have to use getMeasuredWidth()
        // and getMeasuredHeight().
        int size = 0;
        int width = getMeasuredWidth();
        int height = getMeasuredHeight();
        int widthWithoutPadding = width - getPaddingLeft() - getPaddingRight();
        int heigthWithoutPadding = height - getPaddingTop()
                - getPaddingBottom();

        // Finally we have some simple logic that calculates the size of the
        // view
        // and calls setMeasuredDimension() to set that size.
        // Before we compare the width and height of the view, we remove the
        // padding,
        // and when we set the dimension we add it back again. Now the actual
        // content
        // of the view will be square, but, depending on the padding, the total
        // dimensions
        // of the view might not be.
        if (widthWithoutPadding > heigthWithoutPadding)
        {
            size = heigthWithoutPadding;
        }
        else
        {
            size = widthWithoutPadding;
        }

        // If you override onMeasure() you have to call setMeasuredDimension().
        // This is how you report back the measured size. If you don’t call
        // setMeasuredDimension() the parent will throw an exception and your
        // application will crash.
        // We are calling the onMeasure() method of the superclass so we don’t
        // actually need to call setMeasuredDimension() since that takes care
        // of that. However, the purpose with overriding onMeasure() was to
        // change the default behaviour and to do that we need to call
        // setMeasuredDimension() with our own values.
        setMeasuredDimension(size + getPaddingLeft() + getPaddingRight(), size
                + getPaddingTop() + getPaddingBottom());
    }

    /**
     * Use onSizeChanged instead of onAttachedToWindow to get the dimensions of
     * the view, because this method is called after measuring the dimensions of
     * MATCH_PARENT & WRAP_CONTENT. Use this dimensions to setup the bounds and
     * paints.
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh)
    {
        super.onSizeChanged(w, h, oldw, oldh);

        // Share the dimensions
        this.layout_width = w;
        this.layout_height = h;

        setupBounds();
        setupPaints();
        invalidate();
    }

    /**
     * Set the properties of the paints we're using to draw the progress wheel
     */
    private void setupPaints()
    {
        this.barPaint.setColor(this.barColor);
        this.barPaint.setAntiAlias(true);
        this.barPaint.setStyle(Style.STROKE);
        this.barPaint.setStrokeWidth(this.barWidth);

        this.rimPaint.setColor(this.rimColor);
        this.rimPaint.setAntiAlias(true);
        this.rimPaint.setStyle(Style.STROKE);
        this.rimPaint.setStrokeWidth(this.rimWidth);

        this.circlePaint.setColor(this.circleColor);
        this.circlePaint.setAntiAlias(true);
        this.circlePaint.setStyle(Style.FILL);

        this.textPaint.setColor(this.textColor);
        this.textPaint.setStyle(Style.FILL);
        this.textPaint.setAntiAlias(true);
        this.textPaint.setTextSize(this.textSize);

        this.contourPaint.setColor(this.contourColor);
        this.contourPaint.setAntiAlias(true);
        this.contourPaint.setStyle(Style.STROKE);
        this.contourPaint.setStrokeWidth(this.contourSize);
    }

    /**
     * Set the bounds of the component
     */
    private void setupBounds()
    {
        // Width should equal to Height, find the min value to steup the circle
        int minValue = Math.min(this.layout_width, this.layout_height);

        // Calc the Offset if needed
        int xOffset = this.layout_width - minValue;
        int yOffset = this.layout_height - minValue;

        // Add the offset
        this.paddingTop = this.getPaddingTop() + (yOffset / 2);
        this.paddingBottom = this.getPaddingBottom() + (yOffset / 2);
        this.paddingLeft = this.getPaddingLeft() + (xOffset / 2);
        this.paddingRight = this.getPaddingRight() + (xOffset / 2);

        int width = getWidth(); // this.getLayoutParams().width;
        int height = getHeight(); // this.getLayoutParams().height;

        this.rectBounds = new RectF(this.paddingLeft, this.paddingTop, width
                - this.paddingRight, height - this.paddingBottom);

        this.circleBounds = new RectF(this.paddingLeft + this.barWidth,
                this.paddingTop + this.barWidth, width - this.paddingRight
                        - this.barWidth, height - this.paddingBottom
                        - this.barWidth);
        this.circleInnerContour = new RectF(this.circleBounds.left
                + (this.rimWidth / 2.0f) + (this.contourSize / 2.0f),
                this.circleBounds.top + (this.rimWidth / 2.0f)
                        + (this.contourSize / 2.0f), this.circleBounds.right
                        - (this.rimWidth / 2.0f) - (this.contourSize / 2.0f),
                this.circleBounds.bottom - (this.rimWidth / 2.0f)
                        - (this.contourSize / 2.0f));
        this.circleOuterContour = new RectF(this.circleBounds.left
                - (this.rimWidth / 2.0f) - (this.contourSize / 2.0f),
                this.circleBounds.top - (this.rimWidth / 2.0f)
                        - (this.contourSize / 2.0f), this.circleBounds.right
                        + (this.rimWidth / 2.0f) + (this.contourSize / 2.0f),
                this.circleBounds.bottom + (this.rimWidth / 2.0f)
                        + (this.contourSize / 2.0f));

        this.fullRadius = (width - this.paddingRight - this.barWidth) / 2;
        this.circleRadius = (this.fullRadius - this.barWidth) + 1;
    }

    /**
     * Parse the attributes passed to the view from the XML
     * 
     * @param a
     *            the attributes to parse
     */
    private void parseAttributes(TypedArray a)
    {
        this.barWidth = (int) a.getDimension(
                R.styleable.ProgressWheel_barWidth, this.barWidth);

        this.rimWidth = (int) a.getDimension(
                R.styleable.ProgressWheel_rimWidth, this.rimWidth);

        this.spinSpeed = (int) a.getDimension(
                R.styleable.ProgressWheel_spinSpeed, this.spinSpeed);

        this.delayMillis = a.getInteger(R.styleable.ProgressWheel_delayMillis,
                this.delayMillis);
        if (this.delayMillis < 0)
        {
            this.delayMillis = 0;
        }

        this.barColor = a.getColor(R.styleable.ProgressWheel_barColor,
                this.barColor);

        this.barLength = (int) a.getDimension(
                R.styleable.ProgressWheel_barLength, this.barLength);

        this.textSize = (int) a.getDimension(
                R.styleable.ProgressWheel_textSize, this.textSize);

        this.textColor = a.getColor(R.styleable.ProgressWheel_textColor,
                this.textColor);

        // if the text is empty , so ignore it
        if (a.hasValue(R.styleable.ProgressWheel_text))
        {
            setText(a.getString(R.styleable.ProgressWheel_text));
        }

        this.rimColor = a.getColor(R.styleable.ProgressWheel_rimColor,
                this.rimColor);

        this.circleColor = a.getColor(R.styleable.ProgressWheel_circleColor,
                this.circleColor);

        this.contourColor = a.getColor(R.styleable.ProgressWheel_contourColor,
                this.contourColor);
        this.contourSize = a.getDimension(
                R.styleable.ProgressWheel_contourSize, this.contourSize);

        // Recycle
        a.recycle();
    }

    // ----------------------------------
    // Animation stuff
    // ----------------------------------

    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);
        // Draw the inner circle
        canvas.drawArc(this.circleBounds, 360, 360, false, this.circlePaint);
        // Draw the rim
        canvas.drawArc(this.circleBounds, 360, 360, false, this.rimPaint);
        canvas.drawArc(this.circleOuterContour, 360, 360, false,
                this.contourPaint);
        canvas.drawArc(this.circleInnerContour, 360, 360, false,
                this.contourPaint);
        // Draw the bar
        if (this.isSpinning)
        {
            canvas.drawArc(this.circleBounds, this.progress - 90,
                    this.barLength, false, this.barPaint);
        }
        else
        {
            canvas.drawArc(this.circleBounds, -90, this.progress, false,
                    this.barPaint);
        }
        // Draw the text (attempts to center it horizontally and vertically)
        float textHeight = this.textPaint.descent() - this.textPaint.ascent();
        float verticalTextOffset = (textHeight / 2) - this.textPaint.descent();

        for (String s : this.splitText)
        {
            float horizontalTextOffset = this.textPaint.measureText(s) / 2;
            canvas.drawText(s, this.getWidth() / 2 - horizontalTextOffset,
                    this.getHeight() / 2 + verticalTextOffset, this.textPaint);
        }
        if (this.isSpinning)
        {
            scheduleRedraw();
        }
    }

    private void scheduleRedraw()
    {
        this.progress += this.spinSpeed;
        if (this.progress > 360)
        {
            this.progress = 0;
        }
        postInvalidateDelayed(this.delayMillis);
    }

    /**
     * Check if the wheel is currently spinning
     */

    public boolean isSpinning()
    {
        return this.isSpinning;
    }

    /**
     * Reset the count (in increment mode)
     */
    public void resetCount()
    {
        this.progress = 0;
        setText("0%");
        invalidate();
    }

    /**
     * Turn off spin mode
     */
    public void stopSpinning()
    {
        this.isSpinning = false;
        this.progress = 0;
        postInvalidate();
    }

    /**
     * Puts the view on spin mode
     */
    public void spin()
    {
        this.isSpinning = true;
        postInvalidate();
    }

    /**
     * Increment the progress by 1 (of 360)
     */
    public void incrementProgress()
    {
        this.isSpinning = false;
        this.progress++;
        if (this.progress > 360)
        {
            this.progress = 0;
        }
        // setText(Math.round(((float) progress / 360) * 100) + "%");
        postInvalidate();
    }

    /**
     * Set the progress to a specific value
     */
    public void setProgress(int i)
    {
        this.isSpinning = false;
        this.progress = i;
        postInvalidate();
    }

    // ----------------------------------
    // Getters + setters
    // ----------------------------------

    /**
     * Set the text in the progress bar Doesn't invalidate the view
     * 
     * @param text
     *            the text to show ('\n' constitutes a new line)
     */
    public void setText(String text)
    {
        this.text = text;
        this.splitText = this.text.split("\n");
    }

    public int getCircleRadius()
    {
        return this.circleRadius;
    }

    public void setCircleRadius(int circleRadius)
    {
        this.circleRadius = circleRadius;
    }

    public int getBarLength()
    {
        return this.barLength;
    }

    public void setBarLength(int barLength)
    {
        this.barLength = barLength;
    }

    public int getBarWidth()
    {
        return this.barWidth;
    }

    public void setBarWidth(int barWidth)
    {
        this.barWidth = barWidth;

        if (this.barPaint != null)
        {
            this.barPaint.setStrokeWidth(this.barWidth);
        }
    }

    public int getTextSize()
    {
        return this.textSize;
    }

    public void setTextSize(int textSize)
    {
        this.textSize = textSize;

        if (this.textPaint != null)
        {
            this.textPaint.setTextSize(this.textSize);
        }
    }

    @Override
    public int getPaddingTop()
    {
        return this.paddingTop;
    }

    public void setPaddingTop(int paddingTop)
    {
        this.paddingTop = paddingTop;
    }

    @Override
    public int getPaddingBottom()
    {
        return this.paddingBottom;
    }

    public void setPaddingBottom(int paddingBottom)
    {
        this.paddingBottom = paddingBottom;
    }

    @Override
    public int getPaddingLeft()
    {
        return this.paddingLeft;
    }

    public void setPaddingLeft(int paddingLeft)
    {
        this.paddingLeft = paddingLeft;
    }

    @Override
    public int getPaddingRight()
    {
        return this.paddingRight;
    }

    public void setPaddingRight(int paddingRight)
    {
        this.paddingRight = paddingRight;
    }

    public int getBarColor()
    {
        return this.barColor;
    }

    public void setBarColor(int barColor)
    {
        this.barColor = barColor;

        if (this.barPaint != null)
        {
            this.barPaint.setColor(this.barColor);
        }
    }

    public int getCircleColor()
    {
        return this.circleColor;
    }

    public void setCircleColor(int circleColor)
    {
        this.circleColor = circleColor;

        if (this.circlePaint != null)
        {
            this.circlePaint.setColor(this.circleColor);
        }
    }

    public int getRimColor()
    {
        return this.rimColor;
    }

    public void setRimColor(int rimColor)
    {
        this.rimColor = rimColor;

        if (this.rimPaint != null)
        {
            this.rimPaint.setColor(this.rimColor);
        }
    }

    public Shader getRimShader()
    {
        return this.rimPaint.getShader();
    }

    public void setRimShader(Shader shader)
    {
        this.rimPaint.setShader(shader);
    }

    public int getTextColor()
    {
        return this.textColor;
    }

    public void setTextColor(int textColor)
    {
        this.textColor = textColor;

        if (this.textPaint != null)
        {
            this.textPaint.setColor(this.textColor);
        }
    }

    public int getSpinSpeed()
    {
        return this.spinSpeed;
    }

    public void setSpinSpeed(int spinSpeed)
    {
        this.spinSpeed = spinSpeed;
    }

    public int getRimWidth()
    {
        return this.rimWidth;
    }

    public void setRimWidth(int rimWidth)
    {
        this.rimWidth = rimWidth;

        if (this.rimPaint != null)
        {
            this.rimPaint.setStrokeWidth(this.rimWidth);
        }
    }

    public int getDelayMillis()
    {
        return this.delayMillis;
    }

    public void setDelayMillis(int delayMillis)
    {
        this.delayMillis = delayMillis;
    }

    public int getContourColor()
    {
        return this.contourColor;
    }

    public void setContourColor(int contourColor)
    {
        this.contourColor = contourColor;

        if (this.contourPaint != null)
        {
            this.contourPaint.setColor(this.contourColor);
        }
    }

    public float getContourSize()
    {
        return this.contourSize;
    }

    public void setContourSize(float contourSize)
    {
        this.contourSize = contourSize;

        if (this.contourPaint != null)
        {
            this.contourPaint.setStrokeWidth(this.contourSize);
        }
    }
}
