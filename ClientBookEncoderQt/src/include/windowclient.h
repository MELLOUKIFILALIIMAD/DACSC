#ifndef WINDOWCLIENT_H
#define WINDOWCLIENT_H

#include <string>

class WindowClient
{
public:
    WindowClient();
    ~WindowClient();

    void setLogin(const std::string& login);
    std::string getLogin() const;

    void setPassword(const std::string& password);
    std::string getPassword() const;

    void Register(const std::string& login, const std::string& password);

    void HashPassword();
    void ComparePassword(const std::string& hashedPassword) const;

    void setConnected(bool connected);
    bool isConnected() const;

private:
    std::string login;
    std::string password;
    bool connected;
}

#endif // WINDOWCLIENT_H