package $package;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import java.util.Map;
import java.util.HashMap;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.event.ActionEvent;
import java.io.Serializable;


@ManagedBean(name = "myBean")
@SessionScoped
public class MyBean implements Serializable {


    private static final long serialVersionUID = 8301865434469950945L;

    String str = "hello";


    public String getStr() {
        return str;
    }

    public void setStr(String str) {
        this.str = str;
    }

    @SuppressWarnings({"UnusedDeclaration"})
    public void reset(ActionEvent ae) {
        str = "";
    }

   private boolean showDebugFlag;
   private Map monthList;
   private String selectedMonth;
   public String firstName;

   public boolean isShowDebugFlag() {
      return showDebugFlag;
   }

   public void setShowDebugFlag(boolean showDebugFlag) {
      this.showDebugFlag = showDebugFlag;
   }

   public Map getMonthList() {
      if (monthList == null) {
         monthList = new HashMap();
         monthList.put("Mr.", "Mr.");
         monthList.put("Mrs.", "Mrs.");
         monthList.put("Miss", "Miss");
      }
      return monthList;
   }

   public void setMonthList(Map monthList) {
      this.monthList = monthList;
   }

   public String getSelectedMonth() {
      return selectedMonth;
   }

   public void setSelectedMonth(String selectedMonth) {
      this.selectedMonth = selectedMonth;
   }


   public String getFirstName() {
      return firstName;
   }

   public void setFirstName(String firstName) {
      this.firstName = firstName;
   }


   public String userSignup() {
      FacesContext context = FacesContext.getCurrentInstance();
      if (firstName == null || firstName.length() < 1) {
         context.addMessage(null,
                 new FacesMessage(FacesMessage.SEVERITY_ERROR, "first name cannot be empty", "first name cannot be empty"));
         return "signupError";
      }

      return "signupComplete";
   }

   public String test() {
      return null;
   }
}
