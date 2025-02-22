# Aktualny Kontekst Projektu

## Implementacja Systemu Przelewów

### Bieżący Status
Zaimplementowano system zakładek i różne typy przelewów w formularzu "Make a Transfer".

### Zrealizowane Elementy
1. Frontend:
   - System zakładek w sekcji "Make a Transfer"
   - Dynamiczna walidacja IBAN i email
   - Poprawiona kolejność ładowania skryptów JavaScript
   - Wyświetlanie IBAN i salda przy kontach
   - Obsługa różnych typów formularzy

2. Backend:
   - Zaktualizowano AccountRepository o metody:
     * findByIban
     * findFirstByOwner_email
   - Rozszerzono ValidationController o lepszą walidację emaila
   - Usunięto stary TransferForm
   - Dodano dedykowane DTO dla każdego typu przelewu

3. Walidacja:
   - IBAN przez IbanValidator
   - Email przez AccountRepository (sprawdzanie zarówno emaila jak i username)
   - Saldo konta przed wykonaniem przelewu
   - Natychmiastowa walidacja w formularzu

### Typy Przelewów
1. Przelew między własnymi kontami:
   - Dynamiczna lista kont z wyłączeniem wybranego
   - Wyświetlanie IBAN i salda dla obu kont

2. Przelew wewnętrzny:
   - Walidacja IBAN lub email odbiorcy
   - Obsługa obu metod identyfikacji odbiorcy

3. Przelew zewnętrzny:
   - Walidacja IBAN
   - Implementacja jako withdrawal

### Aktualne Funkcjonalności
- Przełączanie między typami przelewów
- Dynamiczna walidacja wszystkich pól
- Natychmiastowe komunikaty o błędach
- Automatyczna aktualizacja list kont
- Zabezpieczenia przed nieprawidłowymi danymi

### Następne Kroki
1. Testy integracyjne nowych funkcjonalności
2. Monitoring działania walidacji
3. Zbieranie feedbacku od użytkowników
4. Potencjalne rozszerzenie o @BankTag w przyszłości

### Uwagi Techniczne
- Skrypty JavaScript załadowane w prawidłowej kolejności
- Wykorzystanie Bootstrap dla komponentów UI
- Zaimplementowana obsługa błędów na froncie i backendzie
- Dodana walidacja cross-field dla przelewów własnych