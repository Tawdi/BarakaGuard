
CREATE DATABASE barakaguard;

-- Table Client
CREATE TABLE IF NOT EXISTS Client (
    id UUID PRIMARY KEY,
    nom VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL
);

-- Table Compte
CREATE TABLE IF NOT EXISTS Compte (
    id UUID PRIMARY KEY,
    numero VARCHAR(20) UNIQUE NOT NULL,
    solde NUMERIC(15,2) DEFAULT 0,
    idClient UUID NOT NULL,
    typeCompte VARCHAR(20) NOT NULL CHECK (typeCompte IN ('courant', 'epargne')),
    decouvertAutorise NUMERIC(15,2),
    tauxInteret NUMERIC(5,2),
    CONSTRAINT fk_client FOREIGN KEY (idClient)
    REFERENCES Client(id) ON DELETE CASCADE
);

-- Table Transaction
CREATE TABLE IF NOT EXISTS Transaction (
    id UUID PRIMARY KEY,
    date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    montant NUMERIC(15,2) NOT NULL,
    type VARCHAR(20) NOT NULL CHECK (type IN ('VERSEMENT', 'RETRAIT', 'VIREMENT')),
    lieu VARCHAR(100),
    idCompte UUID NOT NULL,
    CONSTRAINT fk_compte FOREIGN KEY (idCompte)
    REFERENCES Compte(id) ON DELETE CASCADE
);

-- Index utiles
CREATE INDEX IF NOT EXISTS idx_transaction_idCompte ON Transaction(idCompte);
CREATE INDEX IF NOT EXISTS idx_compte_idClient ON Compte(idClient);
