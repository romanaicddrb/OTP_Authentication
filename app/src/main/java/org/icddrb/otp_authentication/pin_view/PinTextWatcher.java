package org.icddrb.otp_authentication.pin_view;


import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;


import java.util.ArrayList;

class PinTextWatcher implements TextWatcher {

    private int currentIndex;
    private boolean isFirst = false, isLast = false;
    static boolean isDeleting=false;
    private static boolean isProcessing=false;
    private String newTypedString = "", entirePin="";
    private ArrayList<EditText> pinEditTexts=new ArrayList<>();

    public PinTextWatcher( int currentIndex,ArrayList<EditText> pinEditTexts) {
        this.currentIndex = currentIndex;
        this.pinEditTexts.clear();
        this.pinEditTexts.addAll(pinEditTexts);

        if (currentIndex == 0)
            this.isFirst = true;
        else if (currentIndex == pinEditTexts.size()- 1)
            this.isLast = true;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        newTypedString = s.subSequence(start, start + count).toString().trim();
    }

    @Override
    public void afterTextChanged(Editable s) {

        String text = newTypedString;

        pinEditTexts.get(currentIndex).removeTextChangedListener(this);
        pinEditTexts.get(currentIndex).setText(text);
        pinEditTexts.get(currentIndex).setSelection(text.length());
        pinEditTexts.get(currentIndex).addTextChangedListener(this);

        if(!isProcessing)
            if (text.length() >= 1)
                moveToNext();
//            else {
//                if(!isDeleting)
//                    moveToPrevious();
//                isDeleting=false;
//            }
    }

    private void moveToNext() {
        if (!isLast) {
            pinEditTexts.get(currentIndex).setFocusable(false);
            pinEditTexts.get(currentIndex + 1).requestFocus();
        }

        if (isAllEditTextsFilled() && !isProcessing) {
            if(PinView.onPinCompletionListener!=null){
                PinView.onPinCompleted(entirePin);
            }
        }
    }

    private void moveToPrevious() {
        isDeleting=true;

        if (!isFirst) {
            pinEditTexts.get(currentIndex - 1).setFocusable(true);
            pinEditTexts.get(currentIndex - 1).setText("");
            pinEditTexts.get(currentIndex - 1).setFocusableInTouchMode(true);
            pinEditTexts.get(currentIndex - 1).requestFocus();
        }
    }

    private boolean isAllEditTextsFilled() {
        entirePin="";
        for (EditText editText : pinEditTexts) {
            entirePin+=editText.getText().toString();
            if (editText.getText().toString().trim().length() == 0)
                return false;
        }
        return true;
    }

    void setProcessing(boolean isProcessing){
        this.isProcessing=isProcessing;
    }

}
