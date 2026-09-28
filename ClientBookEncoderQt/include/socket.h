#ifndef SOCKET_H
#define SOCKET_H

#include "protocole.h"

#define PORT_ENCODING 5000

int Socket();
void Bind(int socket, int port);
void Listen(int socket);
int Accept(int socket);
void Connect(int socket, const char *ip, int port);

int Send(int socket, MESSAGE *message);
int Receive(int socket, MESSAGE *message);

void Close(int socket);

#endif