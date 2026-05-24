package com.personal.keymonkmodern;

import android.content.Context;
import android.graphics.*;
import android.inputmethodservice.InputMethodService;
import android.view.*;
import android.view.inputmethod.InputConnection;
import java.util.*;

public class DualSwipeKeyboardView extends View {
    private static class Key { RectF r; String label; Key(RectF r, String label){this.r=r;this.label=label;} }
    private static class Stroke { Path path = new Path(); StringBuilder keys = new StringBuilder(); String last = ""; }
    private final InputMethodService ime;
    private final Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final ArrayList<Key> keys = new ArrayList<>();
    private final HashMap<Integer, Stroke> strokes = new HashMap<>();
    private final String[] rows = {"qwertyuiop", "asdfghjkl", "zxcvbnm"};
    private String debug = "Dual-swipe debug: slide with two fingers";
    public DualSwipeKeyboardView(Context c, InputMethodService ime){ super(c); this.ime=ime; setFocusable(true); setFocusableInTouchMode(true); }

    @Override protected void onMeasure(int ws, int hs){ int w=MeasureSpec.getSize(ws); int h=(int)(getResources().getDisplayMetrics().density*300); setMeasuredDimension(w,h); }

    @Override protected void onSizeChanged(int w,int h,int oldw,int oldh){ layoutKeys(w,h); }
    private void layoutKeys(int w,int h){ keys.clear(); float gap=6, top=52, rowH=(h-top-12)/4f; for(int ri=0;ri<rows.length;ri++){ String row=rows[ri]; float keyW=(w-gap*(row.length()+1))/row.length(); float x=gap + (ri==1? keyW*.35f: ri==2? keyW*1.05f:0); float y=top+ri*rowH; for(int i=0;i<row.length();i++){ keys.add(new Key(new RectF(x,y,x+keyW,y+rowH-gap), row.substring(i,i+1))); x+=keyW+gap; }} float y=top+3*rowH; keys.add(new Key(new RectF(gap,y,w*.22f,y+rowH-gap),"⌫")); keys.add(new Key(new RectF(w*.24f,y,w*.74f,y+rowH-gap),"space")); keys.add(new Key(new RectF(w*.76f,y,w-gap,y+rowH-gap),"↵")); }

    @Override protected void onDraw(Canvas c){ super.onDraw(c); p.setStyle(Paint.Style.FILL); p.setColor(Color.rgb(30,30,34)); c.drawRect(0,0,getWidth(),getHeight(),p); p.setColor(Color.rgb(48,48,54)); c.drawRect(0,0,getWidth(),48,p); p.setColor(Color.WHITE); p.setTextSize(16*getResources().getDisplayMetrics().scaledDensity); c.drawText(debug,14,31,p); for(Key k:keys){ p.setStyle(Paint.Style.FILL); p.setColor(Color.rgb(70,70,78)); c.drawRoundRect(k.r,16,16,p); p.setStyle(Paint.Style.STROKE); p.setStrokeWidth(1); p.setColor(Color.rgb(110,110,120)); c.drawRoundRect(k.r,16,16,p); p.setStyle(Paint.Style.FILL); p.setColor(Color.WHITE); p.setTextAlign(Paint.Align.CENTER); p.setTextSize(22*getResources().getDisplayMetrics().scaledDensity); Paint.FontMetrics fm=p.getFontMetrics(); c.drawText(k.label,k.r.centerX(),k.r.centerY()-(fm.ascent+fm.descent)/2,p); } p.setTextAlign(Paint.Align.LEFT); p.setStyle(Paint.Style.STROKE); p.setStrokeWidth(7); p.setColor(Color.argb(220,255,255,255)); for(Stroke s:strokes.values()) c.drawPath(s.path,p); }

    @Override public boolean onTouchEvent(android.view.MotionEvent e){ int action=e.getActionMasked(); int idx=e.getActionIndex(); if(action==MotionEvent.ACTION_DOWN || action==MotionEvent.ACTION_POINTER_DOWN){ int id=e.getPointerId(idx); Stroke s=new Stroke(); s.path.moveTo(e.getX(idx),e.getY(idx)); strokes.put(id,s); addKey(s,e.getX(idx),e.getY(idx)); }
        else if(action==MotionEvent.ACTION_MOVE){ for(int i=0;i<e.getPointerCount();i++){ Stroke s=strokes.get(e.getPointerId(i)); if(s!=null){ s.path.lineTo(e.getX(i),e.getY(i)); addKey(s,e.getX(i),e.getY(i)); } } }
        else if(action==MotionEvent.ACTION_UP || action==MotionEvent.ACTION_POINTER_UP || action==MotionEvent.ACTION_CANCEL){ int id=e.getPointerId(idx); Stroke s=strokes.get(id); if(s!=null){ s.path.lineTo(e.getX(idx),e.getY(idx)); addKey(s,e.getX(idx),e.getY(idx)); } if(action==MotionEvent.ACTION_UP || action==MotionEvent.ACTION_CANCEL){ commitGestureOrTap(); strokes.clear(); } else { strokes.remove(id); } }
        invalidate(); return true; }
    private void addKey(Stroke s,float x,float y){ String lab=hit(x,y); if(lab!=null && !lab.equals(s.last)){ if(lab.length()==1) s.keys.append(lab); s.last=lab; debug="path: "+s.keys; } }
    private String hit(float x,float y){ for(Key k:keys) if(k.r.contains(x,y)) return k.label; return null; }
    private void commitGestureOrTap(){ InputConnection ic=ime.getCurrentInputConnection(); if(ic==null) return; ArrayList<String> parts=new ArrayList<>(); for(Stroke s:strokes.values()) if(s.keys.length()>0) parts.add(s.keys.toString()); String out=""; if(parts.size()==0) return; if(parts.size()==1 && parts.get(0).length()==1) out=parts.get(0); else out="["+join(parts," | ")+"]"; debug="committed: "+out; ic.commitText(out,1); }
    private static String join(ArrayList<String> xs,String sep){ StringBuilder b=new StringBuilder(); for(int i=0;i<xs.size();i++){ if(i>0)b.append(sep); b.append(xs.get(i)); } return b.toString(); }
}
