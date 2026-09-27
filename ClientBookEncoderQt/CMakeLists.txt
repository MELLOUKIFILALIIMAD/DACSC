cmake_minimum_required(VERSION 3.16)

project(DACSC LANGUAGES C CXX)

set(CMAKE_CXX_STANDARD 17)
set(CMAKE_CXX_STANDARD_REQUIRED ON)

set(CMAKE_EXPORT_COMPILE_COMMANDS ON)

# =========================
# QT
# =========================

set(CMAKE_AUTOMOC ON)
set(CMAKE_AUTOUIC OFF)
set(CMAKE_AUTORCC OFF)

find_package(Qt5 REQUIRED COMPONENTS Widgets)

# =========================
# CLIENT
# =========================

add_executable(ClientBookEncoder
    src/main.cpp
    src/mainwindowclientbookencoder.cpp
    src/Client.cpp
    src/socket.cpp

    include/mainwindowclientbookencoder.h
    include/ui_mainwindowclientbookencoder.h
    include/Client.h
    include/socket.h
)

target_include_directories(ClientBookEncoder PRIVATE
    ${CMAKE_CURRENT_SOURCE_DIR}/include
)

target_link_libraries(ClientBookEncoder PRIVATE
    Qt5::Widgets
)

# =========================
# SERVEUR
# =========================

add_executable(Serveur
    src/Serveur.cpp
    src/socket.cpp

    include/socket.h
)

target_include_directories(Serveur PRIVATE
    ${CMAKE_CURRENT_SOURCE_DIR}/include
)
