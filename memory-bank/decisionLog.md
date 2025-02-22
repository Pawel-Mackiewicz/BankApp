# Decision Log

## [2024-02-22] - System Przelewów Bankowych

### Lokalizacja Implementacji
Implementacja będzie zintegrowana w istniejącym formularzu "Make a Transfer" na stronie dashboard (transfer-form div, linia 412).

### Szczegóły implementacji UI

#### 1. System Zakładek
- Zakładki będą dodane w górnej części formularza transferowego
- Trzy typy zakładek:
  1. Własne konta
  2. Przelew w banku
  3. Przelew zewnętrzny

#### 2. Formularze dla Każdego Typu
1. Przelew między własnymi kontami
   ```html
   - Select konta źródłowego z wyświetleniem:
     - Numeru konta
     - IBAN
     - Dostępnego salda
   - Select konta docelowego (bez wybranego konta źródłowego)
     - Numer konta
     - IBAN
     - Dostępne saldo
   - Kwota
   - Tytuł przelewu
   ```

2. Przelew w ramach banku
   ```html
   - Select konta źródłowego z:
     - Numerem konta
     - IBAN
     - Dostępnym saldem
   - Wybór metody identyfikacji odbiorcy:
     - IBAN (walidacja przez IbanValidator)
     - Email (walidacja przez AccountRepository)
   - Kwota
   - Tytuł przelewu
   ```

3. Przelew zewnętrzny
   ```html
   - Select konta źródłowego z:
     - Numerem konta
     - IBAN
     - Dostępnym saldem
   - Pole IBAN odbiorcy (walidacja przez IbanValidator)
   - Nazwa odbiorcy
   - Kwota
   - Tytuł przelewu
   ```

### Walidacja
1. Frontend (JavaScript):
   - Natychmiastowa walidacja formularzy
   - Dynamiczne ukrywanie wybranego konta źródłowego z listy kont docelowych
   - Walidacja IBAN w czasie rzeczywistym
   - Walidacja dostępnych środków
   - Wyświetlanie błędów inline w formularzu

2. Backend:
   - IbanValidator dla walidacji numerów IBAN
   - AccountRepository do wyszukiwania kont po emailu
   - Weryfikacja dostępnych środków
   - Walidacja uprawnień do kont

### Integracja z Backendem
1. Modyfikacja kontrolera:
   - Nowe endpointy dla różnych typów przelewów
   - Obsługa walidacji
   - Zwracanie odpowiednich komunikatów błędów

2. Serwisy:
   - Rozszerzenie TransactionService o obsługę różnych typów przelewów
   - Integracja z IbanValidator
   - Wykorzystanie AccountRepository do wyszukiwania po emailu

### Modyfikacje DTO
Wykorzystanie istniejących klas:
- OwnAccountTransferRequest
- InternalAccountTransferRequest
- ExternalAccountTransferRequest

### Plan Wdrożenia
1. Modyfikacja HTML/CSS:
   - Dodanie systemu zakładek
   - Utworzenie formularzy dla każdego typu przelewu
   - Stylizacja zgodna z istniejącym designem

2. Implementacja JavaScript:
   - Obsługa przełączania zakładek
   - Dynamiczna walidacja
   - Aktualizacja list kont
   - Obsługa błędów

3. Backend:
   - Rozszerzenie kontrolerów
   - Implementacja walidacji
   - Integracja z istniejącymi serwisami

4. Testy:
   - Testy jednostkowe nowej funkcjonalności
   - Testy integracyjne
   - Testy UI/UX

### Następne kroki
1. Rozpoczęcie implementacji HTML/CSS
2. Implementacja logiki JavaScript
3. Rozszerzenie backendu
4. Testy i walidacja

## [2024-02-22] - System Przelewów Bankowych - Status Implementacji

### Zrealizowane Funkcjonalności
1. Frontend:
   - System zakładek w "Make Transfer"
   - Dynamiczna walidacja IBAN i email
   - Poprawiona kolejność ładowania skryptów
   - Wyświetlanie IBAN i salda przy kontach

2. Backend:
   - Zaktualizowano AccountRepository:
     * findByIban
     * findFirstByOwner_email
   - Rozszerzono ValidationController
   - Usunięto stary TransferForm
   - Dodano dedykowane DTO

3. Walidacja:
   - IBAN przez IbanValidator
   - Email przez AccountRepository
   - Saldo konta przed przelewem
   - Natychmiastowa walidacja w UI

### Rezultaty Implementacji
1. Interfejs:
   - Intuicyjny system zakładek
   - Lepsze UX przy wyborze kont
   - Natychmiastowy feedback

2. Kod:
   - Większa modularność
   - Lepsza separacja odpowiedzialności
   - Rozszerzona walidacja

3. Bezpieczeństwo:
   - Dwustronna walidacja
   - Weryfikacja uprawnień
   - Kontrola dostępnych środków

### Kolejne Kroki
1. Testy integracyjne
2. Monitoring walidacji
3. Zbieranie feedbacku
4. Przygotowanie do @BankTag