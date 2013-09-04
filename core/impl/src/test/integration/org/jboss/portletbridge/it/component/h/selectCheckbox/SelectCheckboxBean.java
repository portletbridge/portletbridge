package org.jboss.portletbridge.it.component.h.selectCheckbox;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

@ManagedBean(name = "selCheckboxBean")
@SessionScoped
public class SelectCheckboxBean {

    public static final String[] colors = new String[] { "White", "Black", "Red", "Green", "Blue" };
    public static final Map<String, Object> ages = new LinkedHashMap<String, Object>();

    static {
        ages.put("0-17", "young");
        ages.put("18-64", "adult");
        ages.put("65+", "senior");
    }

    private boolean accepted = false;

    private String gender;
    private String ageGroup;
    private String continent;
    private String[] favColors;

    public boolean getAccepted() {
        return accepted;
    }

    public void setAccepted(boolean accepted) {
        this.accepted = accepted;
    }

    public String[] getColors() {
        return colors;
    }

    public Map<String, Object> getAges() {
        return ages;
    }

    public void vGender(FacesContext context, UIComponent input, Object newValue) throws ValidatorException {
        String[] values = (String[]) newValue;
        if (values.length > 1) {
            throw new ValidatorException(new FacesMessage("Only one gender can be selected."));
        }
    }

    public String[] getGender() {
        return new String[] { gender };
    }

    public void setGender(String[] gender) {
        if (gender.length != 1) {
            throw new RuntimeException("More/Less than one gender is present.");
        }
        this.gender = gender[0];
    }

    public String[] getFavColors() {
        return favColors;
    }

    public void setFavColors(String[] favColors) {
        this.favColors = favColors;
    }

    public String getAgeGroup() {
        return ageGroup;
    }

    public void setAgeGroup(String ageGroup) {
        this.ageGroup = ageGroup;
    }

    public String getContinent() {
        return continent;
    }

    public void setContinent(String continent) {
        this.continent = continent;
    }

    public String getSelectionString() {
        String result = "";
        result += accepted ? "Thanks for accepting our conditions. " : "Please accept our conditions. ";
        if (gender != null) {
            result += "You are a " + (ageGroup != null ? ageGroup + " " : "") + gender + " from " + continent;
            result += favColors != null && favColors.length > 0 ? " and your favorite colors are " + Arrays.toString(favColors)
                    : " and you have no favorite colors.";
        }
        return result;
    }

    public void submit() {
    }
}
