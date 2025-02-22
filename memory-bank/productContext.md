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
- Zarządzanie kontami użytkowników
- Obsługa transakcji (przelewy, wpłaty, wypłaty)
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

## Cele Projektu
- Bezpieczne zarządzanie transakcjami
- Przyjazny interfejs użytkownika
- Skalowalność i wydajność
- Zgodność z najlepszymi praktykami branżowymi