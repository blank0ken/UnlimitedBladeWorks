package me.shangqu.unlimitedbladeworks.widget;

import android.text.Editable;
import android.text.InputFilter;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;

import java.util.LinkedList;

public class BankCardTextWatcher implements TextWatcher {

    private EditText mEt;
    private Button mBtn;
    private final String REGEXP = "^(\\d{4}\\s)*\\d{0,4}(?<!\\s)$";
    private boolean mIsUpdating = false;

    private InputFilter mFilter;

    public BankCardTextWatcher(Button mBtn, EditText mEt) {
        this.mBtn = mBtn;
        this.mEt = mEt;
        mFilter = new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end,
                                       Spanned dest, int dstart, int dend) {
                boolean keepOriginal = true;
                StringBuilder sb = new StringBuilder(end - start);
                for (int i = start; i < end; i++) {
                    char c = source.charAt(i);
                    if (isCharAllowed(c)) {
                        sb.append(c);
                    } else {
                        keepOriginal = false;
                    }
                }
                if (keepOriginal) {
                    return null;
                } else {
                    if (source instanceof Spanned) {
                        SpannableString sp = new SpannableString(sb);
                        TextUtils.copySpansFrom((Spanned) source, start, sb.length(), null, sp, 0);
                        return sp;
                    } else {
                        return sb;
                    }
                }
            }

            private boolean isCharAllowed(char c) {
                return Character.isDigit(c) || Character.isSpaceChar(c);
            }
        };
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    @Override
    public void afterTextChanged(Editable editable) {

        editable.setFilters(new InputFilter[]{mFilter});
        String originalString = editable.toString();
        if (mIsUpdating || originalString.matches(REGEXP)) {
            return;
        }

        mIsUpdating = true;

        //delete the whitespace
        LinkedList<Integer> spaceIndices = new LinkedList<Integer>();
        for (int index = originalString.indexOf(' '); index >= 0; index = originalString.indexOf(' ', index + 1)) {
            spaceIndices.offerLast(index);
        }
        Integer spaceIndex = null;
        while (!spaceIndices.isEmpty()) {
            spaceIndex = spaceIndices.removeLast();
            editable.delete(spaceIndex, spaceIndex + 1);
        }

        //add whitespaces, up to four time
        for (int i = 0; ((i + 1) * 4 + i) < editable.length(); i++) {
            if (editable.toString().split(" ").length <= 4) {
                editable.insert((i + 1) * 4 + i, " ");
            }
        }

        //move the cursor before the space after delete
        int cursorPos = mEt.getSelectionStart();
        if (cursorPos > 0 && editable.charAt(cursorPos - 1) == ' ') {
            mEt.setSelection(cursorPos - 1);
        }
        //unset the flag, to notify that updating has finished
        mIsUpdating = false;

        int length = mEt.getText().toString().length();
        if (length >= 18 && length <= 23) {
            mBtn.setClickable(true);
            mBtn.setEnabled(true);
        } else {
            mBtn.setClickable(false);
            mBtn.setEnabled(false);
        }

    }

    public static String getCardNum(EditText et) {
        if (et == null) {
            return "";
        }
        Editable editable = et.getText();
        final String originCardNum = editable == null ? "" : editable.toString();
        char[] array = originCardNum.toCharArray();
        char[] newArray = new char[array.length];
        for (int i = 0; i < newArray.length; i++) {
            newArray[i] = '\0';
        }
        for (int i = 0, j = 0; i < array.length; i++) {
            if (array[i] >= '0' && array[i] <= '9') {
                newArray[j++] = array[i];
            }
        }
        String tmp = new String(newArray);
        return tmp.trim();
    }
}