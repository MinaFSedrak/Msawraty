package egypt.sedrak.insta1;

/**
 * Created by lenovov on 23-Feb-17.
 */
public class Photographer {

    private String name;
    private String profile_picture;



    public Photographer(){

    }

    public Photographer(String name , String profile_picture){
        this.name = name;
        this.profile_picture = profile_picture;
    }




    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProfile_picture() {
        return profile_picture;
    }

    public void setProfile_picture(String profile_picture) {
        this.profile_picture = profile_picture;
    }
}
