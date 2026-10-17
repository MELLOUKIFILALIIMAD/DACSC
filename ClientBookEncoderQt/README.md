# DACSC – Book Encoder

Application client/serveur d'encodage de livres.

- **Client** : interface Qt5 (C++)
- **Serveur** : C++ multi-threads (pool de threads) + MySQL/MariaDB
- **Client Java** : peut utiliser le même serveur grâce au protocole texte décrit ci-dessous

## Structure

```
ClientBookEncoderQt/
├── configuration.txt        # NB_THREADS et PORT_ENCODING
├── CMakeLists.txt
├── include/
│   ├── config.h             # lecture de la configuration (nbThreads, port)
│   ├── protocole.h          # définition du protocole (requêtes, séparateurs, tailles)
│   ├── socket.h             # sockets + envoi/réception de trames
│   ├── database.h           # accès base de données (serveur)
│   └── Client.h
└── src/
    ├── config.c             # LireConfiguration()
    ├── socket.cpp
    ├── Serveur.cpp
    ├── database.cpp
    ├── Client.cpp
    ├── main.cpp
    └── mainwindowclientbookencoder.cpp
ClientBookEncoderJava/       # entités et DAO Java
BD_Books/                    # création de la base de données
```

## Prérequis

- CMake ≥ 3.16, compilateur C/C++17
- Qt5 (Widgets)
- Bibliothèque cliente MySQL/MariaDB (`mysql.h`, `libmysqlclient`)
- Base de données `PourStudent` accessible en local (utilisateur `Student`),
  créée avec `BD_Books/CreationBD.cpp`

## Compilation

```bash
cd ClientBookEncoderQt
mkdir build && cd build
cmake ..
make
```

Deux exécutables sont produits : `Client` et `Serveur`.

## Configuration

`configuration.txt` est lu avec le chemin `../configuration.txt` : les
exécutables doivent donc être lancés **depuis le dossier `build/`**.

```
NB_THREADS=5        # nombre de clients gérés simultanément
PORT_ENCODING=5000  # port TCP du serveur
```

## Lancement

```bash
cd ClientBookEncoderQt/build
./Serveur      # d'abord
./Client       # ensuite (se connecte à 127.0.0.1:PORT_ENCODING)
```

## Protocole

Le client et le serveur échangent des **trames texte**, ce qui permet à
n'importe quel langage (C++, Java…) de communiquer avec le serveur.

### Format d'une trame

```
XXXX<contenu>
```

- `XXXX` : longueur du contenu **en octets** (UTF-8), sur 4 chiffres ASCII
  complétés par des zéros (ex : `0012`)
- `contenu` : `REQUETE#champ1#champ2#...`
- longueur maximale du contenu : **9999 octets**

Exemple : `0016LOGIN#bob#secret`

Les champs ne doivent pas contenir `#`. Les listes (`GET_*`) sont des champs
dont chaque élément est suivi de `;` (ex : `1;2;3;`).

### Requêtes et réponses

| Requête (client → serveur) | Réponse (serveur → client) |
|---|---|
| `LOGIN#login#password` | `LOGIN#ok` ou `LOGIN#ko#raison` |
| `LOGOUT#login` | *(aucune)* |
| `GET_AUTHORS` | `GET_AUTHORS#ok#ids#noms#prenoms` ou `GET_AUTHORS#ko#raison` |
| `GET_SUBJECTS` | `GET_SUBJECTS#ok#ids#noms` ou `GET_SUBJECTS#ko#raison` |
| `ADD_AUTHOR#nom#prenom#yyyy-mm-dd` | `ADD_AUTHOR#ok#message` ou `ADD_AUTHOR#ko#raison` |
| `ADD_SUBJECT#nom` | `ADD_SUBJECT#ok#message` ou `ADD_SUBJECT#ko#raison` |
| `ADD_BOOK#titre#isbn#auteur#sujet#pages#annee#prix#stock` | `ADD_BOOK#ok#message` ou `ADD_BOOK#ko#raison` |

- `auteur` est de la forme `Nom Prénom`, comme dans la liste renvoyée par `GET_AUTHORS`.
- `prix` utilise `.` comme séparateur décimal.
- Une requête inconnue ou mal formée reçoit `REQUETE#ko#Requête inconnue` ou
  `REQUETE#ko#Requête invalide`.

### Exemple de session

```
C -> S : 0016LOGIN#bob#secret
S -> C : 0008LOGIN#ok
C -> S : 0011GET_AUTHORS
S -> C : 0052GET_AUTHORS#ok#1;2;#Werber;Herbert;#Bernard;Frank;
```

## Écrire un client Java

- Lire **exactement 4 octets**, les convertir en entier `n`, puis lire
  **exactement `n` octets** (`DataInputStream.readFully`).
- Calculer la longueur sur les **octets UTF-8** et non sur le nombre de
  caractères (important pour les accents).
- Découper les champs avec `split("#", -1)` : sans le `-1`, Java supprime les
  champs vides en fin de trame.
- Ne jamais envoyer `#` à l'intérieur d'un champ.

## Fonctions principales (`socket.h`)

| Fonction | Rôle |
|---|---|
| `SendTrame` | Ajoute l'en-tête `XXXX` et envoie la trame |
| `ReceiveTrame` | Lit une trame complète (1 = ok, 0 = déconnexion, -1 = erreur) |
| `Echange` | Côté client : envoie une requête puis attend la réponse |
| `Decouper` | Découpe `A#B#C` en champs (garde les champs vides, thread-safe) |

`LireConfiguration()` (`config.h`) remplit `nbThreads` et `port` à partir de
`configuration.txt`.

## Limites connues

- Trame limitée à 9999 octets : de très longues listes d'auteurs peuvent la dépasser.
- Le caractère `#` est interdit dans les données saisies.
- Les mots de passe sont envoyés et stockés en clair (pas de hachage pour l'instant).