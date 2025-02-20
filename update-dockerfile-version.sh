#!/bin/bash

# Skrypt do aktualizacji wersji w Dockerfile
VERSION=$1

# Aktualizacja wersji w Dockerfile
sed -i "s/^ARG VERSION=.*/ARG VERSION=${VERSION}/" Dockerfile

echo "Dockerfile updated to version ${VERSION}"