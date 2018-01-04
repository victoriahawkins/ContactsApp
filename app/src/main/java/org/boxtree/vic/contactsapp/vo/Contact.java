package org.boxtree.vic.contactsapp.vo;


import android.os.Parcel;
import android.os.Parcelable;

/**
 * This is the Contact value object that is passed from Contact Framgent to the Main Activity and to other activities.
 * It impelements Parcelable so that it can be passed as an object through the Intent IPC subsystem
 */
public class Contact implements Parcelable {

    private String id; // rowid position, but lookup key is more reliable way to refer to record in contact db
    public String name;
    private String nickname;
    private String address;
    private String phone;
    private String email;
    private String lookupKey; // android contacts primary key


    public Contact(String id, String name, String nickname, String address, String phone, String email, String lookupKey) {
        this.id = id;
        this.name = name;
        this.nickname = nickname;
        this.address = address;
        this.phone = phone;
        this.email = email;
        this.lookupKey = lookupKey;
    }


    /* create a contact from a Parcel (passed from intent) */
    public Contact(Parcel contactIn) {
        String[] data = new String[7];
        contactIn.readStringArray(data);
        this.id = data[0];
        this.name = data[1];
        this.nickname = data[2];
        this.address = data[3];
        this.phone = data[4];
        this.email = data[5];
        this.lookupKey = data[6];
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLookupKey() {
        return lookupKey;
    }

    public void setLookupKey(String lookupKey) {
        this.lookupKey = lookupKey;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(
                new String[] {getId(), getName(), getNickname(), getAddress(), getPhone(), getEmail(), getLookupKey()}
        );

    }

    public static final Parcelable.Creator<Contact> CREATOR = new Parcelable.Creator<Contact>() {

        @Override
        public Contact createFromParcel(Parcel source) {
            return new Contact(source);
        }

        @Override
        public Contact[] newArray(int size) {
            return new Contact[size];
        }
    };


    @Override
    public String toString() {
        return "Contact{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", nickname='" + nickname + '\'' +
                ", address='" + address + '\'' +
                ", phone='" + phone + '\'' +
                ", email='" + email + '\'' +
                ", lookupKey='" + lookupKey + '\'' +
                '}';
    }
}
