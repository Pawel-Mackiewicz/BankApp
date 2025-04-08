package info.mackiewicz.bankapp.user.model.interfaces;

import info.mackiewicz.bankapp.user.model.vo.EmailAddress;
import info.mackiewicz.bankapp.user.model.vo.PhoneNumber;

public interface PersonalInfo {
    String getFirstname();
    String getLastname();
    EmailAddress getEmail();
    PhoneNumber getPhoneNumber();
    String getUsername();
}
