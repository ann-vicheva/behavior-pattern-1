package com.example.pp_lab3_poved;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

public class MediatorActivity extends AppCompatActivity {

    //------------------------
    interface ChatRoomMediator
    {
        public String showMessage(User user, String message);
    }

    // Посредник
    class ChatRoom implements ChatRoomMediator
    {
        public String showMessage(User user, String message)
        {
            return "Feb 14, 10:58 ["+user.getName()+"]: "+message;
        }
    }
    //------------------------
    class User {
        protected String name;
        protected ChatRoomMediator chatMediator;

        public User(String name, ChatRoomMediator chatMediator) {
            this.name = name;
            this.chatMediator = chatMediator;
        }

        public String getName() {
            return this.name;
        }

        public String send(String message) {
            return this.chatMediator.showMessage(this, message);
        }
    }
    //------------------------

    TextView tv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mediator);
        tv=(TextView)findViewById(R.id.tv);
    }

    public void create(View view){
        ChatRoom mediator = new ChatRoom();

        User john = new User("John Doe", mediator);
        User jane = new User("Jane Doe", mediator);

        tv.setText(tv.getText()+"\n"+john.send("Привет, jane!"));
        tv.setText(tv.getText()+"\n"+jane.send("Привет, john!"));

    }

    public void clear(View view){
        tv.setText("");
    }

}
