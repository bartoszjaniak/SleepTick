package pl.aaronrose.bj.sleeptick;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;


public class SelectionOption {
    private TextView textView;
    private LinearLayout linear;
    private Context context;
    private boolean isCheck;

    public SelectionOption(Context context, TextView textView, LinearLayout linear){
        this.textView = textView;
        this.linear = linear;
        this.context = context;
    }

    public void setValue(String value){
        textView.setText(value);
    }

    public LinearLayout getLinear(){
        return linear;
    }

    public void check(){
        linear.setBackgroundColor(context.getResources().getColor(R.color.checkedBackground));
        this.isCheck = true;
    }
    public void uncheck(){
        linear.setBackgroundColor(context.getResources().getColor(R.color.white));
        this.isCheck = true;
    }
    public void visible(){
        linear.setVisibility(View.VISIBLE);
    }
    public void invisible(){
        linear.setVisibility(View.INVISIBLE);
    }


    public boolean isChecked(){
        return isCheck;
    }

}
