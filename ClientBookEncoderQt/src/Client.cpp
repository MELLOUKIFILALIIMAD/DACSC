#include "Client.h"

#include <cstring>
#include <string>
using namespace std;

Client::Client()
{
    this->login = "";
    this->password = "";
    this->connected = false;
    this->socketServeur = -1;
}

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
void Client::setSocketServeur(int socket)
{
    socketServeur = socket;
}
int Client::getSocketServeur()
{
    return socketServeur;
}

string Client::PrepareMessageQuery(const string &query) {
    string tailleMessage = std::to_string(query.length());

    if (tailleMessage == "0") {
        return "Erreur: Message vide !";
    }

    while (tailleMessage.length() < 4) {
        tailleMessage = "0" + tailleMessage;
    }

    return tailleMessage + query;
}

MESSAGE Client::DecryptMessageQuery(const string &encryptedQuery) {
    MESSAGE msg;

    if (encryptedQuery.length() < 4) {
        return msg;
    }

    string tailleMessageStr = encryptedQuery.substr(0, 4);
    int tailleMessage = std::stoi(tailleMessageStr);

    if (tailleMessage <= 0 || tailleMessage > static_cast<int>(encryptedQuery.length() - 4)) {
        return msg;
    }

    msg.type = socketServeur;
    msg.expediteur = -1;
    msg.requete = -1;
    msg.data1 = (char*)tailleMessageStr.c_str();
    msg.data2 = NULL;
    msg.texte = (char*)encryptedQuery.substr(4, tailleMessage).c_str();

    return msg;
}