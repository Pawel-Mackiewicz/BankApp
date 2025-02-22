#!/bin/bash

# Nowa wersja przekazywana jako pierwszy argument
VERSION=$1

# Aktualizacja wersji w linii COPY w Dockerfile, która zawiera nazwę jar
# Wyszukujemy wzorzec: "COPY --from=builder /app/target/BankApp-" 
# następnie sekwencję cyfr i kropek, potem ".jar app.jar"
# i zastępujemy znalezioną wersję nową wersją.
sed -i "s|\(COPY --from=builder /app/target/BankApp-\)[0-9.]\+\(\.jar app\.jar\)|\1${VERSION}\2|" Dockerfile

echo "Dockerfile updated to version ${VERSION}"