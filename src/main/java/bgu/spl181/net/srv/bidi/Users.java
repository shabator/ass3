
package bgu.spl181.net.srv.bidi;

import bgu.spl181.net.srv.bidi.User;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Users {

    @SerializedName("users")
    @Expose
    private List<User> users = null;


    /**
     *
     * @param users
     */
    public Users(List<User> users) {
        super();
        this.users = users;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

}