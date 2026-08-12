-- Garde-fou d'idempotence pour les crédits automatiques (répartition des paiements
-- propriétaire/plateforme) : deux mouvements de même type portant la même référence sur le
-- même portefeuille ne peuvent pas coexister. NULL reste libre côté MySQL (dépôts/retraits
-- manuels sans référence peuvent toujours se répéter).
ALTER TABLE wallet_transactions
    ADD CONSTRAINT uk_wallet_transactions_wallet_type_reference UNIQUE (wallet_id, type, reference);
