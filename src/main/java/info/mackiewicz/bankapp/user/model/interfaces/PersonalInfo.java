package info.mackiewicz.bankapp.user.model.interfaces;

import info.mackiewicz.bankapp.user.model.vo.Email;
import info.mackiewicz.bankapp.user.model.vo.PhoneNumber;

public interface PersonalInfo {
    String getFirstname();
    String getLastname();
    Email getEmail();
    PhoneNumber getPhoneNumber();
    String getUsername();
}
