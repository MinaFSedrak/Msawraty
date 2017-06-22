package egypt.sedrak.insta1;

import java.io.Serializable;

/**
 * Created by lenovov on 09-Mar-17.
 */

public class Package implements Serializable  {

    private String categorie;
    private String price;
    private String duration;
    private String numOfPhotographers;
    private String extraDescription;



    private String categorieLogo;


    public  Package(){

    }

    public String getCategorie() {
        return categorie;
    }

    public void setCategorie(String categorie) {
        this.categorie = categorie;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getNumOfPhotographers() {
        return numOfPhotographers;
    }

    public void setNumOfPhotographers(String numOfPhotographers) {
        this.numOfPhotographers = numOfPhotographers;
    }

    public String getExtraDescription() {
        return extraDescription;
    }

    public void setExtraDescription(String extraDescription) {
        this.extraDescription = extraDescription;
    }

    public String getCategorieLogo() {
        return categorieLogo;
    }

    public void setCategorieLogo(String categorieLogo) {
        this.categorieLogo = categorieLogo;
    }

}
