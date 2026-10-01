#ifndef DATABASE_H
#define DATABASE_H

void ConnexionBD();
void DeconnexionBD();

int VerifierLogin(const char *login, const char *password);
int LoginExiste(const char *login);
int AjouterEmploye(const char *login, const char *password);
#endif