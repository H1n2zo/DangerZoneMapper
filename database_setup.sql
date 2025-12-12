-- Create Database
CREATE DATABASE IF NOT EXISTS danger_zone_db;
USE danger_zone_db;

-- Create Hazard Zones Table
CREATE TABLE IF NOT EXISTS hazard_zones (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    type VARCHAR(50) NOT NULL,
    latitude DOUBLE NOT NULL,
    longitude DOUBLE NOT NULL,
    radius DOUBLE NOT NULL,
    description TEXT,
    date_added DATE NOT NULL,
    INDEX idx_type (type),
    INDEX idx_date (date_added)
);

-- Insert Sample Data for Ormoc City
INSERT INTO hazard_zones (name, type, latitude, longitude, radius, description, date_added) VALUES
('Downtown Ormoc - Flood Zone', 'Flood', 11.0059, 124.6075, 800, 'Historical flooding during heavy rains and typhoons. Low-lying area near coastal region.', '2024-01-15'),
('Barangay Dolores Landslide Area', 'Landslide', 11.0850, 124.6100, 500, 'Steep slopes vulnerable during heavy rainfall. Multiple incidents recorded in rainy season.', '2024-02-20'),
('Market Area Fire Risk Zone', 'Fire', 11.0065, 124.6080, 300, 'Dense commercial area with high fire risk due to congested structures.', '2024-03-10'),
('Coastal Area Typhoon Zone', 'Typhoon', 11.0045, 124.6200, 1200, 'Exposed to typhoon storm surge and strong winds from Ormoc Bay.', '2024-01-25'),
('Alta Vista Landslide Risk', 'Landslide', 11.0500, 124.6000, 600, 'Mountainous area with history of soil erosion and landslides during monsoon.', '2024-02-15'),
('Barangay Liberty - Flood Prone', 'Flood', 11.0100, 124.6050, 700, 'River overflow area during typhoon season. Evacuation required during heavy rains.', '2024-03-01'),
('Industrial Zone Fire Risk', 'Fire', 11.0200, 124.6150, 400, 'Industrial warehouses and factories with combustible materials.', '2024-01-30'),
('San Pablo Earthquake Zone', 'Earthquake', 11.0300, 124.6120, 1000, 'Located near fault line. Multiple minor tremors recorded in past years.', '2024-02-05');

-- Create Historical Incidents Table
CREATE TABLE IF NOT EXISTS historical_incidents (
    id INT AUTO_INCREMENT PRIMARY KEY,
    hazard_zone_id INT,
    incident_date DATE NOT NULL,
    severity VARCHAR(50),
    casualties INT DEFAULT 0,
    damages_estimated DECIMAL(15,2),
    description TEXT,
    FOREIGN KEY (hazard_zone_id) REFERENCES hazard_zones(id) ON DELETE CASCADE
);

-- Insert Sample Historical Incidents
INSERT INTO historical_incidents (hazard_zone_id, incident_date, severity, casualties, damages_estimated, description) VALUES
(1, '2023-11-15', 'High', 0, 5000000.00, 'Severe flooding during Typhoon Ramon affected 500+ families. Water level reached 2 meters.'),
(2, '2023-07-20', 'Medium', 2, 1500000.00, 'Landslide blocked main road. Two casualties reported. 15 houses damaged.'),
(3, '2024-01-05', 'Low', 0, 800000.00, 'Market fire contained quickly. 3 stalls destroyed.'),
(4, '2023-12-10', 'High', 0, 8000000.00, 'Typhoon Odette brought storm surge. Coastal homes severely damaged.'),
(5, '2023-08-12', 'Medium', 1, 2000000.00, 'Heavy rains triggered landslide. One fatality, 8 houses destroyed.'),
(1, '2022-10-18', 'Medium', 0, 3000000.00, 'Flash flood in downtown area. Roads impassable for 2 days.'),
(6, '2023-09-05', 'High', 0, 4500000.00, 'River overflow affected 300 families. Major evacuation conducted.');

-- Create Users Table (for admin access)
CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(50) DEFAULT 'viewer',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Insert default admin user (password: admin123)
INSERT INTO users (username, password, role) VALUES
('admin', 'admin123', 'admin'),
('viewer', 'viewer123', 'viewer');

-- Create Safety Guidelines Table
CREATE TABLE IF NOT EXISTS safety_guidelines (
    id INT AUTO_INCREMENT PRIMARY KEY,
    hazard_type VARCHAR(50) NOT NULL,
    guideline_title VARCHAR(255),
    guideline_text TEXT,
    priority INT DEFAULT 1
);

-- Insert Safety Guidelines
INSERT INTO safety_guidelines (hazard_type, guideline_title, guideline_text, priority) VALUES
('Flood', 'Before a Flood', 'Store emergency supplies. Know evacuation routes. Elevate electrical appliances.', 1),
('Flood', 'During a Flood', 'Move to higher ground immediately. Avoid walking through floodwater. Turn off utilities.', 2),
('Flood', 'After a Flood', 'Return home only when authorities say it is safe. Check for structural damage.', 3),
('Landslide', 'Warning Signs', 'Watch for cracks in walls, tilting trees, and unusual sounds. Evacuate immediately.', 1),
('Landslide', 'During Landslide', 'Move quickly away from the path. Go to high ground if possible.', 2),
('Fire', 'Fire Prevention', 'Check electrical wiring. Store flammables safely. Install smoke detectors.', 1),
('Fire', 'During Fire', 'Call 911. Evacuate immediately. Stay low and cover mouth. Do not use elevators.', 2),
('Earthquake', 'Drop, Cover, Hold', 'Drop to ground, take cover under sturdy furniture, hold on until shaking stops.', 1),
('Earthquake', 'After Earthquake', 'Check for injuries. Inspect home for damage. Be prepared for aftershocks.', 2),
('Typhoon', 'Before Typhoon', 'Secure loose objects. Stock emergency supplies. Follow evacuation orders.', 1),
('Typhoon', 'During Typhoon', 'Stay indoors. Stay away from windows. Listen to emergency broadcasts.', 2);

-- Show all tables
SHOW TABLES;

-- Display counts
SELECT 'Hazard Zones' as Table_Name, COUNT(*) as Record_Count FROM hazard_zones
UNION ALL
SELECT 'Historical Incidents', COUNT(*) FROM historical_incidents
UNION ALL
SELECT 'Safety Guidelines', COUNT(*) FROM safety_guidelines;

SELECT 'Database setup completed successfully!' as Status;