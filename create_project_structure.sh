#!/bin/bash

# Navegar a la carpeta base del código fuente Java
# Si no estás ya en la raíz del proyecto, ajusta la ruta o navega primero.
# Este script asume que te encuentras en la raíz de tu proyecto Spring Boot.
cd src/main/java

# Definir el paquete base para simplificar
BASE_PACKAGE="com/conjuntoresidencial/api"

# --- CREAR LA ESTRUCTURA DE PAQUETES ---

# CAPA: DOMINIO
mkdir -p "${BASE_PACKAGE}/domain/user/model"
mkdir -p "${BASE_PACKAGE}/domain/user/port/in"
mkdir -p "${BASE_PACKAGE}/domain/user/port/out"

mkdir -p "${BASE_PACKAGE}/domain/property/model"
mkdir -p "${BASE_PACKAGE}/domain/property/port/in"
mkdir -p "${BASE_PACKAGE}/domain/property/port/out"

mkdir -p "${BASE_PACKAGE}/domain/payment/model"
mkdir -p "${BASE_PACKAGE}/domain/payment/port/in"
mkdir -p "${BASE_PACKAGE}/domain/payment/port/out"

mkdir -p "${BASE_PACKAGE}/domain/vehicle/model"
mkdir -p "${BASE_PACKAGE}/domain/vehicle/port/in"
mkdir -p "${BASE_PACKAGE}/domain/vehicle/port/out"

mkdir -p "${BASE_PACKAGE}/domain/access_control/model"
mkdir -p "${BASE_PACKAGE}/domain/access_control/port/in"
mkdir -p "${BASE_PACKAGE}/domain/access_control/port/out"

mkdir -p "${BASE_PACKAGE}/domain/shared/exception"

# CAPA: APLICACIÓN
mkdir -p "${BASE_PACKAGE}/application/user/service"
mkdir -p "${BASE_PACKAGE}/application/property/service"
mkdir -p "${BASE_PACKAGE}/application/payment/service"
mkdir -p "${BASE_PACKAGE}/application/vehicle/service"
mkdir -p "${BASE_PACKAGE}/application/access_control/service"

# CAPA: INFRAESTRUCTURA
mkdir -p "${BASE_PACKAGE}/infrastructure/web/controller"
mkdir -p "${BASE_PACKAGE}/infrastructure/web/dto/request"
mkdir -p "${BASE_PACKAGE}/infrastructure/web/dto/response"
mkdir -p "${BASE_PACKAGE}/infrastructure/web/mapper"
mkdir -p "${BASE_PACKAGE}/infrastructure/web/exception"

mkdir -p "${BASE_PACKAGE}/infrastructure/persistence/postgresql/entity" # Directorio para entidades JPA si son separadas
mkdir -p "${BASE_PACKAGE}/infrastructure/persistence/postgresql/repository"
mkdir -p "${BASE_PACKAGE}/infrastructure/persistence/postgresql/adapter"

mkdir -p "${BASE_PACKAGE}/infrastructure/security/config"
mkdir -p "${BASE_PACKAGE}/infrastructure/security/jwt"
mkdir -p "${BASE_PACKAGE}/infrastructure/security/service"

mkdir -p "${BASE_PACKAGE}/infrastructure/config"

echo "Estructura de directorios creada."

# --- AÑADIR ARCHIVOS .gitkeep A LAS CARPETAS HOJA ---
# (Para que Git pueda rastrear los directorios vacíos)

# Domain Layer
touch "${BASE_PACKAGE}/domain/user/model/.gitkeep"
touch "${BASE_PACKAGE}/domain/user/port/in/.gitkeep"
touch "${BASE_PACKAGE}/domain/user/port/out/.gitkeep"
touch "${BASE_PACKAGE}/domain/property/model/.gitkeep"
touch "${BASE_PACKAGE}/domain/property/port/in/.gitkeep"
touch "${BASE_PACKAGE}/domain/property/port/out/.gitkeep"
touch "${BASE_PACKAGE}/domain/payment/model/.gitkeep"
touch "${BASE_PACKAGE}/domain/payment/port/in/.gitkeep"
touch "${BASE_PACKAGE}/domain/payment/port/out/.gitkeep"
touch "${BASE_PACKAGE}/domain/vehicle/model/.gitkeep"
touch "${BASE_PACKAGE}/domain/vehicle/port/in/.gitkeep"
touch "${BASE_PACKAGE}/domain/vehicle/port/out/.gitkeep"
touch "${BASE_PACKAGE}/domain/access_control/model/.gitkeep"
touch "${BASE_PACKAGE}/domain/access_control/port/in/.gitkeep"
touch "${BASE_PACKAGE}/domain/access_control/port/out/.gitkeep"
touch "${BASE_PACKAGE}/domain/shared/exception/.gitkeep"

# Application Layer
touch "${BASE_PACKAGE}/application/user/service/.gitkeep"
touch "${BASE_PACKAGE}/application/property/service/.gitkeep"
touch "${BASE_PACKAGE}/application/payment/service/.gitkeep"
touch "${BASE_PACKAGE}/application/vehicle/service/.gitkeep"
touch "${BASE_PACKAGE}/application/access_control/service/.gitkeep"

# Infrastructure Layer
touch "${BASE_PACKAGE}/infrastructure/web/controller/.gitkeep"
touch "${BASE_PACKAGE}/infrastructure/web/dto/request/.gitkeep"
touch "${BASE_PACKAGE}/infrastructure/web/dto/response/.gitkeep"
touch "${BASE_PACKAGE}/infrastructure/web/mapper/.gitkeep"
touch "${BASE_PACKAGE}/infrastructure/web/exception/.gitkeep"
touch "${BASE_PACKAGE}/infrastructure/persistence/postgresql/entity/.gitkeep"
touch "${BASE_PACKAGE}/infrastructure/persistence/postgresql/repository/.gitkeep"
touch "${BASE_PACKAGE}/infrastructure/persistence/postgresql/adapter/.gitkeep"
touch "${BASE_PACKAGE}/infrastructure/security/config/.gitkeep"
touch "${BASE_PACKAGE}/infrastructure/security/jwt/.gitkeep"
touch "${BASE_PACKAGE}/infrastructure/security/service/.gitkeep"
touch "${BASE_PACKAGE}/infrastructure/config/.gitkeep"

echo "Archivos .gitkeep añadidos a las carpetas hoja."

# Volver al directorio raíz del proyecto (opcional)
cd ../../..

echo "Script completado. Revisa la estructura en src/main/java/${BASE_PACKAGE}"