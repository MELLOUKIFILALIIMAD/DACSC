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
using namespace std;

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
    string tailleLastName = to_string(lastName.length());
    string tailleFirstName = to_string(firstName.length());
    string taillebirthDate = to_string(birthDate.length());
    if (tailleLastName == "0") {
        this->dialogError("Erreur","Nom invalide !");
        return;
    }

    if (tailleFirstName == "0") {
        this->dialogError("Erreur","Prénom invalide !");
        return;
    }
    if (taillebirthDate  == "0")
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
    cout << "Nom : " << lastName << endl;
    cout << "Prénom : " << firstName << endl;
    cout << "Date de naissance : " << birthDate << endl;
    MESSAGE m;
    m.type = client.getSocketServeur();
    m.expediteur = idClient;
    m.requete = ADD_AUTHOR;
    m.data1 = (char*) lastName.c_str();
    m.data2 = (char*) firstName.c_str();
    m.texte = (char*) birthDate.c_str();
    int result = Send(m.type, &m);
    if (result < 0) {
        this->dialogError("Erreur","Erreur lors de l'envoi du message ADD_AUTHOR au serveur !");
        return;
    }
    int receiveResult = Receive(client.getSocketServeur(), &m);
    if (receiveResult < 0) {
        this->dialogError("Erreur","Erreur lors de la réception de la réponse du serveur pour le message ADD_AUTHOR !");
        return;
    }

    if (string(m.data1) != "OK") {
        this->dialogError("Erreur", string(m.texte));
        return;
    }
    else
    {
        this->dialogMessage("Ajout Auteur", string(m.texte));  
        this->addComboBoxAuthors(lastName + " " + firstName);      
    }
}

void MainWindowClientBookEncoder::on_pushButtonAddSubject_clicked() {
    string name = this->dialogInputText("Nouveau sujet","Nom ?");
    string tailleName = to_string(name.length());
    if (tailleName == "0") {
        this->dialogError("Erreur","Nom invalide !");
        return;
    }
    cout << "Nom : " << name << endl;
    MESSAGE m;
    m.type = client.getSocketServeur();
    m.requete = ADD_SUBJECT;
    m.expediteur = idClient;
    m.data1 = (char*) name.c_str();
    m.data2 = NULL;
    m.texte = NULL;
    int result = Send(m.type, &m);
    if (result < 0) {
        this->dialogError("Erreur","Erreur lors de l'envoi du message ADD_SUBJECT au serveur !");
        return;
    }
    int receiveResult = Receive(client.getSocketServeur(), &m);
    if (receiveResult < 0) {
        this->dialogError("Erreur","Erreur lors de la réception de la réponse du serveur pour le message ADD_SUBJECT !");
        return;
    }

    if (string(m.data1) != "OK") {
        this->dialogError("Erreur", string(m.texte));
        return;
    }
    else
    {
        this->dialogMessage("Ajout Sujet", string(m.texte));
        this->addComboBoxSubjects(name);
    } 
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

    MESSAGE msg;
    msg.type = client.getSocketServeur();
    msg.expediteur = idClient;
    msg.requete = ADD_BOOK;
    msg.data1 = (char*) this->getTitle().c_str();
    msg.data2 = (char*) this->getIsbn().c_str();
    
    string data3 = this->getSelectionAuthor() + ";" + this->getSelectionSubject() + ";" +
                   std::to_string(this->getPageCount()) + ";" +
                   std::to_string(this->getPublishYear()) + ";" +
                   std::to_string(this->getPrice()) + ";" +
                   std::to_string(this->getStockQuantity());
    msg.texte = (char*) data3.c_str();

    int result = Send(msg.type, &msg);
    if (result < 0) {
        this->dialogError("Erreur","Erreur lors de l'envoi du message ADD_BOOK au serveur !");
    }

    int receiveResult = Receive(client.getSocketServeur(), &msg);
    if (receiveResult < 0) {
        this->dialogError("Erreur","Erreur lors de la réception de la réponse du serveur pour le message ADD_BOOK !");
    }

    if (string(msg.data1) != "OK") {
        this->dialogError("Erreur", string(msg.texte));
    }
    else
    {
        this->dialogMessage("Ajout Livre", string(msg.texte));
        this->addTupleTableBooks(0, this->getTitle(), this->getSelectionAuthor(), this->getSelectionSubject(),
                                 this->getIsbn(), this->getPageCount(), this->getPublishYear(),
                                 this->getPrice(), this->getStockQuantity());
    }
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

    string tailleLogin = std::to_string(login.length());
    string taillePassword = std::to_string(password.length());

    if (tailleLogin == "0") {
        this->dialogError("Erreur","Login invalide !");
        return;
    }

    if (taillePassword == "0") {
        this->dialogError("Erreur","Password invalide !");
        return;
    }

    // Trouver un autre moyen ?
    while (tailleLogin.length() < 4) {
        tailleLogin = "0" + tailleLogin;
    }
    while (taillePassword.length() < 4) {
        taillePassword = "0" + taillePassword;
    }

    string data2 = login;
    string texte = password;

    cout << "Login : " << data2 << endl;
    cout << "Password : " << texte << endl;

    MESSAGE message;
    message.type = client.getSocketServeur();
    message.expediteur = idClient;
    message.requete = LOGIN;
    message.data1 = NULL;
    message.data2 = data2.empty() ? NULL : (char*)data2.c_str();
    message.texte = texte.empty() ? NULL : (char*)texte.c_str();


    // Traitement du message LOGIN côté client
    printf("(CLIENT) Requete envoyee : %d\n", message.requete);
    int result = Send(message.type, &message);
    if (result < 0) {
        this->dialogError("Erreur","Erreur lors de l'envoi du message LOGIN au serveur !");
        return;
    }

    // Attente de la réponse du serveur
    int receiveResult = Receive(client.getSocketServeur(), &message);
    if (receiveResult < 0) {
        this->dialogError("Erreur","Erreur lors de la réception de la réponse du serveur pour le message LOGIN !");
        return;
    }

    if (string(message.data1) != "OK") {
        this->dialogError("Erreur","Erreur de connexion : " + string(message.texte));
        return;
    }
    ui->comboBoxAuthors->clear();
    ui->comboBoxSubjects->clear();
    MESSAGE msg;
    msg.type = client.getSocketServeur();
    msg.expediteur = idClient;
    msg.requete = GET_AUTHORS;
    msg.data1 = NULL;
    msg.data2 = NULL;
    msg.texte = NULL;

    if (Send(idClient, &msg) == -1)
    {
        dialogError("Erreur", "Erreur lors de l'envoi de la requête GET_AUTHORS");
        return;
    }

    printf("(CLIENT) Requête GET_AUTHORS envoyée\n");

    if (Receive(client.getSocketServeur(), &msg) == -1)
    {
        dialogError("Erreur", "Erreur lors de la réception de la réponse GET_AUTHORS");
        return;
    }

    printf("(CLIENT) Réponse GET_AUTHORS reçue\n");

    if (msg.data1 != NULL && msg.data2 != NULL && msg.texte != NULL)
    {
        std::stringstream ids(msg.data1);
        std::stringstream lastnames(msg.data2);
        std::stringstream firstnames(msg.texte);

        std::string id, lastname, firstname;

        while (std::getline(ids, id, ';') &&
            std::getline(lastnames, lastname, ';') &&
            std::getline(firstnames, firstname, ';'))
        {
            std::string author = lastname + " " + firstname;

            this->addComboBoxAuthors(author);
        }
    }

    free(msg.data1);
    free(msg.data2);
    free(msg.texte);

    msg.type = client.getSocketServeur();;
    msg.expediteur = idClient;
    msg.requete = GET_SUBJECTS;
    msg.data1 = NULL;
    msg.data2 = NULL;
    msg.texte = NULL;

    if (Send(idClient, &msg) == -1)
    {
        dialogError("Erreur", "Erreur lors de l'envoi de la requête GET_SUBJECTS");
        return;
    }

    printf("(CLIENT) Requête GET_SUBJECTS envoyée\n");

    if (Receive(client.getSocketServeur(), &msg) == -1)
    {
        dialogError("Erreur", "Erreur lors de la réception de la réponse GET_SUBJECTS");
        return;
    }

    printf("(CLIENT) Réponse GET_SUBJECTS reçue\n");

    if (msg.data1 != NULL && msg.texte != NULL)
    {
        std::stringstream ids(msg.data1);
        std::stringstream names(msg.texte);

        std::string id, name;

        while (std::getline(ids, id, ';') &&
            std::getline(names, name, ';'))
        {
            this->addComboBoxSubjects(name);
        }
    }

    printf("(CLIENT) Sujets ajoutés à la combobox\n");

    free(msg.data1);
    free(msg.texte);



    // Faciliter l'accès aux informations pour le client
    client.setLogin(login);
    client.setPassword(password);
    client.setConnected(true);

    // Affichage de la fenêtre principale après une connexion réussie
    this->loginOk();
}

void MainWindowClientBookEncoder::on_actionLogout_triggered() {
    MESSAGE message;
    message.type = client.getSocketServeur();
    message.expediteur = idClient;
    message.requete = LOGOUT;
    message.data1 = NULL;
    message.data2 = (char*)client.getLogin().c_str();

    printf("(CLIENT) Requete envoyee : %d\n", message.requete);
    int result = Send(message.type, &message);
    if (result < 0) {
        this->dialogError("Erreur","Erreur lors de l'envoi du message LOGOUT au serveur !");
        return;
    }
    
    ui->comboBoxAuthors->clear();
    ui->comboBoxSubjects->clear();

    client.setLogin("");
    client.setPassword("");
    client.setConnected(false);    
    this->logoutOk();
}

void MainWindowClientBookEncoder::on_actionQuitter_triggered(){
    if(client.isConnected())
    {
        MESSAGE message;
        message.type = client.getSocketServeur();
        message.expediteur = idClient;
        message.requete = LOGOUT;
        message.data1 = NULL;
        message.data2 = (char*)client.getLogin().c_str();

        printf("(CLIENT) Requete envoyee : %d\n", message.requete);
        int result = Send(message.type, &message);
        if (result < 0) {
            this->dialogError("Erreur","Erreur lors de l'envoi du message LOGOUT au serveur !");
            return;
        }

        client.setLogin("");
        client.setPassword("");
        client.setConnected(false);    
            
    }
    QApplication::exit(0);
}