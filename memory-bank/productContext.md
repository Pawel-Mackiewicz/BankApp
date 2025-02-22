# Kontekst Produktu - BankApp

## Przegląd Projektu
BankApp to aplikacja bankowa zbudowana w Java Spring Boot, oferująca podstawowe funkcjonalności bankowe poprzez interfejs webowy i API REST.

## Główne Komponenty
- **Kontrolery**: Obsługa żądań HTTP (web i REST API)
- **Serwisy**: Logika biznesowa
- **Modele**: Encje bazodanowe i obiekty domenowe
- **Repozytoria**: Dostęp do bazy danych
- **DTOs**: Obiekty transferu danych
- **Walidatory**: Sprawdzanie poprawności danych

## Kluczowe Funkcjonalności

### System Transakcji
1. Typy Przelewów:
   - Przelewy między własnymi kontami
   - Przelewy wewnętrzne (w ramach banku)
   - Przelewy zewnętrzne
   
2. Metody Identyfikacji:
   - IBAN (wszystkie typy)
   - Email (przelewy wewnętrzne)
   - Przygotowanie pod @BankTag

3. Walidacja:
   - Walidacja IBAN
   - Walidacja email
   - Sprawdzanie dostępnych środków
   - Weryfikacja uprawnień

### Interfejs Użytkownika
- System zakładek dla różnych typów przelewów
- Dynamiczna walidacja formularzy
- Wyświetlanie IBAN i salda
- Kopiowanie IBAN do schowka

### Pozostałe Funkcje
- Zarządzanie kontami użytkowników
- Historia transakcji
- Ustawienia użytkownika
- Eksport danych (CSV, PDF)

## Pliki Memory Bank
- **activeContext.md**: Bieżący kontekst sesji i aktywne zadania
- **decisionLog.md**: Rejestr decyzji architektonicznych
- **progress.md**: Postęp prac i następne kroki
- **systemPatterns.md**: Wzorce projektowe i standardy
- **productContext.md** (ten plik): Ogólny kontekst projektu

## Ograniczenia Techniczne
- Java Spring Boot
- Thymeleaf (szablony HTML)
- Spring Security
- JPA/Hibernate
- RESTful API
- Bootstrap dla UI
- JavaScript dla walidacji frontend

## Cele Projektu
- Bezpieczne zarządzanie transakcjami
- Przyjazny interfejs użytkownika
- Skalowalność i wydajność
- Zgodność z najlepszymi praktykami branżowymi

## Standardy Implementacyjne
1. Frontend:
   - Natychmiastowa walidacja
   - Responsywny design
   - Spójny wygląd komponentów
   - Obsługa błędów w UI

2. Backend:
   - Walidacja wielopoziomowa
   - Transakcyjność operacji
   - Logowanie zdarzeń
   - Zabezpieczenia CSRF

3. API:
   - RESTful endpoints
   - Walidacja requestów
   - Standardowe formaty odpowiedzi
   - Obsługa błędów HTTP

## Planowane Rozszerzenia
- Implementacja @BankTag
- Szablony przelewów
- Przelewy cykliczne
- Powiadomienia o transakcjach