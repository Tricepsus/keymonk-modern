package com.personal.keymonkmodern;

import android.inputmethodservice.InputMethodService;
import android.view.View;

public class KeymonkImeService extends InputMethodService {
    @Override public View onCreateInputView() { return new DualSwipeKeyboardView(this, this); }
}
