package com.personal.keymonkmodern;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class SettingsActivity extends Activity {
    @Override public void onCreate(Bundle b) { super.onCreate(b); TextView v = new TextView(this); v.setPadding(32,32,32,32); v.setText("Keymonk Modern DualSwipe\n\nEnable this keyboard in Android settings, then select it as your input method.\n\nVersion 0.1: QWERTY + two-finger swipe path debug."); setContentView(v); }
}
