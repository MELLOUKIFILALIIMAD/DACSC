#ifndef CLIENT_H
#define CLIENT_H

#include <string>

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
    
    void setSocketServeur(int socket);
    int getSocketServeur();

private:
    std::string login;
    std::string password;
    bool connected;

    int socketServeur;
};

#endif // CLIENT_H