-- Marque lu/non-lu pour les notifications in-app (cloche de notification temps réel).

ALTER TABLE notification_logs
    ADD COLUMN is_read BOOLEAN NOT NULL DEFAULT FALSE;
