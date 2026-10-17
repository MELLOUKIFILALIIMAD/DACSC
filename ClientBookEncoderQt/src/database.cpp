#include <stdio.h>
#include <stdlib.h>
#include <signal.h>
#include <pthread.h>
#include <string>
#include <cstring>
#include <mysql.h>
#include "database.h"

MYSQL *connexion;

void ConnexionBD()
{
    connexion = mysql_init(NULL);

    if (mysql_real_connect(connexion,"localhost","Student","PassStudent1_","PourStudent",0, 0, 0) == NULL)
    {
        fprintf(stderr, "%s\n", mysql_error(connexion));
        exit(1);
    }
}

void DeconnexionBD()
{
    mysql_close(connexion);
}

int VerifierLogin(const char *login, const char *password)
{
    char requete[256];
    printf("(SERVEUR) Login reçu : '%s'\n", login);
    printf("(SERVEUR) Mot de passe reçu : '%s'\n", password);

    sprintf(requete,"SELECT id FROM employees WHERE login='%s' AND password='%s';",login, password);

    if (mysql_query(connexion, requete))
    {
        return 0;
    }

    MYSQL_RES *resultat = mysql_store_result(connexion);

    if (mysql_num_rows(resultat) > 0)
    {
        mysql_free_result(resultat);
        return 1;
    }

    mysql_free_result(resultat);

    return 0;
}

int isLoggedin(const char *login)
{
    char requete[256];

    sprintf(requete,"SELECT Active FROM employees WHERE login='%s';",login);

    if (mysql_query(connexion, requete))
    {
        return 0;
    }

    MYSQL_RES *resultat = mysql_store_result(connexion);

    if (mysql_num_rows(resultat) > 0)
    {
        MYSQL_ROW row = mysql_fetch_row(resultat);
        int active = atoi(row[0]);
        mysql_free_result(resultat);
        return active;
    }

    mysql_free_result(resultat);

    return 0;
}

int LoginExiste(const char *login)
{
    char requete[256];

    sprintf(requete,"SELECT id FROM employees WHERE login='%s';",login);

    if (mysql_query(connexion, requete))
    {
        return 0;
    }

    MYSQL_RES *resultat = mysql_store_result(connexion);

    if (mysql_num_rows(resultat) > 0)
    {
        mysql_free_result(resultat);
        return 1;
    }

    mysql_free_result(resultat);

    return 0;
}

int LoggedIn(const char *login)
{
    char requete[256];

    // Updating Active to 1 if the user is logged in
    sprintf(requete,"UPDATE employees SET Active = 1 WHERE login='%s';",login);

    if (mysql_query(connexion, requete))
    {
        return 0;
    }

    return 1;
}
void ReinitialiserConnexions()
{
    char requete[] = "UPDATE employees SET Active = 0";

    if (mysql_query(connexion, requete))
    {
        fprintf(stderr, "(SERVEUR) Erreur de réinitialisation : %s\n",
                mysql_error(connexion));
    }
}
int LoggedOut(const char *login)
{
    char requete[256];

    // Updating Active to 0 if the user is logged out
    sprintf(requete,"UPDATE employees SET Active = 0 WHERE login='%s';",login);

    if (mysql_query(connexion, requete))
    {
        return 0;
    }

    printf("(SERVEUR) Utilisateur déconnecté : %s\n", login);

    return 1;
}
int AddAuthors(const char* lastname, const char* firstname, const char * date)
{
    char requete[256];

    sprintf(requete,"INSERT INTO authors (last_name, first_name, birth_date) VALUES ('%s','%s', '%s');", lastname, firstname, date);

    if (mysql_query(connexion, requete))
    {
        fprintf(stderr, "%s\n", mysql_error(connexion));
        return 0;
    }

    printf("(SERVEUR) Auteur ajoute : %s %s\n", lastname, firstname);

    return 1;

}
int AddSubjects(const char *name)
{
    char requete[256];

    sprintf(requete,"INSERT INTO subjects (name) VALUES ('%s');",name);

    if (mysql_query(connexion, requete))
    {
        fprintf(stderr, "%s\n", mysql_error(connexion));
        return 0;
    }

    printf("(SERVEUR) Sujet ajoute : %s\n", name);

    return 1;
}

int AddBook(const char* authorname, const char* subjecttitle, const char *title, const char *isbn, int pageCount, int stockQuantity, float price, int publishYear)
{
    size_t taille = strlen(title) + strlen(authorname) + strlen(subjecttitle) + strlen(isbn) + 512;
    char* requete = (char*)malloc(taille);

    if (requete == NULL)
        return 0;

    sprintf(requete,"SELECT id FROM authors WHERE CONCAT(last_name, ' ', first_name)='%s';", authorname);
    if (mysql_query(connexion, requete))
    {
        fprintf(stderr, "%s\n", mysql_error(connexion));
        return 0;
    }

    MYSQL_RES *resultat = mysql_store_result(connexion);
    if (mysql_num_rows(resultat) == 0)
    {
        fprintf(stderr, "(SERVEUR) Auteur non trouvé : %s\n", authorname);
        mysql_free_result(resultat);
        return 0;
    }

    MYSQL_ROW row = mysql_fetch_row(resultat);
    int author_id = atoi(row[0]);
    mysql_free_result(resultat);

    sprintf(requete,"SELECT id FROM subjects WHERE name='%s';", subjecttitle);
    if (mysql_query(connexion, requete))
    {
        fprintf(stderr, "%s\n", mysql_error(connexion));
        return 0;
    }
    resultat = mysql_store_result(connexion);
    if (mysql_num_rows(resultat) == 0)
    {
        fprintf(stderr, "(SERVEUR) Sujet non trouvé : %s\n", subjecttitle);
        mysql_free_result(resultat);
        return 0;
    }
    row = mysql_fetch_row(resultat);
    int subject_id = atoi(row[0]);

    sprintf(requete,"INSERT INTO books (author_id, subject_id, title, isbn, page_count, stock_quantity, price, publish_year) VALUES (%d, %d, '%s', '%s', %d, %d, %f, %d);", author_id, subject_id, title, isbn, pageCount, stockQuantity, price, publishYear);
    if (mysql_query(connexion, requete))
    {
        fprintf(stderr, "%s\n", mysql_error(connexion));
        mysql_free_result(resultat);
        return 0;
    }

    free(requete);
    printf("(SERVEUR) Livre ajoute : %s\n", title);

    mysql_free_result(resultat);
    return 1;
}

int GetAuthors(std::string &ids, std::string &lastnames, std::string &firstnames)
{
    ids = "";
    lastnames = "";
    firstnames = "";

    if (mysql_query(connexion, "SELECT id, last_Name, first_Name FROM authors;"))
    {
        printf("(SERVEUR) Erreur lors de la récupération des auteurs : %s\n", mysql_error(connexion));
        return 0;
    }

    MYSQL_RES *resultat = mysql_store_result(connexion);

    if (resultat == NULL)
    {
        return 0;
    }

    MYSQL_ROW row;
    while ((row = mysql_fetch_row(resultat)))
    {
        ids        += std::string(row[0] ? row[0] : "") + ";";
        lastnames  += std::string(row[1] ? row[1] : "") + ";";
        firstnames += std::string(row[2] ? row[2] : "") + ";";
    }

    mysql_free_result(resultat);

    return 1;
}

int GetSubjects(std::string &ids, std::string &names)
{
    ids = "";   // exemple : 1;2;3;
    names = ""; // exemple : Roman;Science-fiction;Thriller;

    if (mysql_query(connexion, "SELECT id, name FROM subjects;"))
    {
        printf("(SERVEUR) Erreur lors de la récupération des sujets : %s\n", mysql_error(connexion));
        return 0;
    }

    MYSQL_RES *resultat = mysql_store_result(connexion);

    if (resultat == NULL)
    {
        return 0;
    }

    MYSQL_ROW row;
    while ((row = mysql_fetch_row(resultat)))
    {
        ids   += std::string(row[0] ? row[0] : "") + ";";
        names += std::string(row[1] ? row[1] : "") + ";";
    }

    mysql_free_result(resultat);

    return 1;
}

// Not used anywhere
int AjouterEmploye(const char *login, const char *password)
{
    char requete[256];

    sprintf(requete,"INSERT INTO employees (login, password) VALUES ('%s','%s');",login, password);

    if (mysql_query(connexion, requete))
    {
        fprintf(stderr, "%s\n", mysql_error(connexion));
        return 0;
    }

    printf("(SERVEUR) Employe ajoute : %s\n", login);

    return 1;
}