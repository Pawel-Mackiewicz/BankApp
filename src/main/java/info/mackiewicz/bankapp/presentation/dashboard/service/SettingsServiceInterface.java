package info.mackiewicz.bankapp.presentation.dashboard.service;

import info.mackiewicz.bankapp.presentation.dashboard.dto.ChangePasswordRequest;
import info.mackiewicz.bankapp.presentation.dashboard.dto.ChangeUsernameRequest;
import info.mackiewicz.bankapp.user.model.User;

public interface SettingsServiceInterface {
    User getUserSettings(Integer userId);
    boolean changePassword(User user, ChangePasswordRequest request);
    void changeUsername(User user, ChangeUsernameRequest request);
}