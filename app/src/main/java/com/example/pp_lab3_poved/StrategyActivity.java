package com.example.pp_lab3_poved;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

public class StrategyActivity extends AppCompatActivity {

    //----------------------
    public interface PayStrategy {
        boolean pay(int paymentAmount);
        void collectPaymentDetails();
    }
    //----------------------
    public static class PayByPayPal implements PayStrategy {
        public PayByPayPal(){
            tv.setText(tv.getText()+"\n"+"Enter the user's email: user@example.com"+"\n"+
            "Enter the password: qwerty"+"\n"+
            "Enter user email: amanda@ya.com"+"\n"+
            "Data verification has been successful."+"\n"+
            "Payment PayPal has been successful.");
        }

        @Override
        public boolean pay(int paymentAmount) {
            return false;
        }

        @Override
        public void collectPaymentDetails() {

        }
    }
    //----------------------
    public class PayByCreditCard implements PayStrategy {

        public PayByCreditCard() {
            tv.setText(tv.getText()+"\n"+"Enter the number: 1234 5678 9101 1121"+"\n"+
                    "Enter the date: 12/12/2012"+"\n"+
                            "Enter cvv : 444"+"\n"+
                            "Data verification has been successful."+"\n"+
                            "Payment CreditCard has been successful.");
        }

        @Override
        public boolean pay(int paymentAmount) {
            return false;
        }

        @Override
        public void collectPaymentDetails() {

        }
    }
    //----------------------


    static TextView tv;
    CheckBox cb1;
    CheckBox cb2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_strategy);
        cb1=(CheckBox)findViewById(R.id.first_obj);
        cb2=(CheckBox)findViewById(R.id.second_obj);
        tv=(TextView)findViewById(R.id.tv);
    }

    public void create(View view){
        PayStrategy strategy;

        if(cb1.isChecked()){
            strategy = new PayByPayPal();
        }else{
            strategy = new PayByCreditCard();
        }

    }

    public void clear(View view){
        tv.setText("");
    }
}
