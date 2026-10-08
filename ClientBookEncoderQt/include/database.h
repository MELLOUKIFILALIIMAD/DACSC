#ifndef DATABASE_H
#define DATABASE_H

#include "protocole.h"

void ConnexionBD();
void DeconnexionBD();

int VerifierLogin(const char *login, const char *password);
int LoginExiste(const char *login);

int LoggedIn(const char *login);
int LoggedOut(const char *login);
int isLoggedin(const char *login);

MESSAGE GetAuthors(int client);
MESSAGE GetSubjects(int client);

int AjouterEmploye(const char *login, const char *password);
#endif