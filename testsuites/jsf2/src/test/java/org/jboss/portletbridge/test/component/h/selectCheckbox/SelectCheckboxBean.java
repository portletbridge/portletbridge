package org.jboss.portletbridge.test.component.h.selectCheckbox;

import java.util.Arrays;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;

@ManagedBean(name = "selCheckboxBean")
@SessionScoped
public class SelectCheckboxBean {
	
	public static final String[] colors = new String[]{"White", "Black", "Red", "Green", "Blue"};

	private boolean accepted = false;

	public boolean getAccepted() {
		return accepted;
	}
	
	public void setAccepted(boolean accepted) {
		this.accepted = accepted;
	}

	public String[] getColors() {
		return colors;
	}

    public void vGender(FacesContext context, UIComponent input, Object newValue) throws ValidatorException {
    	String[] values = (String[]) newValue;
    	if(values.length > 1) {
    		throw new ValidatorException(new FacesMessage("Only one gender can be selected."));
    	}
    }

    private String gender;
    private String[] favColors;
    
    public String[] getGender() {
		return new String[]{gender};
	}
    
    public void setGender(String[] gender) {
		if(gender.length != 1) {
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
    
    public String getSelectionString() {
    	String result = "";
    	result += accepted ? "Thanks for accepting our conditions. " : "Please accept our conditions. ";
    	if(gender != null) {
    		result += "You are a " + gender;
        	result += favColors != null && favColors.length > 0 ? " and your favorite colors are " + Arrays.toString(favColors) : " and you have no favorite colors.";
    	}
    	return result;
    }
}
