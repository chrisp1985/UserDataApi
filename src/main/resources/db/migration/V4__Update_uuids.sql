UPDATE user
SET uniqueId = UUID()
WHERE uniqueId IS NULL;