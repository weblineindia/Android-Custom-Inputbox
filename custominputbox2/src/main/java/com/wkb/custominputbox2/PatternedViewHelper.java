package com.wkb.custominputbox2;

import android.content.res.TypedArray;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.EditText;

import com.wkb.custominputbox2.utils.PatternUtils;


public class PatternedViewHelper {

    private String mSpecialChar;
    private String mPattern;

    private String mRawText = "";

    private EditText editText;

    public PatternedViewHelper(EditText et) {
        this.editText = et;
    }

    public void resolveAttributes(AttributeSet attrs) {
        if (attrs == null) return;
        TypedArray a = editText.getContext().obtainStyledAttributes(attrs, R.styleable.CustomEditText);
        mPattern = a.getString(R.styleable.CustomEditText_edt_pattern);
        mSpecialChar = a.getString(R.styleable.CustomEditText_edt_specialChar);
        if (mSpecialChar == null) {
            mSpecialChar = "#";
        }
        boolean showHint = a.getBoolean(R.styleable.CustomEditText_edt_showPatternAsHint, false);
        a.recycle();


        if (mPattern != null) {
            /**
             *Set pattern with user input using xml attribute.
             */

            editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(mPattern.length())});

            if (showHint) {
                editText.setHint(mPattern);
            }

            final TextWatcher textWatcher = new TextWatcher() {
                private boolean mForcing = false;
                private StringBuilder sb;

                private boolean isDeleting = false;

                private int differenceCount = 0;
                public int toBeSetCursorPosition = 0;
                public int mBeforeTextLength = 0;

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    mBeforeTextLength = s.length();
                    if (!mForcing) {
                        sb = new StringBuilder();
                        differenceCount = PatternUtils.getDifferenceCount(s.toString().substring(0, editText.getSelectionStart()), mPattern, mSpecialChar.charAt(0));
                        sb.append(mRawText);
                        if (after == 0) {
                            isDeleting = true;
                            try {
                                sb.delete(editText.getSelectionEnd() - count - differenceCount, editText.getSelectionEnd() - differenceCount);
                            } catch (IndexOutOfBoundsException e) {
                                //Do nothing. User tried to delete unremovable char(s) of pattern.
                            }
                        } else {
                            isDeleting = false;
                        }
                    }
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (!mForcing) {
                        if (!isDeleting) {
                            try {
                                int from = editText.getSelectionEnd() - count - differenceCount;
                                if (from < 0) {
                                    from = 0;
                                }
                                sb.insert(from, s.subSequence(start, start + count));
                            } catch (StringIndexOutOfBoundsException e) {
                                Log.e("PatternedEditText: ", e.toString());
                                //getSelectionEnd() returns 0 after screen rotation.
                                //Added to handle filling EditText after rotation.
                                //onRestoreInstanceState() is responsible for setting text.
                            }
                        }
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (!mForcing) {
                        mForcing = true;
                        mRawText = sb.toString();
                        String convertedText;
                        if (PatternUtils.isTextAppliesPattern(mRawText, mPattern, mSpecialChar.charAt(0))) {
                            convertedText = mRawText;
                            mRawText = PatternUtils.convertPatternedTextToText(mRawText, mPattern, mSpecialChar.charAt(0));
                        } else {
                            convertedText = PatternUtils.convertTextToPatternedText(mRawText, mPattern, mSpecialChar.charAt(0));
                        }
                        toBeSetCursorPosition = editText.getSelectionStart() + convertedText.length() - s.length();
                        if (mBeforeTextLength == 0) {
                            toBeSetCursorPosition = convertedText.length();
                        }
                        s.clear();
                        s.append(convertedText);
                        try {
                            if (isDeleting) {
                                if (toBeSetCursorPosition < convertedText.length()) {
                                    ++toBeSetCursorPosition;
                                }
                            } else if (toBeSetCursorPosition != convertedText.length()) {
                                --toBeSetCursorPosition;
                            }
                            editText.setSelection(toBeSetCursorPosition);
                        } catch (IndexOutOfBoundsException e) {
                            Log.e("PatternedEditText: ", e.toString());
                        }
                        mForcing = false;
                    }
                }
            };
            editText.addTextChangedListener(textWatcher);
        }


    }


}
