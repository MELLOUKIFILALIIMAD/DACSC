# DACSC – Client Book Encoder (C/C++)

Client Qt5 d'encodage de livres. Il communique avec le serveur par des trames texte.

## Structure

```
ClientBookEncoderQt/
├── configuration.txt        # NB_THREADS et PORT_ENCODING
├── CMakeLists.txt
├── include/
│   ├── config.h             # lecture de la configuration (nbThreads, port)
│   ├── protocole.h          # définition du protocole (requêtes, séparateurs, tailles)
│   ├── socket.h             # sockets + envoi/réception de trames
│   └── Client.h
└── src/
    ├── config.c             # LireConfiguration()
    ├── socket.cpp
    ├── Client.cpp
    ├── main.cpp
    └── mainwindowclientbookencoder.cpp
```

## Prérequis

- CMake ≥ 3.16, compilateur C/C++17
- Qt5 (Widgets)
- Un serveur démarré et accessible sur `127.0.0.1:PORT_ENCODING`

## Compilation

```bash
cd ClientBookEncoderQt
mkdir build && cd build
cmake ..
make Client
```

## Configuration

`configuration.txt` est lu avec le chemin `../configuration.txt` : le client
doit donc être lancé **depuis le dossier `build/`**.

```
NB_THREADS=5        # utilisé par le serveur
PORT_ENCODING=5000  # port TCP auquel le client se connecte
```

## Lancement

```bash
cd ClientBookEncoderQt/build
./Client
```

Au démarrage, `main.cpp` appelle `LireConfiguration()`, crée la socket et se
connecte au serveur, puis ouvre la fenêtre Qt.

## Protocole

Le client et le serveur échangent des **trames texte**.

### Format d'une trame

```
XXXX<contenu>
```

- `XXXX` : longueur du contenu **en octets**, sur 4 chiffres ASCII
  complétés par des zéros (ex : `0012`)
- `contenu` : `REQUETE#champ1#champ2#...`
- longueur maximale du contenu : **9999 octets**

Exemple : `0016LOGIN#bob#secret`

Les champs ne doivent pas contenir `#` (le client refuse ce caractère dans les
données saisies). Les listes (`GET_*`) sont des champs dont chaque élément est
suivi de `;` (ex : `1;2;3;`).

### Requêtes envoyées et réponses attendues

| Requête (client → serveur) | Réponse (serveur → client) |
|---|---|
| `LOGIN#login#password` | `LOGIN#ok` ou `LOGIN#ko#raison` |
| `LOGOUT#login` | *(aucune)* |
| `GET_AUTHORS` | `GET_AUTHORS#ok#ids#noms#prenoms` ou `GET_AUTHORS#ko#raison` |
| `GET_SUBJECTS` | `GET_SUBJECTS#ok#ids#noms` ou `GET_SUBJECTS#ko#raison` |
| `ADD_AUTHOR#nom#prenom#yyyy-mm-dd` | `ADD_AUTHOR#ok#message` ou `ADD_AUTHOR#ko#raison` |
| `ADD_SUBJECT#nom` | `ADD_SUBJECT#ok#message` ou `ADD_SUBJECT#ko#raison` |
| `ADD_BOOK#titre#isbn#auteur#sujet#pages#annee#prix#stock` | `ADD_BOOK#ok#message` ou `ADD_BOOK#ko#raison` |

- `auteur` est de la forme `Nom Prénom`, comme dans la liste affichée.
- `prix` utilise `.` comme séparateur décimal.

### Exemple de session

```
C -> S : 0016LOGIN#bob#secret
S -> C : 0008LOGIN#ok
C -> S : 0011GET_AUTHORS
S -> C : 0052GET_AUTHORS#ok#1;2;#Werber;Herbert;#Bernard;Frank;
```

## Fonctions principales côté client

### `socket.h`

| Fonction | Rôle |
|---|---|
| `SendTrame` | Ajoute l'en-tête `XXXX` et envoie la trame (0 = ok, -1 = erreur) |
| `ReceiveTrame` | Lit une trame complète (1 = ok, 0 = déconnexion, -1 = erreur) |
| `Echange` | Envoie une requête puis attend la réponse |
| `Decouper` | Découpe `A#B#C` en champs (garde les champs vides) |

### `config.h`

| Fonction | Rôle |
|---|---|
| `LireConfiguration` | Remplit `nbThreads` et `port` à partir de `configuration.txt` |

### `mainwindowclientbookencoder.cpp`

| Fonction | Rôle |
|---|---|
| `Dialogue` | Envoie une requête via `Echange` et découpe la réponse en champs |
| `ReponseOk` | Vérifie que la réponse correspond à la requête et que son statut est `ok` |
| `Champ` | Retourne un champ de la réponse (ou `""` s'il n'existe pas) |
| `SansSeparateur` | Vérifie qu'un texte ne contient pas `#` |

## Limites connues

- Trame limitée à 9999 octets : de très longues listes d'auteurs peuvent la dépasser.
- Le caractère `#` est interdit dans les données saisies.
- Le mot de passe est envoyé en clair (pas de hachage pour l'instant).
