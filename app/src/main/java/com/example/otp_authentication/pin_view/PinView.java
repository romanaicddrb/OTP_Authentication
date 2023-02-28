package com.example.otp_authentication.pin_view;

import android.content.ClipboardManager;
import android.content.Context;
import android.content.res.TypedArray;

import android.graphics.drawable.Drawable;
import androidx.appcompat.widget.LinearLayoutCompat;
import android.text.InputFilter;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.MenuItem;
import android.widget.EditText;
import com.example.otp_authentication.R;

import java.util.ArrayList;


public class PinView extends LinearLayoutCompat {

    private final float DEFAULT_PIN_TEXT_SIZE=23;
    private final int DEFAULT_PIN_COUNT=6;
    private Context context;
    private String pinText="";
    private int pinSize = getResources().getDimensionPixelSize(R.dimen.pin_size);
    private float pinTextSize=DEFAULT_PIN_TEXT_SIZE;
    private int pinTextColor = getResources().getColor(android.R.color.black);
    private short pinCount=DEFAULT_PIN_COUNT, inputType= InputType.TYPE_NUMBER;
    private boolean isSecure = false;
    private Drawable background;
    private ArrayList<EditText> editTextsArrayList =new ArrayList<>();
    private ArrayList<PinTextWatcher> textWatcherArrayList =new ArrayList<>();
    static OnPinCompletedListener onPinCompletionListener;

    public PinView(Context context) {
        super(context);
        this.context=context;
        this.setOrientation(HORIZONTAL);
        this.setGravity(Gravity.CENTER);

        addPins();
    }

    public PinView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context=context;
        this.setOrientation(HORIZONTAL);
        this.setGravity(Gravity.CENTER);

        setStyleAndPins(attrs);
    }

    public PinView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context=context;
        this.setOrientation(HORIZONTAL);
        this.setGravity(Gravity.CENTER);

        setStyleAndPins(attrs);
    }


    private void setStyleAndPins(AttributeSet attrs){

        TypedArray a=context.obtainStyledAttributes(attrs,R.styleable.PinView);
        pinCount=(short)a.getInteger(R.styleable.PinView_pinCount,DEFAULT_PIN_COUNT);
        inputType=(short)a.getInteger(R.styleable.PinView_inputType,InputType.TYPE_TEXT);
        pinSize =a.getDimensionPixelSize(R.styleable.PinView_pinSize,pinSize);
        isSecure = a.getBoolean(R.styleable.PinView_isSecure, isSecure);

        pinText =a.getString(R.styleable.PinView_pinText);
        pinTextSize =a.getDimension(R.styleable.PinView_pinTextSize,DEFAULT_PIN_TEXT_SIZE);
        pinTextColor =a.getColor(R.styleable.PinView_textColor,pinTextColor);

        if(a.hasValue(R.styleable.PinView_pinBackground)){
            background=a.getDrawable(R.styleable.PinView_pinBackground);
        }
        a.recycle();

        addPins();
    }

    @Override
    protected void onCreateContextMenu(ContextMenu menu) {
        super.onCreateContextMenu(menu);

        menu.add(0, 0, 0, "Paste").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                ClipboardManager clipboardManager=(ClipboardManager)context.getSystemService(Context.CLIPBOARD_SERVICE);
                String text=clipboardManager.getPrimaryClip().getItemAt(0).getText().toString();
                setText(text);
                return true;
            }
        });
    }

    private void addPins(){
        this.removeAllViews();
        editTextsArrayList.clear();
        for (int i=0; i<getPinCount(); i++){
            EditText editText=new EditText(context);
            editText.setGravity(Gravity.CENTER);
            LinearLayoutCompat.LayoutParams layoutParams=new LinearLayoutCompat.
                                        LayoutParams(pinSize, pinSize/*,1*/);
            layoutParams.setMargins(getResources().getDimensionPixelSize(R.dimen.margin_pin_edit_text),getResources().getDimensionPixelSize(R.dimen.margin_pin_edit_text),
                    getResources().getDimensionPixelSize(R.dimen.margin_pin_edit_text),getResources().getDimensionPixelSize(R.dimen.margin_pin_edit_text));
            editText.setLayoutParams(layoutParams);
            editText.setTextSize(pinTextSize);
            editText.setTextColor(pinTextColor);
            editText.setMaxLines(1);
            editText.setLines(1);
            editText.setPadding(0,0,0,0);
            editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(1), new InputFilter.AllCaps()});
            if(background!=null)
                editText.setBackground(background);
            setPasswordType(editText);
            editTextsArrayList.add(editText);
            this.addView(editText);
        }
        textWatcherArrayList.clear();
        for (int i = 0; i< getPinCount(); i++){
            PinTextWatcher pinTextWatcher=new PinTextWatcher(i, editTextsArrayList);
            textWatcherArrayList.add(pinTextWatcher);
            editTextsArrayList.get(i).addTextChangedListener(pinTextWatcher);
            editTextsArrayList.get(i).setOnKeyListener(new PinOnKeyListener(i, editTextsArrayList));
        }


        if(!isInEditMode() && pinText!=null)
            setText(pinText);

    }

    private void setPasswordType(EditText editText){
        if(isSecure){
            if(inputType==InputType.TYPE_TEXT)
                editText.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);
            else
                editText.setInputType(android.text.InputType.TYPE_CLASS_NUMBER | android.text.InputType.TYPE_NUMBER_VARIATION_PASSWORD);
        }
        else {
            if (inputType == InputType.TYPE_TEXT)
                editText.setInputType(android.text.InputType.TYPE_CLASS_TEXT);
            else
                editText.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
        }
    }

    private short getPinCount(){
        return pinCount;
    }

    /**
     *
     * @return Collective text in the pinview
     */
    public String getText(){
        String text="";
        for (int i=0; i<getPinCount(); i++){
            text+= editTextsArrayList.get(i).getText().toString();
        }
        return text;
    }

    /**
     * Set the pinview text
     * @param text string
     */
    public void setText(String text){
        short validLength=(short) text.length();
        if(validLength>=getPinCount()) validLength=getPinCount();
        for (int i=0; i<validLength; i++){
            editTextsArrayList.get(i).setText(Character.toString(text.charAt(i)));
        }
    }

    /**
     * Total number of pins in the pinview
     * <br> Default is 6
     * @param pinCount integer
     */
    public void setPinCount(int pinCount){
        this.pinCount=(short) pinCount;
        addPins();
    }

    /**
     *
     * <br> Default is Number
     * @param inputType Takes input as InputType.TYPE_NUMBER or InputType.TYPE_TEXT
     */
    public void setInputType(short inputType){
        this.inputType= inputType;
        for (int i=0; i<getPinCount(); i++){
            if(inputType==InputType.TYPE_TEXT) {
                if(isSecure)
                    editTextsArrayList.get(i).setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);
                else
                    editTextsArrayList.get(i).setInputType(android.text.InputType.TYPE_CLASS_TEXT);
            }
            else {
                if(isSecure)
                    editTextsArrayList.get(i).setInputType(android.text.InputType.TYPE_CLASS_NUMBER| android.text.InputType.TYPE_NUMBER_VARIATION_PASSWORD);
                else
                    editTextsArrayList.get(i).setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
            }
        }
    }

    public void setPassword(boolean isSecure){
        this.isSecure=isSecure;

        textWatcherArrayList.get(0).setProcessing(true);
        if(isSecure){
            for (int i=0; i<getPinCount(); i++) {
                if (inputType == InputType.TYPE_NUMBER)
                    editTextsArrayList.get(i).setInputType(android.text.InputType.TYPE_CLASS_NUMBER | android.text.InputType.TYPE_NUMBER_VARIATION_PASSWORD);
                else
                    editTextsArrayList.get(i).setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);
            }
        }
        else {
            setInputType(inputType);
        }
    }

    /**
     * Sets the drawable background of individual pin
     * <br> Default is null
     * @param backgroundDrawable Drawable to use as the background
     */
    public void setPinBackground(Drawable backgroundDrawable){
        background=backgroundDrawable;
        for (int i=0; i<getPinCount(); i++) {
            editTextsArrayList.get(i).setBackground(background);
        }
    }

    /**
     * Default value is 50dp
     * @param sizeInDp Width of the individual pin. Is also applied to height.
     */
    public void setPinSize(int sizeInDp){
        this.pinSize =sizeInDp;

        for (int i=0; i<getPinCount(); i++) {
            LinearLayoutCompat.LayoutParams layoutParams=new LinearLayoutCompat.
                LayoutParams(convertDpToPixel(sizeInDp,context),
                    convertDpToPixel(sizeInDp,context)/*,1*/);
            layoutParams.setMargins(getResources().getDimensionPixelSize(R.dimen.margin_pin_edit_text),getResources().getDimensionPixelSize(R.dimen.margin_pin_edit_text),
                    getResources().getDimensionPixelSize(R.dimen.margin_pin_edit_text),getResources().getDimensionPixelSize(R.dimen.margin_pin_edit_text));
            editTextsArrayList.get(i).setLayoutParams(layoutParams);
        }
    }


    /**
     * Set a listener to detect when all the pins are filled
     * @param onPinCompletionListener instance of OnPinCompletionListener
     */
    public void setOnPinCompletionListener(OnPinCompletedListener onPinCompletionListener){
        this.onPinCompletionListener=onPinCompletionListener;
    }

    private int convertDpToPixel(float dp, Context context) {
        return Math.round(dp * ((float) context.getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT));
    }

    static void onPinCompleted(String entirePin) {
        onPinCompletionListener.onPinCompleted(entirePin);
    }

    /**
     * Requests focus on the pin at index zero
     */
    public void requestPinFocus(){
        editTextsArrayList.get(0).requestFocus();
    }

    /**
     * Request focus on the pin at specified index, if index is invalid defaults to zeroth pin
     * @param index index of the pin
     */
    public void requestPinFocus(int index){
        if(index>0 && index<getPinCount())
            editTextsArrayList.get(index).requestFocus();
        else
            editTextsArrayList.get(0).requestFocus();
    }

    /**
     * Textsize of the pins
     * @param textSize size in sp
     */
    public void setPinTextSize(float textSize){
        pinTextSize=textSize;
        for (int i=0; i<getPinCount(); i++){
            editTextsArrayList.get(i).setTextSize(pinTextSize);
        }
    }

    /**
     * Text color of the pins
     * @param textColor
     */
    public void setTextColor(int textColor){
        pinTextColor=textColor;
        for (int i=0; i<getPinCount(); i++){
            editTextsArrayList.get(i).setTextColor(pinTextColor);
        }
    }

}
