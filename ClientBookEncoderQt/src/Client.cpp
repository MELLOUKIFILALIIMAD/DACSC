#include "Client.h"

Client::Client() : connected(false) {}

Client::~Client() {}

// Avant de commencer, le setLogin et setPassword sont là uniquement pour stocker les informations de connexion. Le reste est géré côté serveur.
// Récupérer le login et le mot de passe saisis par l'utilisateur, puis les stocker dans les variables membres de la classe Client.
// Nous permet de faciliter l'accès 

void Client::setLogin(const std::string& login) {
    this->login = login;
}

std::string Client::getLogin() const {
    return login;
}

void Client::setPassword(const std::string& password) {
    this->password = password;
}

std::string Client::getPassword() const {
    return password;
}

void Client::HashPassword() {
    // Pas pour l'étape 1
}

void Client::ComparePassword(const std::string& hashedPassword) const {
    // Pas pour l'étape 1
}

void Client::setConnected(bool connected) {
    this->connected = connected;
}

bool Client::isConnected() const {
    return connected;
}