package bgu.spl181.net.srv.bidi;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MovieUser {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("name")
    @Expose
    private String name;


    public MovieUser(String id, String name) {
        this.id = id;
        this.name = name;
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
}
