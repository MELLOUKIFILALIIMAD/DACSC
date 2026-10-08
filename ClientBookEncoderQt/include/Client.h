#ifndef CLIENT_H
#define CLIENT_H

#include <string>
#include "protocole.h"

class Client
{
public:
    Client();
    ~Client();

    void setLogin(const std::string& login);
    std::string getLogin() const;

    void setPassword(const std::string& password);
    std::string getPassword() const;

    void Register(const std::string& login, const std::string& password);

    void HashPassword();
    void ComparePassword(const std::string& hashedPassword) const;

    void setConnected(bool connected);
    bool isConnected() const;

    std::string PrepareMessageQuery(const std::string &query);
    MESSAGE DecryptMessageQuery(const std::string &encryptedQuery);
    
    void setSocketServeur(int socket);
    int getSocketServeur();

private:
    std::string login;
    std::string password;
    bool connected;

    int socketServeur;
};

#endif // CLIENT_H