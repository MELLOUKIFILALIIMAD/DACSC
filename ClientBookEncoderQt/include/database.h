#ifndef DATABASE_H
#define DATABASE_H

#include <string>

void ConnexionBD();
void DeconnexionBD();
void ReinitialiserConnexions();

int VerifierLogin(const char *login, const char *password);
int LoginExiste(const char *login);

int LoggedIn(const char *login);
int LoggedOut(const char *login);
int isLoggedin(const char *login);

int AddAuthors(const char *lastname, const char *firstname, const char *date);
int AddSubjects(const char *name);
int AddBook(const char* authorname, const char* subjecttitle, const char *title, const char *isbn, int pageCount, int stockQuantity, float price, int publishYear);
// Retournent 1 si OK, 0 si erreur. Les listes sont de la forme "1;2;3;"
int GetAuthors(std::string &ids, std::string &lastnames, std::string &firstnames);
int GetSubjects(std::string &ids, std::string &names);

int AjouterEmploye(const char *login, const char *password);
#endif