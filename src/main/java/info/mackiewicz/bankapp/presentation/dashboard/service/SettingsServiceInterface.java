package info.mackiewicz.bankapp.presentation.dashboard.service;

import info.mackiewicz.bankapp.presentation.dashboard.dto.ChangePasswordRequest;
import info.mackiewicz.bankapp.presentation.dashboard.dto.ChangeUsernameRequest;
import info.mackiewicz.bankapp.presentation.dashboard.dto.UserSettingsDTO;
import info.mackiewicz.bankapp.user.model.User;

public interface SettingsServiceInterface {
    UserSettingsDTO getUserSettings(Integer userId);
    boolean changePassword(User user, ChangePasswordRequest request);
    void changeUsername(User user, ChangeUsernameRequest request);
}