package com.example.pp_lab3_poved;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;

public class ChainActivity extends AppCompatActivity {


    //-------------------
    public abstract class Middleware {
        private Middleware next;

        /**
         * Помогает строить цепь из объектов-проверок.
         */
        public Middleware linkWith(Middleware next) {
            this.next = next;
            return next;
        }

        /**
         * Подклассы реализуют в этом методе конкретные проверки.
         */
        public abstract boolean check(String email, String password);

        /**
         * Запускает проверку в следующем объекте или завершает проверку, если мы в
         * последнем элементе цепи.
         */
        protected boolean checkNext(String email, String password) {
            if (next == null) {
                return true;
            }
            return next.check(email, password);
        }
    }
    //-------------------
    public class ThrottlingMiddleware extends Middleware {
        private int requestPerMinute;
        private int request;
        private long currentTime;

        public ThrottlingMiddleware(int requestPerMinute) {
            this.requestPerMinute = requestPerMinute;
            this.currentTime = System.currentTimeMillis();
        }

        /**
         * Обратите внимание, вызов checkNext() можно вставить как в начале этого
         * метода, так и в середине или в конце.
         *
         * Это даёт еще один уровень гибкости по сравнению с проверками в цикле.
         * Например, элемент цепи может пропустить все остальные проверки вперёд и
         * запустить свою проверку в конце.
         */
        public boolean check(String email, String password) {
            if (System.currentTimeMillis() > currentTime + 60_000) {
                request = 0;
                currentTime = System.currentTimeMillis();
            }

            request++;

            if (request > requestPerMinute) {
                tv.setText(tv.getText()+"\n"+"Request limit exceeded!");
                //System.out.println("Request limit exceeded!");
                Thread.currentThread().stop();
            }
            return checkNext(email, password);
        }
    }
    //-------------------
    public class UserExistsMiddleware extends Middleware {
        private Server server;

        public UserExistsMiddleware(Server server) {
            this.server = server;
        }

        public boolean check(String email, String password) {
            if (!server.hasEmail(email)) {
                tv.setText(tv.getText()+"\n"+"This email is not registered!");
                //System.out.println("This email is not registered!");
                return false;
            }
            if (!server.isValidPassword(email, password)) {
                tv.setText(tv.getText()+"\n"+"Wrong password!");
                //System.out.println("Wrong password!");
                return false;
            }
            return checkNext(email, password);
        }
    }
    //-------------------
    public class RoleCheckMiddleware extends Middleware {
        public boolean check(String email, String password) {
            if (email.equals("admin@example.com")) {
                tv.setText(tv.getText()+"\n"+"Hello, admin!");
                //System.out.println("Hello, admin!");
                return true;
            }
            tv.setText(tv.getText()+"\n"+"Hello, user!");
            //System.out.println("Hello, user!");
            return checkNext(email, password);
        }
    }
    //-------------------
    public class Server {
        private Map<String, String> users = new HashMap<>();
        private Middleware middleware;

        /**
         * Клиент подаёт готовую цепочку в сервер. Это увеличивает гибкость и
         * упрощает тестирование класса сервера.
         */
        public void setMiddleware(Middleware middleware) {
            this.middleware = middleware;
        }

        /**
         * Сервер получает email и пароль от клиента и запускает проверку
         * авторизации у цепочки.
         */
        public boolean logIn(String email, String password) {
            if (middleware.check(email, password)) {
                tv.setText(tv.getText()+"\n"+"Authorization have been successful!");
                //System.out.println("Authorization have been successful!");

                // Здесь должен быть какой-то полезный код, работающий для
                // авторизированных пользователей.

                return true;
            }
            return false;
        }

        public void register(String email, String password) {
            users.put(email, password);
        }

        public boolean hasEmail(String email) {
            return users.containsKey(email);
        }

        public boolean isValidPassword(String email, String password) {
            return users.get(email).equals(password);
        }
    }
    //-------------------

    //-------------------
    //-------------------

    static TextView tv;
    CheckBox cb1;
    CheckBox cb2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chain);
        cb1=(CheckBox)findViewById(R.id.first_obj);
        cb2=(CheckBox)findViewById(R.id.second_obj);
        tv=(TextView)findViewById(R.id.tv);
    }

    public void create(View view){
        Server server = new Server();
        server.register("admin@example.com", "admin_pass");
        server.register("user@example.com", "user_pass");

        // Проверки связаны в одну цепь. Клиент может строить различные цепи,
        // используя одни и те же компоненты.
        Middleware middleware = new ThrottlingMiddleware(2);
        middleware.linkWith(new UserExistsMiddleware(server))
                .linkWith(new RoleCheckMiddleware());

        // Сервер получает цепочку от клиентского кода.
        server.setMiddleware(middleware);
        boolean succces;
        String email="";
        String password="";
        if(cb1.isChecked()){
            email="admin@example.com";
            password="admin_pass";
        }else{
            email="user@example.com";
            password="user_pass";
        }
        succces = server.logIn(email, password);
    }

    public void clear(View view){
        tv.setText("");
    }

}
