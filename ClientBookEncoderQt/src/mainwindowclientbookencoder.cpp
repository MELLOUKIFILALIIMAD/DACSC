#include "mainwindowclientbookencoder.h"
#include "ui_mainwindowclientbookencoder.h"
#include "Client.h"
#include "unistd.h"
#include <QInputDialog>
#include <QMessageBox>
#include <QDate>
#include <iostream>
#include <socket.h>
#include <string>
#include <sstream>
#include <cstring>
using namespace std;

///////////////////////////////////////////////////////////////////////////////////////////////////////////////
///// Outils pour le protocole texte (voir protocole.h) //////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////////////////////////
namespace {

int Dialogue(int socketServeur, const string &requete, char *reponse, char *champs[])
{
    if (Echange(socketServeur, requete.c_str(), reponse, TAILLE_MAX_TRAME + 1) <= 0)
    {
        return -1;
    }

    return Decouper(reponse, champs, MAX_CHAMPS);
}

// La réponse correspond-elle à la requête attendue, avec le statut "ok" ?
bool ReponseOk(int nb, char *champs[], const char *requete)
{
    return nb >= 2 && strcmp(champs[0], requete) == 0 && strcmp(champs[1], STATUT_OK) == 0;
}

// Champ numéro i de la réponse ("" s'il n'existe pas) : raison du ko, message du ok...
string Champ(int nb, char *champs[], int i)
{
    return (i < nb) ? string(champs[i]) : string("");
}

// '#' sert de séparateur dans le protocole : interdit dans les données saisies
bool SansSeparateur(const string &texte)
{
    return texte.find(SEPARATEUR_CHAMP) == string::npos;
}

}

MainWindowClientBookEncoder::MainWindowClientBookEncoder(int idClient, QWidget *parent)
    : QMainWindow(parent)
    , ui(new Ui::MainWindowClientBookEncoder)
    , idClient(idClient)
{
    ui->setupUi(this);
    ::close(2);
    client.setSocketServeur(idClient);
    //this->setFixedSize(1068, 301);

    // Configuration de la table des employes (Personnel Garage)
    ui->tableWidgetEncodedBooks->setColumnCount(9);
    ui->tableWidgetEncodedBooks->setRowCount(0);
    QStringList labelsTableEmployes;
    labelsTableEmployes << "Id" << "Titre" << "Auteur" << "Sujet" << "ISBN" << "Pages" << "Année" << "Prix" << "Stock";
    ui->tableWidgetEncodedBooks->setHorizontalHeaderLabels(labelsTableEmployes);
    ui->tableWidgetEncodedBooks->horizontalHeader()->setVisible(true);
    ui->tableWidgetEncodedBooks->horizontalHeader()->setStretchLastSection(true);
    ui->tableWidgetEncodedBooks->verticalHeader()->setVisible(false);
    ui->tableWidgetEncodedBooks->horizontalHeader()->setStyleSheet("background-color: lightyellow");
    int columnWidths[] = {35, 250, 200, 200, 150, 50, 50, 50, 40};
    for (int col = 0; col < 9; ++col)
        ui->tableWidgetEncodedBooks->setColumnWidth(col, columnWidths[col]);

    this->logoutOk();

    
    // Exemples d'utilisation (à supprimer)
    // this->addTupleTableBooks(1,"Les Thanatonautes","Bernard Werber","Science-Fiction","978-2253139225",505,1999,9.7f,3);
    // this->addTupleTableBooks(6,"Dune","Frank Herbert","Science-Fiction","978-2266320481",929,2021,11.95f,13);
    // this->addTupleTableBooks(13,"Le silence des agneaux","Thomas Harris","Thriller","978-2266208949",377,2015,7.7f,17);

    // this->addComboBoxAuthors("Bernard Werber");
    // this->addComboBoxAuthors("Dan Brown");

    // this->addComboBoxSubjects("Roman");
    // this->addComboBoxSubjects("Science-fiction");

}

MainWindowClientBookEncoder::~MainWindowClientBookEncoder() {
    delete ui;
}

///////////////////////////////////////////////////////////////////////////////////////////////////////////////
///// Fonctions utiles Table des livres encodés (ne pas modifier) ////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////////////////////////
void MainWindowClientBookEncoder::addTupleTableBooks(int id,
                                                     string title,
                                                     string author,
                                                     string subject,
                                                     string isbn,
                                                     int pageCount,
                                                     int publishYear,
                                                     float price,
                                                     int stockQuantity)
{
    int nb = ui->tableWidgetEncodedBooks->rowCount();
    nb++;
    ui->tableWidgetEncodedBooks->setRowCount(nb);
    ui->tableWidgetEncodedBooks->setRowHeight(nb-1,10);

    // id
    QTableWidgetItem *item = new QTableWidgetItem;
    item->setFlags(item->flags() & ~Qt::ItemIsSelectable);
    item->setTextAlignment(Qt::AlignCenter);
    item->setText(QString::number(id));
    ui->tableWidgetEncodedBooks->setItem(nb-1,0,item);

    // title
    item = new QTableWidgetItem;
    item->setFlags(item->flags() & ~Qt::ItemIsSelectable);
    item->setText(QString::fromStdString(title));
    ui->tableWidgetEncodedBooks->setItem(nb-1,1,item);

    // author
    item = new QTableWidgetItem;
    item->setFlags(item->flags() & ~Qt::ItemIsSelectable);
    item->setTextAlignment(Qt::AlignCenter);
    item->setText(QString::fromStdString(author));
    ui->tableWidgetEncodedBooks->setItem(nb-1,2,item);

    // subject
    item = new QTableWidgetItem;
    item->setFlags(item->flags() & ~Qt::ItemIsSelectable);
    item->setTextAlignment(Qt::AlignCenter);
    item->setText(QString::fromStdString(subject));
    ui->tableWidgetEncodedBooks->setItem(nb-1,3,item);

    // isbn
    item = new QTableWidgetItem;
    item->setFlags(item->flags() & ~Qt::ItemIsSelectable);
    item->setTextAlignment(Qt::AlignCenter);
    item->setText(QString::fromStdString(isbn));
    ui->tableWidgetEncodedBooks->setItem(nb-1,4,item);

    // pageCount
    item = new QTableWidgetItem;
    item->setFlags(item->flags() & ~Qt::ItemIsSelectable);
    item->setTextAlignment(Qt::AlignCenter);
    item->setText(QString::number(pageCount));
    ui->tableWidgetEncodedBooks->setItem(nb-1,5,item);

    // publishYear
    item = new QTableWidgetItem;
    item->setFlags(item->flags() & ~Qt::ItemIsSelectable);
    item->setTextAlignment(Qt::AlignCenter);
    item->setText(QString::number(publishYear));
    ui->tableWidgetEncodedBooks->setItem(nb-1,6,item);

    // price
    item = new QTableWidgetItem;
    item->setFlags(item->flags() & ~Qt::ItemIsSelectable);
    item->setTextAlignment(Qt::AlignCenter);
    item->setText(QString::number(price));
    ui->tableWidgetEncodedBooks->setItem(nb-1,7,item);

    // stockQuantity
    item = new QTableWidgetItem;
    item->setFlags(item->flags() & ~Qt::ItemIsSelectable);
    item->setTextAlignment(Qt::AlignCenter);
    item->setText(QString::number(stockQuantity));
    ui->tableWidgetEncodedBooks->setItem(nb-1,8,item);
}

///////////////////////////////////////////////////////////////////////////////////////////////////////////////
void MainWindowClientBookEncoder::clearTableBooks() {
    ui->tableWidgetEncodedBooks->setRowCount(0);
}

///////////////////////////////////////////////////////////////////////////////////////////////////////////////
///// Fonctions utiles des comboboxes (ne pas modifier) //////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////////////////////////
void MainWindowClientBookEncoder::addComboBoxAuthors(string author){
    ui->comboBoxAuthors->addItem(QString::fromStdString(author));
}

string MainWindowClientBookEncoder::getSelectionAuthor() const {
    return ui->comboBoxAuthors->currentText().toStdString();
}

void MainWindowClientBookEncoder::clearComboBoxAuthors() {
    ui->comboBoxAuthors->clear();
}

void MainWindowClientBookEncoder::addComboBoxSubjects(string subject){
    ui->comboBoxSubjects->addItem(QString::fromStdString(subject));
}

string MainWindowClientBookEncoder::getSelectionSubject() const {
    return ui->comboBoxSubjects->currentText().toStdString();
}

void MainWindowClientBookEncoder::clearComboBoxSubjects() {
    ui->comboBoxSubjects->clear();
}

///////////////////////////////////////////////////////////////////////////////////////////////////////////////
///// Fonction utiles de la fenêtre (ne pas modifier) ////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////////////////////////
string MainWindowClientBookEncoder::getTitle() const {
    return ui->lineEditTitle->text().toStdString();
}

string MainWindowClientBookEncoder::getIsbn() const {
    return ui->lineEditIsbn->text().toStdString();
}

int MainWindowClientBookEncoder::getPageCount() const {
    return ui->spinBoxPageCount->value();
}

float MainWindowClientBookEncoder::getPrice() const {
    return ui->doubleSpinBoxPrice->value();
}

int MainWindowClientBookEncoder::getPublishYear() const {
    return ui->spinBoxPublishYear->value();
}

int MainWindowClientBookEncoder::getStockQuantity() const {
    return ui->spinBoxStockQuantity->value();
}

void MainWindowClientBookEncoder::loginOk() {
    ui->pushButtonClear->setEnabled(true);
    ui->pushButtonAddBook->setEnabled(true);
    ui->pushButtonAddAuthor->setEnabled(true);
    ui->pushButtonAddSubject->setEnabled(true);
    ui->actionLogin->setEnabled(false);
    ui->actionLogout->setEnabled(true);
}

void MainWindowClientBookEncoder::logoutOk() {
    ui->pushButtonClear->setEnabled(false);
    ui->pushButtonAddBook->setEnabled(false);
    ui->pushButtonAddAuthor->setEnabled(false);
    ui->pushButtonAddSubject->setEnabled(false);
    ui->actionLogin->setEnabled(true);
    ui->actionLogout->setEnabled(false);
}
///////////////////////////////////////////////////////////////////////////////////////////////////////////////
///// Fonctions permettant d'afficher des boites de dialogue (ne pas modifier) ///////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////////////////////////
void MainWindowClientBookEncoder::dialogMessage(const string& title,const string& message) {
   QMessageBox::information(this,QString::fromStdString(title),QString::fromStdString(message));
}

/////////////////////////////////////////////////////////////////////////////////////////////////////////////
void MainWindowClientBookEncoder::dialogError(const string& title,const string& message) {
   QMessageBox::critical(this,QString::fromStdString(title),QString::fromStdString(message));
}

/////////////////////////////////////////////////////////////////////////////////////////////////////////////
string MainWindowClientBookEncoder::dialogInputText(const string& title,const string& question) {
    return QInputDialog::getText(this,QString::fromStdString(title),QString::fromStdString(question)).toStdString();
}

/////////////////////////////////////////////////////////////////////////////////////////////////////////////
int MainWindowClientBookEncoder::dialogInputInt(const string& title,const string& question) {
    return QInputDialog::getInt(this,QString::fromStdString(title),QString::fromStdString(question));
}

///////////////////////////////////////////////////////////////////////////////////////////////////////////////
///// Fonctions gestion des boutons et items de menu (TO DO) /////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////////////////////////
void MainWindowClientBookEncoder::on_pushButtonAddAuthor_clicked() {
    string lastName = this->dialogInputText("Nouvel auteur","Nom ?");
    string firstName = this->dialogInputText("Nouvel auteur","Prénom ?");
    string birthDate = this->dialogInputText("Nouvel auteur","Date de naissance (yyyy-mm-dd) ?");
    if (lastName.empty()) {
        this->dialogError("Erreur","Nom invalide !");
        return;
    }

    if (firstName.empty()) {
        this->dialogError("Erreur","Prénom invalide !");
        return;
    }
    if (birthDate.empty())
    {
        this->dialogError("Erreur","Date invalide !");
        return;
    }
    QDate date = QDate::fromString(QString::fromStdString(birthDate), "yyyy-MM-dd"); 
    if (!date.isValid() || date.toString("yyyy-MM-dd").toStdString() != birthDate) 
    {
        this->dialogError("Erreur","Date invalide ! Le format doit être yyyy-mm-dd.");
        return; 
    }
    if (!SansSeparateur(lastName) || !SansSeparateur(firstName)) {
        this->dialogError("Erreur", "Le caractère '#' est interdit !");
        return;
    }
    cout << "Nom : " << lastName << endl;
    cout << "Prénom : " << firstName << endl;
    cout << "Date de naissance : " << birthDate << endl;

    // ***** Construction de la requete *****
    string requete = string(ADD_AUTHOR) + SEPARATEUR_CHAMP + lastName
                                        + SEPARATEUR_CHAMP + firstName
                                        + SEPARATEUR_CHAMP + birthDate;

    // ***** Envoi requete + réception réponse *****
    char reponse[TAILLE_MAX_TRAME + 1];
    char *champs[MAX_CHAMPS];
    int nb = Dialogue(client.getSocketServeur(), requete, reponse, champs);

    // ***** Parsing de la réponse *****
    if (nb < 2 || strcmp(champs[0], ADD_AUTHOR) != 0) {
        this->dialogError("Erreur","Erreur lors de l'échange du message ADD_AUTHOR avec le serveur !");
        return;
    }

    if (!ReponseOk(nb, champs, ADD_AUTHOR)) {
        this->dialogError("Erreur", Champ(nb, champs, 2));
        return;
    }

    this->dialogMessage("Ajout Auteur", Champ(nb, champs, 2));
}

void MainWindowClientBookEncoder::on_pushButtonAddSubject_clicked() {
    string name = this->dialogInputText("Nouveau sujet","Nom ?");
    if (name.empty()) {
        this->dialogError("Erreur","Nom invalide !");
        return;
    }
    if (!SansSeparateur(name)) {
        this->dialogError("Erreur", "Le caractère '#' est interdit !");
        return;
    }
    cout << "Nom : " << name << endl;

    string requete = string(ADD_SUBJECT) + SEPARATEUR_CHAMP + name;

    char reponse[TAILLE_MAX_TRAME + 1];
    char *champs[MAX_CHAMPS];
    int nb = Dialogue(client.getSocketServeur(), requete, reponse, champs);

    if (nb < 2 || strcmp(champs[0], ADD_SUBJECT) != 0) {
        this->dialogError("Erreur","Erreur lors de l'échange du message ADD_SUBJECT avec le serveur !");
        return;
    }

    if (!ReponseOk(nb, champs, ADD_SUBJECT)) {
        this->dialogError("Erreur", Champ(nb, champs, 2));
        return;
    }

    this->dialogMessage("Ajout Sujet", Champ(nb, champs, 2));
}

void MainWindowClientBookEncoder::on_pushButtonAddBook_clicked() {
    cout << "title = " << this->getTitle() << endl;
    cout << "Isbn = " << this->getIsbn() << endl;
    cout << "PageCount = " << this->getPageCount() << endl;
    cout << "Price = " << this->getPrice() << endl;
    cout << "PublishYear = " << this->getPublishYear() << endl;
    cout << "Stock = " << this->getStockQuantity() << endl;

    cout << "selection auteur = " << this->getSelectionAuthor() << endl;
    cout << "selection sujet  = " << this->getSelectionSubject() << endl;

    if (!SansSeparateur(this->getTitle()) || !SansSeparateur(this->getIsbn())) {
        this->dialogError("Erreur", "Le caractère '#' est interdit !");
        return;
    }

    // ADD_BOOK#titre#isbn#auteur#sujet#pages#annee#prix#stock
    string requete = string(ADD_BOOK) + SEPARATEUR_CHAMP + this->getTitle()
                                      + SEPARATEUR_CHAMP + this->getIsbn()
                                      + SEPARATEUR_CHAMP + this->getSelectionAuthor()
                                      + SEPARATEUR_CHAMP + this->getSelectionSubject()
                                      + SEPARATEUR_CHAMP + std::to_string(this->getPageCount())
                                      + SEPARATEUR_CHAMP + std::to_string(this->getPublishYear())
                                      + SEPARATEUR_CHAMP + std::to_string(this->getPrice())
                                      + SEPARATEUR_CHAMP + std::to_string(this->getStockQuantity());

    char reponse[TAILLE_MAX_TRAME + 1];
    char *champs[MAX_CHAMPS];
    int nb = Dialogue(client.getSocketServeur(), requete, reponse, champs);

    if (nb < 2 || strcmp(champs[0], ADD_BOOK) != 0) {
        this->dialogError("Erreur","Erreur lors de l'échange du message ADD_BOOK avec le serveur !");
        return;
    }

    if (!ReponseOk(nb, champs, ADD_BOOK)) {
        this->dialogError("Erreur", Champ(nb, champs, 2));
        return;
    }

    this->dialogMessage("Ajout Livre", Champ(nb, champs, 2));
    this->addTupleTableBooks(0, this->getTitle(), this->getSelectionAuthor(), this->getSelectionSubject(),
                             this->getIsbn(), this->getPageCount(), this->getPublishYear(),
                             this->getPrice(), this->getStockQuantity());
}

void MainWindowClientBookEncoder::on_pushButtonClear_clicked() {
    ui->lineEditTitle->clear();
    ui->lineEditIsbn->clear();
    ui->spinBoxPageCount->setValue(0);
    ui->doubleSpinBoxPrice->setValue(0);
    ui->spinBoxPublishYear->setValue(0);
    ui->spinBoxStockQuantity->setValue(0);
}

void MainWindowClientBookEncoder::on_actionLogin_triggered() {
    string login = this->dialogInputText("Entrée en session","Login ?");
    string password = this->dialogInputText("Entrée en session","Password ?");

    if (!SansSeparateur(login) || !SansSeparateur(password)) {
        this->dialogError("Erreur", "Le caractère '#' est interdit !");
        return;
    }

    cout << "Login : " << login << endl;
    cout << "Password : " << password << endl;

    char reponse[TAILLE_MAX_TRAME + 1];
    char *champs[MAX_CHAMPS];
    int nb;

    // ***** Construction de la requete *****
    string requete = string(LOGIN) + SEPARATEUR_CHAMP + login + SEPARATEUR_CHAMP + password;

    // ***** Envoi requete + réception réponse *****
    printf("(CLIENT) Requete envoyee : %s\n", LOGIN);
    nb = Dialogue(client.getSocketServeur(), requete, reponse, champs);

    // ***** Parsing de la réponse *****   LOGIN#ok  ou  LOGIN#ko#raison
    if (nb < 2 || strcmp(champs[0], LOGIN) != 0) {
        this->dialogError("Erreur","Erreur lors de l'échange du message LOGIN avec le serveur !");
        return;
    }

    if (!ReponseOk(nb, champs, LOGIN)) {
        this->dialogError("Erreur","Erreur de connexion : " + Champ(nb, champs, 2));
        return;
    }

    ui->comboBoxAuthors->clear();
    ui->comboBoxSubjects->clear();

    // ***** GET_AUTHORS ***** réponse : GET_AUTHORS#ok#ids#noms#prenoms
    nb = Dialogue(client.getSocketServeur(), GET_AUTHORS, reponse, champs);

    if (nb < 2 || strcmp(champs[0], GET_AUTHORS) != 0)
    {
        dialogError("Erreur", "Erreur lors de l'échange de la requête GET_AUTHORS");
        return;
    }

    printf("(CLIENT) Réponse GET_AUTHORS reçue\n");

    if (ReponseOk(nb, champs, GET_AUTHORS) && nb == 5)
    {
        std::stringstream ids(champs[2]);
        std::stringstream lastnames(champs[3]);
        std::stringstream firstnames(champs[4]);

        std::string id, lastname, firstname;

        while (std::getline(ids, id, SEPARATEUR_LISTE) &&
            std::getline(lastnames, lastname, SEPARATEUR_LISTE) &&
            std::getline(firstnames, firstname, SEPARATEUR_LISTE))
        {
            this->addComboBoxAuthors(lastname + " " + firstname);
        }
    }

    // ***** GET_SUBJECTS ***** réponse : GET_SUBJECTS#ok#ids#noms
    nb = Dialogue(client.getSocketServeur(), GET_SUBJECTS, reponse, champs);

    if (nb < 2 || strcmp(champs[0], GET_SUBJECTS) != 0)
    {
        dialogError("Erreur", "Erreur lors de l'échange de la requête GET_SUBJECTS");
        return;
    }

    printf("(CLIENT) Réponse GET_SUBJECTS reçue\n");

    if (ReponseOk(nb, champs, GET_SUBJECTS) && nb == 4)
    {
        std::stringstream ids(champs[2]);
        std::stringstream names(champs[3]);

        std::string id, name;

        while (std::getline(ids, id, SEPARATEUR_LISTE) &&
            std::getline(names, name, SEPARATEUR_LISTE))
        {
            this->addComboBoxSubjects(name);
        }
    }

    printf("(CLIENT) Sujets ajoutés à la combobox\n");

    // Faciliter l'accès aux informations pour le client
    client.setLogin(login);
    client.setPassword(password);
    client.setConnected(true);

    // Affichage de la fenêtre principale après une connexion réussie
    this->loginOk();
}

void MainWindowClientBookEncoder::on_actionLogout_triggered() {
    // LOGOUT#login (le serveur ne répond pas)
    string requete = string(LOGOUT) + SEPARATEUR_CHAMP + client.getLogin();

    printf("(CLIENT) Requete envoyee : %s\n", LOGOUT);
    if (SendTrame(client.getSocketServeur(), requete.c_str()) < 0) {
        this->dialogError("Erreur","Erreur lors de l'envoi du message LOGOUT au serveur !");
        return;
    }
    
    ui->comboBoxAuthors->clear();
    ui->comboBoxSubjects->clear();
    clearTableBooks();

    client.setLogin("");
    client.setPassword("");
    client.setConnected(false);    
    this->logoutOk();
}

void MainWindowClientBookEncoder::on_actionQuitter_triggered(){
    if(client.isConnected())
    {
        string requete = string(LOGOUT) + SEPARATEUR_CHAMP + client.getLogin();

        printf("(CLIENT) Requete envoyee : %s\n", LOGOUT);
        if (SendTrame(client.getSocketServeur(), requete.c_str()) < 0) {
            this->dialogError("Erreur","Erreur lors de l'envoi du message LOGOUT au serveur !");
            return;
        }

        client.setLogin("");
        client.setPassword("");
        client.setConnected(false);    
            
    }
    QApplication::exit(0);
}
