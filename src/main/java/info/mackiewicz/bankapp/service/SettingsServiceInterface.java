package info.mackiewicz.bankapp.service;

import info.mackiewicz.bankapp.dto.ChangePasswordRequest;
import info.mackiewicz.bankapp.dto.ChangeUsernameRequest;
import info.mackiewicz.bankapp.model.User;

public interface SettingsServiceInterface {
    User getUserSettings(Integer userId);
    boolean changePassword(User user, ChangePasswordRequest request);
    void changeUsername(User user, ChangeUsernameRequest request);
}