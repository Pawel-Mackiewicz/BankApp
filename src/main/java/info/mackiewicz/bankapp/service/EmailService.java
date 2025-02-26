package info.mackiewicz.bankapp.service;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class EmailService {


    public void sendEmail(String email, String subject, String content) {
        //TODO: implement email sending
    }

    public void sendPasswordResetEmail(String email, String token) {
        //TODO: implement email sending with password reset token;
        String subject = "Password Reset Request";
        String content = "To reset your password, click the link: " + token;
        sendEmail(email, subject, content);
    }

    public void sendPasswordResetConfirmation(String email) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'sendPasswordResetConfirmation'");
    }
}
