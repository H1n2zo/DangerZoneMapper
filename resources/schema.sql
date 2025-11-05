-- Danger Zone Mapping System Database Schema - ENHANCED
-- MySQL version - ORMOC CITY ONLY

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";

-- Drop existing tables if they exist
DROP TABLE IF EXISTS `audit_log`;
DROP TABLE IF EXISTS `user_activity`;
DROP TABLE IF EXISTS `incidents`;
DROP TABLE IF EXISTS `hazard_zones`;
DROP TABLE IF EXISTS `landmarks`;
DROP TABLE IF EXISTS `safety_guidelines`;
DROP TABLE IF EXISTS `users`;

-- Landmarks Table (ENHANCED)
CREATE TABLE `landmarks` (
  `landmark_id` int(11) NOT NULL AUTO_INCREMENT,
  `landmark_name` varchar(100) NOT NULL,
  `landmark_type` varchar(50) DEFAULT NULL,
  `address` text DEFAULT NULL,
  `barangay` varchar(100) DEFAULT NULL,
  `latitude` decimal(10,8) NOT NULL,
  `longitude` decimal(11,8) NOT NULL,
  `contact_number` varchar(50) DEFAULT NULL,
  `capacity` int(11) DEFAULT NULL,
  `description` text DEFAULT NULL,
  `operating_hours` varchar(100) DEFAULT NULL,
  `is_evacuation_site` tinyint(1) DEFAULT 0,
  `is_active` tinyint(1) DEFAULT 1,
  `facilities` text DEFAULT NULL,
  `accessibility_notes` text DEFAULT NULL,
  `created_by` int(11) DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
  `last_updated` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  PRIMARY KEY (`landmark_id`),
  KEY `idx_landmarks_barangay` (`barangay`),
  KEY `idx_landmarks_created_by` (`created_by`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Hazard Zones Table (ENHANCED)
CREATE TABLE `hazard_zones` (
  `zone_id` int(11) NOT NULL AUTO_INCREMENT,
  `zone_name` varchar(100) NOT NULL,
  `barangay` varchar(100) DEFAULT NULL,
  `hazard_type` varchar(50) NOT NULL,
  `severity_level` varchar(20) DEFAULT NULL,
  `latitude` decimal(10,8) NOT NULL,
  `longitude` decimal(11,8) NOT NULL,
  `radius_meters` int(11) DEFAULT NULL,
  `description` text DEFAULT NULL,
  `risk_factors` text DEFAULT NULL,
  `affected_population` int(11) DEFAULT NULL,
  `mitigation_measures` text DEFAULT NULL,
  `early_warning_system` varchar(100) DEFAULT NULL,
  `date_identified` date DEFAULT NULL,
  `last_assessment_date` date DEFAULT NULL,
  `next_assessment_date` date DEFAULT NULL,
  `assessment_notes` text DEFAULT NULL,
  `created_by` int(11) DEFAULT NULL,
  `last_updated` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `is_active` tinyint(1) DEFAULT 1,
  PRIMARY KEY (`zone_id`),
  KEY `idx_hazard_barangay` (`barangay`),
  KEY `idx_hazard_created_by` (`created_by`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Historical Incidents Table (ENHANCED)
CREATE TABLE `incidents` (
  `incident_id` int(11) NOT NULL AUTO_INCREMENT,
  `zone_id` int(11) DEFAULT NULL,
  `incident_type` varchar(50) NOT NULL,
  `incident_date` date NOT NULL,
  `incident_time` time DEFAULT NULL,
  `barangay` varchar(100) DEFAULT NULL,
  `severity` varchar(20) DEFAULT NULL,
  `casualties` int(11) DEFAULT 0,
  `injuries` int(11) DEFAULT 0,
  `missing` int(11) DEFAULT 0,
  `families_affected` int(11) DEFAULT 0,
  `structures_damaged` int(11) DEFAULT 0,
  `estimated_cost` decimal(15,2) DEFAULT NULL,
  `description` text DEFAULT NULL,
  `response_actions` text DEFAULT NULL,
  `response_time_minutes` int(11) DEFAULT NULL,
  `evacuation_conducted` tinyint(1) DEFAULT 0,
  `evacuees_count` int(11) DEFAULT 0,
  `lessons_learned` text DEFAULT NULL,
  `latitude` decimal(10,8) DEFAULT NULL,
  `longitude` decimal(11,8) DEFAULT NULL,
  `weather_conditions` varchar(100) DEFAULT NULL,
  `created_by` int(11) DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
  `last_updated` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  PRIMARY KEY (`incident_id`),
  KEY `zone_id` (`zone_id`),
  KEY `idx_incident_date` (`incident_date`),
  KEY `idx_incident_barangay` (`barangay`),
  KEY `idx_incident_created_by` (`created_by`),
  CONSTRAINT `incidents_ibfk_1` FOREIGN KEY (`zone_id`) REFERENCES `hazard_zones` (`zone_id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Safety Guidelines Table (ENHANCED)
CREATE TABLE `safety_guidelines` (
  `guideline_id` int(11) NOT NULL AUTO_INCREMENT,
  `hazard_type` varchar(50) NOT NULL,
  `guideline_title` varchar(200) NOT NULL,
  `guideline_content` text NOT NULL,
  `priority_level` int(11) DEFAULT NULL,
  `category` varchar(50) DEFAULT NULL,
  `target_audience` varchar(100) DEFAULT NULL,
  `emergency_contact` varchar(100) DEFAULT NULL,
  `visual_aid_url` varchar(255) DEFAULT NULL,
  `language` varchar(20) DEFAULT 'English',
  `is_active` tinyint(1) DEFAULT 1,
  `created_by` int(11) DEFAULT NULL,
  `last_updated` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  PRIMARY KEY (`guideline_id`),
  KEY `idx_guideline_hazard` (`hazard_type`),
  KEY `idx_guideline_created_by` (`created_by`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Users Table (ENHANCED)
CREATE TABLE `users` (
  `user_id` int(11) NOT NULL AUTO_INCREMENT,
  `username` varchar(50) NOT NULL,
  `password_hash` varchar(255) NOT NULL,
  `full_name` varchar(100) DEFAULT NULL,
  `role` varchar(20) DEFAULT 'viewer',
  `email` varchar(100) DEFAULT NULL,
  `phone` varchar(20) DEFAULT NULL,
  `department` varchar(100) DEFAULT NULL,
  `is_active` tinyint(1) DEFAULT 1,
  `last_password_change` timestamp NULL DEFAULT NULL,
  `failed_login_attempts` int(11) DEFAULT 0,
  `account_locked_until` timestamp NULL DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
  `last_login` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `username` (`username`),
  UNIQUE KEY `email` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- NEW: User Activity Log Table
CREATE TABLE `user_activity` (
  `activity_id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL,
  `activity_type` varchar(50) NOT NULL,
  `activity_description` text DEFAULT NULL,
  `ip_address` varchar(45) DEFAULT NULL,
  `user_agent` varchar(255) DEFAULT NULL,
  `timestamp` timestamp NOT NULL DEFAULT current_timestamp(),
  PRIMARY KEY (`activity_id`),
  KEY `idx_activity_user` (`user_id`),
  KEY `idx_activity_type` (`activity_type`),
  KEY `idx_activity_timestamp` (`timestamp`),
  CONSTRAINT `user_activity_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- NEW: Audit Log Table
CREATE TABLE `audit_log` (
  `log_id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) DEFAULT NULL,
  `table_name` varchar(50) NOT NULL,
  `record_id` int(11) NOT NULL,
  `action` varchar(20) NOT NULL,
  `old_values` text DEFAULT NULL,
  `new_values` text DEFAULT NULL,
  `timestamp` timestamp NOT NULL DEFAULT current_timestamp(),
  PRIMARY KEY (`log_id`),
  KEY `idx_audit_user` (`user_id`),
  KEY `idx_audit_table` (`table_name`),
  KEY `idx_audit_timestamp` (`timestamp`),
  CONSTRAINT `audit_log_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Additional Indexes for Performance
CREATE INDEX `idx_hazard_type` ON `hazard_zones`(`hazard_type`);
CREATE INDEX `idx_hazard_severity` ON `hazard_zones`(`severity_level`);
CREATE INDEX `idx_landmarks_type` ON `landmarks`(`landmark_type`);
CREATE INDEX `idx_landmarks_evacuation` ON `landmarks`(`is_evacuation_site`);
CREATE INDEX `idx_incident_type` ON `incidents`(`incident_type`);
CREATE INDEX `idx_incident_severity` ON `incidents`(`severity`);

-- ORMOC CITY SAMPLE DATA (ENHANCED)

INSERT INTO `landmarks` (`landmark_name`, `landmark_type`, `address`, `barangay`, `latitude`, `longitude`, `contact_number`, `is_evacuation_site`, `capacity`, `facilities`, `accessibility_notes`) VALUES
('Ormoc City Hall', 'Government', 'Real Street', 'District 26', 11.00590000, 124.60750000, '(053) 561-4877', 0, NULL, NULL, 'Wheelchair accessible'),
('Ormoc District Hospital', 'Hospital', 'Bonifacio Street', 'District 8', 11.00640000, 124.60710000, '(053) 255-2316', 0, NULL, 'Emergency room, ICU, Laboratory', '24/7 emergency services'),
('Farmers Plaza', 'Commercial', 'San Pablo Street', 'District 24', 11.00430000, 124.60580000, NULL, 0, NULL, NULL, NULL),
('Ormoc City Superdome', 'Evacuation Center', 'Bagong Buhay', 'Bagong Buhay', 11.01250000, 124.61450000, NULL, 1, 2000, 'Toilets, kitchen, medical station, sleeping areas', 'Fully accessible, ground floor'),
('Divine Word College of Leyte', 'School', 'Bonifacio Street', 'District 8', 11.00750000, 124.60900000, '(053) 561-6951', 1, 500, 'Classrooms, water supply, toilets', 'Multi-story building'),
('St. Peter and Paul Cathedral', 'Religious', 'Real Street', 'District 26', 11.00550000, 124.60800000, NULL, 0, NULL, NULL, NULL),
('Lake Danao Natural Park', 'Park', 'Brgy. Lake Danao', 'Lake Danao', 11.05280000, 124.71690000, NULL, 0, NULL, 'Parking, picnic areas, lake', 'Limited accessibility'),
('Ormoc City Multi-Purpose Gymnasium', 'Evacuation Center', 'Rizal Avenue', 'District 1', 11.00900000, 124.61000000, NULL, 1, 1500, 'Basketball court, bleachers, toilets, storage', 'Ground floor accessible');

INSERT INTO `hazard_zones` (`zone_name`, `barangay`, `hazard_type`, `severity_level`, `latitude`, `longitude`, `radius_meters`, `description`, `affected_population`, `date_identified`, `mitigation_measures`, `early_warning_system`) VALUES
('Anilao-Malbasag River Basin', 'Anilao', 'Flood', 'Critical', 11.04500000, 124.62000000, 3000, 'High-risk flood zone. Scene of 1991 flash flood tragedy.', 8000, '1991-11-05', 'River dredging, early warning sirens, evacuation drills', 'Community-based warning system'),
('Bao River Corridor', 'Bao', 'Flood', 'High', 11.02500000, 124.61500000, 2500, 'Prone to flash floods during heavy rainfall.', 5000, '2011-06-15', 'Flood control structures, tree planting', 'Rain gauge monitoring'),
('Donghol River Area', 'Donghol', 'Landslide', 'High', 11.08000000, 124.64000000, 2000, 'Steep slopes along river banks. Landslide susceptible.', 3500, '2013-11-08', 'Slope stabilization, reforestation', 'Ground movement sensors'),
('Can-adieng Uplands', 'Can-adieng', 'Landslide', 'Critical', 11.09500000, 124.68500000, 1800, 'Mountainous terrain with history of landslides.', 2000, '2013-11-08', 'Retaining walls, drainage improvement', 'Visual inspection schedule'),
('Coastal Barangays Zone', 'Cogon Combado', 'Storm Surge', 'High', 11.03500000, 124.58500000, 4000, 'Coastal areas vulnerable to storm surge during typhoons.', 6500, '2013-11-08', 'Mangrove planting, seawall construction', 'Typhoon tracking system'),
('Downtown Commercial District', 'District 26', 'Fire', 'Medium', 11.00590000, 124.60750000, 800, 'Dense commercial area. Fire risk due to building proximity.', 4000, '2015-03-20', 'Fire hydrants, fire breaks, awareness campaigns', 'Fire detection system'),
('Alta Vista Subdivision', 'Alta Vista', 'Landslide', 'Medium', 11.02000000, 124.63000000, 1500, 'Hillside residential area. Soil erosion concerns.', 2500, '2016-07-12', 'Proper drainage, slope terracing', 'Regular monitoring');

INSERT INTO `incidents` (`zone_id`, `incident_type`, `incident_date`, `incident_time`, `barangay`, `severity`, `casualties`, `injuries`, `families_affected`, `structures_damaged`, `estimated_cost`, `description`, `response_actions`, `evacuation_conducted`, `evacuees_count`, `weather_conditions`) VALUES
(1, 'Flash Flood', '1991-11-05', '05:30:00', 'Anilao-Malbasag', 'Critical', 6000, 2000, 12000, 8000, 500000000.00, 'Tropical Storm Uring (Thelma). Devastating flash flood killed over 6,000 people.', 'Mass evacuation, rescue operations, international aid', 1, 15000, 'Heavy rainfall from tropical storm'),
(2, 'Flood', '2011-06-17', '14:00:00', 'Bao', 'High', 15, 45, 800, 350, 25000000.00, 'Heavy monsoon rains caused widespread flooding.', 'Evacuation to higher ground, relief distribution', 1, 1200, 'Continuous monsoon rain'),
(3, 'Typhoon/Landslide', '2013-11-08', '06:00:00', 'Donghol', 'Critical', 28, 67, 1200, 450, 80000000.00, 'Typhoon Yolanda (Haiyan) triggered multiple landslides.', 'Search and rescue, medical assistance, temporary shelters', 1, 2500, 'Super typhoon, extreme winds'),
(4, 'Landslide', '2013-11-08', '06:30:00', 'Can-adieng', 'Critical', 12, 18, 180, 75, 15000000.00, 'Massive landslide during Typhoon Yolanda.', 'Evacuation, debris clearing, casualty recovery', 1, 400, 'Heavy rain from typhoon'),
(5, 'Storm Surge', '2013-11-08', '07:00:00', 'Cogon Combado', 'High', 8, 34, 600, 280, 45000000.00, 'Storm surge from Typhoon Yolanda inundated coastal communities.', 'Rescue operations, emergency medical care', 1, 1500, 'Typhoon-induced storm surge'),
(1, 'Flood', '2017-12-22', '03:00:00', 'Anilao', 'High', 3, 15, 450, 120, 18000000.00, 'Flash flood during heavy rainfall.', 'Immediate evacuation, relief operations', 1, 800, 'Intense rainfall'),
(6, 'Fire', '2019-03-15', '11:00:00', 'District 26', 'Medium', 0, 5, 45, 32, 8500000.00, 'Market fire spread through wooden stalls.', 'Fire suppression, area cordoning', 0, 0, 'Clear, dry conditions'),
(2, 'Flood', '2020-08-10', '16:00:00', 'Bao', 'Medium', 0, 8, 320, 95, 12000000.00, 'Flooding from sustained rainfall.', 'Sandbagging, pumping operations', 1, 500, 'Prolonged rainfall'),
(7, 'Landslide', '2021-07-18', '02:00:00', 'Alta Vista', 'Medium', 1, 6, 28, 12, 4200000.00, 'Small landslide affected hillside homes.', 'Evacuation, slope assessment', 1, 50, 'Heavy overnight rain');

INSERT INTO `safety_guidelines` (`hazard_type`, `guideline_title`, `guideline_content`, `priority_level`, `category`, `target_audience`, `emergency_contact`) VALUES
('Flood', 'Flash Flood Warning', 'Move to higher ground immediately. Do not cross flowing water. Stay away from rivers during heavy rain.', 1, 'During', 'General Public', 'CDRRMO: (053) 561-5027'),
('Flood', 'Flood Preparation', 'Know evacuation routes. Prepare emergency kit (water, food, medicines, documents). Store important documents in waterproof containers. Monitor PAGASA alerts.', 2, 'Prevention', 'General Public', NULL),
('Flood', 'After Flood Safety', 'Avoid floodwater (may be contaminated). Check building safety before entering. Document damage for insurance. Boil water before drinking.', 3, 'After', 'General Public', NULL),
('Landslide', 'Landslide Warning Signs', 'Watch for: ground cracks, tilting trees/posts, sudden water flow changes, rumbling sounds, doors/windows sticking. Evacuate immediately if observed.', 1, 'Prevention', 'Residents in hillside areas', 'CDRRMO: (053) 561-5027'),
('Landslide', 'During Landslide Emergency', 'Move perpendicular to landslide direction (not up/downslope). Stay away from river valleys. Alert neighbors. Do not return until authorities clear area.', 1, 'During', 'General Public', 'Emergency: 911'),
('Storm Surge', 'Typhoon Storm Surge', 'Evacuate coastal areas when Category 3+ typhoon approaches. Move at least 1km inland or to elevated structures (3+ floors). Bring emergency supplies.', 1, 'During', 'Coastal residents', 'CDRRMO: (053) 561-5027'),
('Storm Surge', 'Storm Surge Preparation', 'Know storm surge risk zones. Identify evacuation routes and shelters. Prepare go-bag with essentials. Secure outdoor items.', 2, 'Prevention', 'Coastal residents', NULL),
('Fire', 'Fire Prevention', 'Install smoke detectors. Keep fire extinguishers accessible. Do not overload electrical outlets. Store flammable materials safely. Create fire escape plan.', 2, 'Prevention', 'General Public', 'Fire: (053) 561-2222'),
('Fire', 'During Fire', 'Evacuate immediately. Stay low under smoke. Feel doors before opening (heat check). Do not use elevators. Call fire department. Meet at designated meeting point.', 1, 'During', 'General Public', 'Fire: (053) 561-2222'),
('Typhoon', 'Typhoon Preparation', 'Monitor PAGASA updates. Secure loose outdoor items. Stock food, water, medicines for 3 days. Charge devices. Prepare flashlights. Know evacuation center location.', 2, 'Prevention', 'General Public', 'CDRRMO: (053) 561-5027'),
('Typhoon', 'During Typhoon', 'Stay indoors away from windows. Avoid using electrical appliances if flooded. Do not go outside until "all clear" announced. Listen to emergency radio.', 1, 'During', 'General Public', 'CDRRMO: (053) 561-5027');

INSERT INTO `users` (`username`, `password_hash`, `full_name`, `role`, `email`, `department`) VALUES
('admin', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Ormoc CDRRMO Admin', 'admin', 'admin@ormoc.gov.ph', 'City Disaster Risk Reduction and Management Office'),
('viewer', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Public Viewer', 'viewer', 'public@ormoc.gov.ph', NULL),
('analyst', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Risk Analyst', 'editor', 'analyst@ormoc.gov.ph', 'CDRRMO - Analysis Division');

COMMIT;