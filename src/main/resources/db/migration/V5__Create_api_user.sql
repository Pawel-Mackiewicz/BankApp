-- Add an API user for Swagger UI access
INSERT IGNORE INTO admins (credentials_expired, enabled, expired, locked, password, username)
VALUES (false, true, false, false, '$2a$12$aNaLg9djXhl.lndvoCRT9OktCz1.ED6BH6plSS/u0I01L//hyYAX6', 'api');

INSERT IGNORE INTO admin_roles (admin_id, role) 
VALUES (LAST_INSERT_ID(), 'ROLE_SWAGGER');