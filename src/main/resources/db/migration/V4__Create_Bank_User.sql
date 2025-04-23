SET FOREIGN_KEY_CHECKS = 0;

INSERT INTO `users` (
    `id`,
    `credentials_expired`,
    `enabled`,
    `expired`,
    `locked`,
    `password`,
    `username`,
    `account_counter`,
    `date_of_birth`,
    `email`,
    `firstname`,
    `lastname`,
    `pesel`,
    `phone_number`
) VALUES (
    -1,
    b'0', -- not credentials_expired
    b'1', -- enabled
    b'0', -- not expired
    b'0', -- not locked
    '${bank_password}', -- password in .env
    'bankapp',
    0,
    '2025-02-08',
    'bankapp@mackiewicz.info',
    'Bank',
    'App',
    '00000000000',
    '000000000'
)
ON DUPLICATE KEY UPDATE id = id;

SET FOREIGN_KEY_CHECKS = 1;