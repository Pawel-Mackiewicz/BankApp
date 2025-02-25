# Decision Log

## [2024-02-24] - Przyspieszony Plan Implementacji Systemu Resetowania Hasła

### Kontekst
Potrzeba przyspieszenia implementacji systemu resetowania hasła z pierwotnego planu 4-tygodniowego do 2 dni, zachowując kluczowe aspekty bezpieczeństwa i funkcjonalności.

### Główne Decyzje

#### 1. Ograniczenie Zakresu
**Decyzja**: Skupienie się na MVP (Minimum Viable Product)

**Uzasadnienie**:
- Czas implementacji ograniczony do 2 dni
- Konieczność priorytetyzacji kluczowych funkcjonalności
- Możliwość późniejszego rozszerzenia systemu

**Konsekwencje**:
- Szybsze wdrożenie podstawowej funkcjonalności
- Mniejsza złożoność początkowego rozwiązania
- Łatwiejsze testowanie i deployment

#### 2. Architektura API
**Decyzja**: Ograniczenie do 2 głównych endpointów:
- `/api/password/reset-request`
- `/api/password/reset-complete`

**Uzasadnienie**:
- Minimalna niezbędna funkcjonalność
- Uproszczenie logiki biznesowej
- Zmniejszenie powierzchni ataku

**Implementacja**:
```java
@RestController
@RequestMapping("/api/password")
public class PasswordResetController {
    @PostMapping("/reset-request")     // Generacja i wysyłka tokenu
    @PostMapping("/reset-complete")    // Walidacja tokenu i zmiana hasła
}
```

#### 3. Bezpieczeństwo
**Decyzja**: Implementacja podstawowego ale solidnego systemu zabezpieczeń

**Kluczowe Elementy**:
1. Rate Limiting:
   - 3 próby/godzinę per email
   - 5 prób/godzinę per IP

2. Walidacja:
   - Sprawdzanie tokenu JWT
   - Walidacja emaila
   - Wymagania dla hasła

3. Monitoring:
   - Logowanie prób resetowania
   - Podstawowe alerty bezpieczeństwa

**Uzasadnienie**:
- Zachowanie kluczowych aspektów bezpieczeństwa
- Możliwość szybkiego wdrożenia
- Łatwe rozszerzenie w przyszłości

#### 4. Frontend
**Decyzja**: Minimalistyczny ale funkcjonalny interfejs

**Implementacja**:
1. Formularze:
   - reset-password.html
   - new-password.html

2. Walidacja:
   - Podstawowa walidacja JS
   - Komunikaty błędów
   - Spójny wygląd z istniejącym UI

**Uzasadnienie**:
- Szybka implementacja
- Zachowanie spójności z istniejącym interfejsem
- Dobry UX mimo prostoty

#### 5. Email
**Decyzja**: Prosta integracja z resend.com

**Szczegóły**:
- Podstawowy szablon
- Retry mechanizm (max 3 próby)
- Logowanie statusów wysyłki

**Uzasadnienie**:
- Szybka implementacja
- Niezawodność wysyłki
- Możliwość późniejszego rozszerzenia

### Plan Wdrożenia

#### Dzień 1
1. Rano (4h):
   - Implementacja kontrolera
   - Podstawowa integracja email

2. Popołudnie (4h):
   - Formularze HTML
   - Logika JavaScript

#### Dzień 2
1. Rano (4h):
   - Rate limiting
   - System monitorowania

2. Popołudnie (4h):
   - Testy krytyczne
   - Deployment

### Metryki Sukcesu
1. Funkcjonalne:
   - Działający proces resetowania hasła
   - Poprawna wysyłka emaili
   - Skuteczna zmiana hasła

2. Techniczne:
   - Podstawowe testy przechodzą
   - Rate limiting działa
   - Monitoring aktywny

3. Bezpieczeństwo:
   - Brak możliwości obejścia zabezpieczeń
   - Skuteczna walidacja tokenów
   - Podstawowe logowanie zdarzeń

### Ryzyka i Mitygacja
1. Bezpieczeństwo:
   - Ryzyko: Ograniczony czas na testy bezpieczeństwa
   - Mitygacja: Skupienie się na kluczowych zabezpieczeniach

2. Wydajność:
   - Ryzyko: Brak czasu na optymalizację
   - Mitygacja: Monitorowanie podstawowych metryk

3. UX:
   - Ryzyko: Uproszczony interfejs
   - Mitygacja: Zachowanie kluczowych elementów UX

### Przyszłe Rozszerzenia
1. Bezpieczeństwo:
   - Zaawansowany monitoring
   - Dodatkowe zabezpieczenia
   - Rozszerzone metryki

2. UX:
   - Rozbudowane szablony email
   - Bardziej zaawansowany interfejs
   - Dodatkowe funkcje użytkownika

3. Monitoring:
   - Zaawansowane statystyki
   - Automatyczne alerty
   - Dashboard bezpieczeństwa

## [2025-02-25] - Konfiguracja Deploymentu na Heroku

### Kontekst
Aplikacja Spring Boot wymaga skonfigurowania automatycznego deployu na platformie Heroku. Baza danych jest już skonfigurowana na zewnętrznym serwisie.

### Główne Decyzje

#### 1. Konfiguracja Systemu
**Decyzja**: Minimalna konfiguracja niezbędna do uruchomienia na Heroku

**Wymagane Pliki**:
- `system.properties`: Określenie wersji Javy (21)
- `Procfile`: Definicja sposobu uruchamiania aplikacji

**Uzasadnienie**:
- Heroku wymaga jawnego określenia wersji Javy
- Procfile zapewnia prawidłowe uruchomienie aplikacji

#### 2. Proces Deploymentu
**Decyzja**: Wykorzystanie GitHub Actions i Heroku CLI

**Kroki Wdrożenia**:
1. Utworzenie aplikacji na Heroku
2. Połączenie z repozytorium GitHub
3. Konfiguracja zmiennych środowiskowych
4. Uruchomienie automatycznego deploymentu

**Uzasadnienie**:
- Automatyzacja procesu deploymentu
- Integracja z istniejącym flow CI/CD
- Łatwe zarządzanie wersjami

### Plan Implementacji

#### Krok 1: Konfiguracja Podstawowa
```bash
# Logowanie do Heroku
heroku login

# Tworzenie aplikacji
heroku create bankapp-prod

# Konfiguracja środowiska
heroku config:set SPRING_PROFILES_ACTIVE=prod
```

#### Krok 2: Continuous Deployment
1. Połączenie repozytorium GitHub z Heroku przez Dashboard
2. Konfiguracja automatycznego deploymentu dla brancha main
3. Konfiguracja review apps (opcjonalnie)

### Metryki Sukcesu
1. Aplikacja uruchamia się poprawnie na Heroku
2. Automatyczny deployment działa po push na main
3. Aplikacja poprawnie łączy się z zewnętrzną bazą danych
4. Logi pokazują prawidłowe działanie aplikacji

### Monitoring i Utrzymanie
1. Regularne sprawdzanie logów Heroku
2. Monitoring zużycia zasobów
3. Backup konfiguracji