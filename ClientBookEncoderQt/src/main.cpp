#include "mainwindowclientbookencoder.h"
#include "socket.h"

#include <QApplication>
int main(int argc, char *argv[])
{
    int idClient = Socket();
    LireConfiguration();
    // connexion au serveur
    Connect(idClient, "127.0.0.1", PORT_ENCODING);

    QApplication a(argc, argv);
    MainWindowClientBookEncoder w(idClient);
    w.show();
    return a.exec();
}