package com.example.pp_lab3_poved;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IteratorActivity extends AppCompatActivity {

    //--------------------------
    public interface ProfileIterator {
        boolean hasNext();

        Profile getNext();

        void reset();
    }
    //--------------------------
    public class FacebookIterator implements ProfileIterator {
        private Facebook facebook;
        private String type;
        private String email;
        private int currentPosition = 0;
        private List<String> emails = new ArrayList<>();
        private List<Profile> profiles = new ArrayList<>();

        public FacebookIterator(Facebook facebook, String type, String email) {
            this.facebook = facebook;
            this.type = type;
            this.email = email;
        }

        private void lazyLoad() {
            if (emails.size() == 0) {
                List<String> profiles = facebook.requestProfileFriendsFromFacebook(this.email, this.type);
                for (String profile : profiles) {
                    this.emails.add(profile);
                    this.profiles.add(null);
                }
            }
        }

        @Override
        public boolean hasNext() {
            lazyLoad();
            return currentPosition < emails.size();
        }

        @Override
        public Profile getNext() {
            if (!hasNext()) {
                return null;
            }

            String friendEmail = emails.get(currentPosition);
            Profile friendProfile = profiles.get(currentPosition);
            if (friendProfile == null) {
                friendProfile = facebook.requestProfileFromFacebook(friendEmail);
                profiles.set(currentPosition, friendProfile);
            }
            currentPosition++;
            return friendProfile;
        }

        @Override
        public void reset() {
            currentPosition = 0;
        }
    }
    //--------------------------
    public class LinkedInIterator implements ProfileIterator {
        private LinkedIn linkedIn;
        private String type;
        private String email;
        private int currentPosition = 0;
        private List<String> emails = new ArrayList<>();
        private List<Profile> contacts = new ArrayList<>();

        public LinkedInIterator(LinkedIn linkedIn, String type, String email) {
            this.linkedIn = linkedIn;
            this.type = type;
            this.email = email;
        }

        private void lazyLoad() {
            if (emails.size() == 0) {
                List<String> profiles = linkedIn.requestRelatedContactsFromLinkedInAPI(this.email, this.type);
                for (String profile : profiles) {
                    this.emails.add(profile);
                    this.contacts.add(null);
                }
            }
        }

        @Override
        public boolean hasNext() {
            lazyLoad();
            return currentPosition < emails.size();
        }

        @Override
        public Profile getNext() {
            if (!hasNext()) {
                return null;
            }

            String friendEmail = emails.get(currentPosition);
            Profile friendContact = contacts.get(currentPosition);
            if (friendContact == null) {
                friendContact = linkedIn.requestContactInfoFromLinkedInAPI(friendEmail);
                contacts.set(currentPosition, friendContact);
            }
            currentPosition++;
            return friendContact;
        }

        @Override
        public void reset() {
            currentPosition = 0;
        }
    }
    //--------------------------
    public interface SocialNetwork {
        ProfileIterator createFriendsIterator(String profileEmail);

        ProfileIterator createCoworkersIterator(String profileEmail);
    }
    //--------------------------
    public class Facebook implements SocialNetwork {
        private List<Profile> profiles;

        public Facebook(List<Profile> cache) {
            if (cache != null) {
                this.profiles = cache;
            } else {
                this.profiles = new ArrayList<>();
            }
        }

        public Profile requestProfileFromFacebook(String profileEmail) {
            simulateNetworkLatency();
            tv.setText(tv.getText()+"\n"+"Facebook: Loading profile '" + profileEmail + "' over the network...");

            return findProfile(profileEmail);
        }

        public List<String> requestProfileFriendsFromFacebook(String profileEmail, String contactType) {
            simulateNetworkLatency();
            tv.setText(tv.getText()+"\n"+"Facebook: Loading '" + contactType + "' list of '" + profileEmail + "' over the network...");
            Profile profile = findProfile(profileEmail);
            if (profile != null) {
                return profile.getContacts(contactType);
            }
            return null;
        }

        private Profile findProfile(String profileEmail) {
            for (Profile profile : profiles) {
                if (profile.getEmail().equals(profileEmail)) {
                    return profile;
                }
            }
            return null;
        }

        private void simulateNetworkLatency() {
            try {
                Thread.sleep(2500);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }

        @Override
        public ProfileIterator createFriendsIterator(String profileEmail) {
            return new FacebookIterator(this, "friends", profileEmail);
        }

        @Override
        public ProfileIterator createCoworkersIterator(String profileEmail) {
            return new FacebookIterator(this, "coworkers", profileEmail);
        }

    }
    //--------------------------
    public class LinkedIn implements SocialNetwork {
        private List<Profile> contacts;

        public LinkedIn(List<Profile> cache) {
            if (cache != null) {
                this.contacts = cache;
            } else {
                this.contacts = new ArrayList<>();
            }
        }

        public Profile requestContactInfoFromLinkedInAPI(String profileEmail) {
            simulateNetworkLatency();
            tv.setText(tv.getText()+"\n"+"LinkedIn: Loading profile '" + profileEmail + "' over the network...");
            return findContact(profileEmail);
        }

        public List<String> requestRelatedContactsFromLinkedInAPI(String profileEmail, String contactType) {
            simulateNetworkLatency();
            tv.setText(tv.getText()+"\n"+"LinkedIn: Loading '" + contactType + "' list of '" + profileEmail + "' over the network...");

            Profile profile = findContact(profileEmail);
            if (profile != null) {
                return profile.getContacts(contactType);
            }
            return null;
        }

        private Profile findContact(String profileEmail) {
            for (Profile profile : contacts) {
                if (profile.getEmail().equals(profileEmail)) {
                    return profile;
                }
            }
            return null;
        }

        private void simulateNetworkLatency() {
            try {
                Thread.sleep(25);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }

        @Override
        public ProfileIterator createFriendsIterator(String profileEmail) {
            return new LinkedInIterator(this, "friends", profileEmail);
        }

        @Override
        public ProfileIterator createCoworkersIterator(String profileEmail) {
            return new LinkedInIterator(this, "coworkers", profileEmail);
        }
    }
    //--------------------------
    public static class Profile {
        private String name;
        private String email;
        private Map<String, List<String>> contacts = new HashMap<>();

        public Profile(String email, String name, String... contacts) {
            this.email = email;
            this.name = name;

            for (String contact : contacts) {
                String[] parts = contact.split(":");
                String contactType = "friend", contactEmail;
                if (parts.length == 1) {
                    contactEmail = parts[0];
                }
                else {
                    contactType = parts[0];
                    contactEmail = parts[1];
                }
                if (!this.contacts.containsKey(contactType)) {
                    this.contacts.put(contactType, new ArrayList<String>());
                }
                this.contacts.get(contactType).add(contactEmail);
            }
        }

        public String getEmail() {
            return email;
        }

        public String getName() {
            return name;
        }

        public List<String> getContacts(String contactType) {
            if (!this.contacts.containsKey(contactType)) {
                this.contacts.put(contactType, new ArrayList<String>());
            }
            return contacts.get(contactType);
        }
    }
    //--------------------------
    public class SocialSpammer {
        public SocialNetwork network;
        public ProfileIterator iterator;

        public SocialSpammer(SocialNetwork network) {
            this.network = network;
        }

        public void sendSpamToFriends(String profileEmail, String message) {
            tv.setText(tv.getText()+"\n"+"Iterating over friends...");
            iterator = network.createFriendsIterator(profileEmail);
            while (iterator.hasNext()) {
                Profile profile = iterator.getNext();
                sendMessage(profile.getEmail(), message);
            }
        }

        public void sendSpamToCoworkers(String profileEmail, String message) {
            tv.setText(tv.getText()+"\n"+"Iterating over coworkers...");
            iterator = network.createCoworkersIterator(profileEmail);
            while (iterator.hasNext()) {
                Profile profile = iterator.getNext();
                sendMessage(profile.getEmail(), message);
            }
        }

        public void sendMessage(String email, String message) {
            tv.setText(tv.getText()+"\n"+"Sent message to: '" + email + "'. Message body: '" + message + "'");
        }
    }
    //--------------------------
    //--------------------------


    CheckBox cb1;
    CheckBox cb2;
    TextView tv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_iterator);
        cb1=(CheckBox)findViewById(R.id.first_obj);
        cb2=(CheckBox)findViewById(R.id.second_obj);
        tv=(TextView)findViewById(R.id.tv);
    }

    public void create(View view){
        SocialNetwork network;
        if(cb1.isChecked()){
            network = new Facebook(createTestProfiles());
        }else{
            network = new LinkedIn(createTestProfiles());
        }
        SocialSpammer spammer = new SocialSpammer(network);
        spammer.sendSpamToFriends("anna.smith@bing.com",
                "Hey! This is Anna's friend Josh. Can you do me a favor and like this post [link]?");
        spammer.sendSpamToCoworkers("anna.smith@bing.com",
                "Hey! This is Anna's boss Jason. Anna told me you would be interested in [link].");
    }

    public static List<Profile> createTestProfiles() {
        List<Profile> data = new ArrayList<Profile>();
        data.add(new Profile("anna.smith@bing.com", "Anna Smith", "friends:mad_max@ya.com", "friends:catwoman@yahoo.com", "coworkers:sam@amazon.com"));
        data.add(new Profile("mad_max@ya.com", "Maximilian", "friends:anna.smith@bing.com", "coworkers:sam@amazon.com"));
        data.add(new Profile("bill@microsoft.eu", "Billie", "coworkers:avanger@ukr.net"));
        data.add(new Profile("avanger@ukr.net", "John Day", "coworkers:bill@microsoft.eu"));
        data.add(new Profile("sam@amazon.com", "Sam Kitting", "coworkers:anna.smith@bing.com", "coworkers:mad_max@ya.com", "friends:catwoman@yahoo.com"));
        data.add(new Profile("catwoman@yahoo.com", "Liza", "friends:anna.smith@bing.com", "friends:sam@amazon.com"));
        return data;
    }

    public void clear(View view){
        tv.setText("");
    }

}
