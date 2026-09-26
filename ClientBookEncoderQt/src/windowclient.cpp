#include "../include/windowclient.h"

WindowClient::WindowClient() : connected(false) {}

WindowClient::~WindowClient() {}

void WindowClient::setLogin(const std::string& login) {
    this->login = login;
}

std::string WindowClient::getLogin() const {
    return login;
}

void WindowClient::setPassword(const std::string& password) {
    this->password = password;
}

std::string WindowClient::getPassword() const {
    return password;
}

void WindowClient::HashPassword() {
    // Implémentation de la fonction de hachage du mot de passe
}

void WindowClient::ComparePassword(const std::string& hashedPassword) const {
    // Implémentation de la comparaison du mot de passe haché
}

void WindowClient::setConnected(bool connected) {
    this->connected = connected;
}

bool WindowClient::isConnected() const {
    return connected;
}