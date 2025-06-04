ALTER TABLE apartment
ADD COLUMN Type VARCHAR(10) DEFAULT 'SALE' AFTER Area;

-- Update existing records to have a default type
UPDATE apartment SET Type = 'SALE' WHERE Type IS NULL; 