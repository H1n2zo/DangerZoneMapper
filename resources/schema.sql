-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Nov 03, 2025 at 01:56 PM
-- Server version: 10.4.32-MariaDB
-- PHP Version: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `dangerzone_ormoc`
--

-- --------------------------------------------------------

--
-- Table structure for table `audit_log`
--

CREATE TABLE `audit_log` (
  `log_id` int(11) NOT NULL,
  `user_id` int(11) DEFAULT NULL,
  `table_name` varchar(50) NOT NULL,
  `record_id` int(11) NOT NULL,
  `action` varchar(20) NOT NULL,
  `old_values` text DEFAULT NULL,
  `new_values` text DEFAULT NULL,
  `timestamp` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `hazard_zones`
--

CREATE TABLE `hazard_zones` (
  `zone_id` int(11) NOT NULL,
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
  `is_active` tinyint(1) DEFAULT 1
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `hazard_zones`
--

INSERT INTO `hazard_zones` (`zone_id`, `zone_name`, `barangay`, `hazard_type`, `severity_level`, `latitude`, `longitude`, `radius_meters`, `description`, `risk_factors`, `affected_population`, `mitigation_measures`, `early_warning_system`, `date_identified`, `last_assessment_date`, `next_assessment_date`, `assessment_notes`, `created_by`, `last_updated`, `is_active`) VALUES
(1, 'Anilao-Malbasag River Basin', 'Anilao', 'Flood', 'Critical', 11.04500000, 124.62000000, 3000, 'High-risk flood zone. Scene of 1991 flash flood tragedy.', NULL, 8000, 'River dredging, early warning sirens, evacuation drills', 'Community-based warning system', '1991-11-05', NULL, NULL, NULL, NULL, '2025-11-03 12:44:32', 1),
(2, 'Bao River Corridor', 'Bao', 'Flood', 'High', 11.02500000, 124.61500000, 2500, 'Prone to flash floods during heavy rainfall.', NULL, 5000, 'Flood control structures, tree planting', 'Rain gauge monitoring', '2011-06-15', NULL, NULL, NULL, NULL, '2025-11-03 12:44:32', 1),
(3, 'Donghol River Area', 'Donghol', 'Landslide', 'High', 11.08000000, 124.64000000, 2000, 'Steep slopes along river banks. Landslide susceptible.', NULL, 3500, 'Slope stabilization, reforestation', 'Ground movement sensors', '2013-11-08', NULL, NULL, NULL, NULL, '2025-11-03 12:44:32', 1),
(4, 'Can-adieng Uplands', 'Can-adieng', 'Landslide', 'Critical', 11.09500000, 124.68500000, 1800, 'Mountainous terrain with history of landslides.', NULL, 2000, 'Retaining walls, drainage improvement', 'Visual inspection schedule', '2013-11-08', NULL, NULL, NULL, NULL, '2025-11-03 12:44:32', 1),
(5, 'Coastal Barangays Zone', 'Cogon Combado', 'Storm Surge', 'High', 11.03500000, 124.58500000, 4000, 'Coastal areas vulnerable to storm surge during typhoons.', NULL, 6500, 'Mangrove planting, seawall construction', 'Typhoon tracking system', '2013-11-08', NULL, NULL, NULL, NULL, '2025-11-03 12:44:32', 1),
(6, 'Downtown Commercial District', 'District 26', 'Fire', 'Medium', 11.00590000, 124.60750000, 800, 'Dense commercial area. Fire risk due to building proximity.', NULL, 4000, 'Fire hydrants, fire breaks, awareness campaigns', 'Fire detection system', '2015-03-20', NULL, NULL, NULL, NULL, '2025-11-03 12:44:32', 1),
(7, 'Alta Vista Subdivision', 'Alta Vista', 'Landslide', 'Medium', 11.02000000, 124.63000000, 1500, 'Hillside residential area. Soil erosion concerns.', NULL, 2500, 'Proper drainage, slope terracing', 'Regular monitoring', '2016-07-12', NULL, NULL, NULL, NULL, '2025-11-03 12:44:32', 1);

-- --------------------------------------------------------

--
-- Table structure for table `incidents`
--

CREATE TABLE `incidents` (
  `incident_id` int(11) NOT NULL,
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
  `last_updated` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `incidents`
--

INSERT INTO `incidents` (`incident_id`, `zone_id`, `incident_type`, `incident_date`, `incident_time`, `barangay`, `severity`, `casualties`, `injuries`, `missing`, `families_affected`, `structures_damaged`, `estimated_cost`, `description`, `response_actions`, `response_time_minutes`, `evacuation_conducted`, `evacuees_count`, `lessons_learned`, `latitude`, `longitude`, `weather_conditions`, `created_by`, `created_at`, `last_updated`) VALUES
(1, 1, 'Flash Flood', '1991-11-05', '05:30:00', 'Anilao-Malbasag', 'Critical', 6000, 2000, 0, 12000, 8000, 500000000.00, 'Tropical Storm Uring (Thelma). Devastating flash flood killed over 6,000 people.', 'Mass evacuation, rescue operations, international aid', NULL, 1, 15000, NULL, NULL, NULL, 'Heavy rainfall from tropical storm', NULL, '2025-11-03 12:44:32', '2025-11-03 12:44:32'),
(2, 2, 'Flood', '2011-06-17', '14:00:00', 'Bao', 'High', 15, 45, 0, 800, 350, 25000000.00, 'Heavy monsoon rains caused widespread flooding.', 'Evacuation to higher ground, relief distribution', NULL, 1, 1200, NULL, NULL, NULL, 'Continuous monsoon rain', NULL, '2025-11-03 12:44:32', '2025-11-03 12:44:32'),
(3, 3, 'Typhoon/Landslide', '2013-11-08', '06:00:00', 'Donghol', 'Critical', 28, 67, 0, 1200, 450, 80000000.00, 'Typhoon Yolanda (Haiyan) triggered multiple landslides.', 'Search and rescue, medical assistance, temporary shelters', NULL, 1, 2500, NULL, NULL, NULL, 'Super typhoon, extreme winds', NULL, '2025-11-03 12:44:32', '2025-11-03 12:44:32'),
(4, 4, 'Landslide', '2013-11-08', '06:30:00', 'Can-adieng', 'Critical', 12, 18, 0, 180, 75, 15000000.00, 'Massive landslide during Typhoon Yolanda.', 'Evacuation, debris clearing, casualty recovery', NULL, 1, 400, NULL, NULL, NULL, 'Heavy rain from typhoon', NULL, '2025-11-03 12:44:32', '2025-11-03 12:44:32'),
(5, 5, 'Storm Surge', '2013-11-08', '07:00:00', 'Cogon Combado', 'High', 8, 34, 0, 600, 280, 45000000.00, 'Storm surge from Typhoon Yolanda inundated coastal communities.', 'Rescue operations, emergency medical care', NULL, 1, 1500, NULL, NULL, NULL, 'Typhoon-induced storm surge', NULL, '2025-11-03 12:44:32', '2025-11-03 12:44:32'),
(6, 1, 'Flood', '2017-12-22', '03:00:00', 'Anilao', 'High', 3, 15, 0, 450, 120, 18000000.00, 'Flash flood during heavy rainfall.', 'Immediate evacuation, relief operations', NULL, 1, 800, NULL, NULL, NULL, 'Intense rainfall', NULL, '2025-11-03 12:44:32', '2025-11-03 12:44:32'),
(7, 6, 'Fire', '2019-03-15', '11:00:00', 'District 26', 'Medium', 0, 5, 0, 45, 32, 8500000.00, 'Market fire spread through wooden stalls.', 'Fire suppression, area cordoning', NULL, 0, 0, NULL, NULL, NULL, 'Clear, dry conditions', NULL, '2025-11-03 12:44:32', '2025-11-03 12:44:32'),
(8, 2, 'Flood', '2020-08-10', '16:00:00', 'Bao', 'Medium', 0, 8, 0, 320, 95, 12000000.00, 'Flooding from sustained rainfall.', 'Sandbagging, pumping operations', NULL, 1, 500, NULL, NULL, NULL, 'Prolonged rainfall', NULL, '2025-11-03 12:44:32', '2025-11-03 12:44:32'),
(9, 7, 'Landslide', '2021-07-18', '02:00:00', 'Alta Vista', 'Medium', 1, 6, 0, 28, 12, 4200000.00, 'Small landslide affected hillside homes.', 'Evacuation, slope assessment', NULL, 1, 50, NULL, NULL, NULL, 'Heavy overnight rain', NULL, '2025-11-03 12:44:32', '2025-11-03 12:44:32');

-- --------------------------------------------------------

--
-- Table structure for table `landmarks`
--

CREATE TABLE `landmarks` (
  `landmark_id` int(11) NOT NULL,
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
  `last_updated` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `landmarks`
--

INSERT INTO `landmarks` (`landmark_id`, `landmark_name`, `landmark_type`, `address`, `barangay`, `latitude`, `longitude`, `contact_number`, `capacity`, `description`, `operating_hours`, `is_evacuation_site`, `is_active`, `facilities`, `accessibility_notes`, `created_by`, `created_at`, `last_updated`) VALUES
(1, 'Ormoc City Hall', 'Government', 'Real Street', 'District 26', 11.00590000, 124.60750000, '(053) 561-4877', NULL, NULL, NULL, 0, 1, NULL, 'Wheelchair accessible', NULL, '2025-11-03 12:44:32', '2025-11-03 12:44:32'),
(2, 'Ormoc District Hospital', 'Hospital', 'Bonifacio Street', 'District 8', 11.00640000, 124.60710000, '(053) 255-2316', NULL, NULL, NULL, 0, 1, 'Emergency room, ICU, Laboratory', '24/7 emergency services', NULL, '2025-11-03 12:44:32', '2025-11-03 12:44:32'),
(3, 'Farmers Plaza', 'Commercial', 'San Pablo Street', 'District 24', 11.00430000, 124.60580000, NULL, NULL, NULL, NULL, 0, 1, NULL, NULL, NULL, '2025-11-03 12:44:32', '2025-11-03 12:44:32'),
(4, 'Ormoc City Superdome', 'Evacuation Center', 'Bagong Buhay', 'Bagong Buhay', 11.01250000, 124.61450000, NULL, 2000, NULL, NULL, 1, 1, 'Toilets, kitchen, medical station, sleeping areas', 'Fully accessible, ground floor', NULL, '2025-11-03 12:44:32', '2025-11-03 12:44:32'),
(5, 'Divine Word College of Leyte', 'School', 'Bonifacio Street', 'District 8', 11.00750000, 124.60900000, '(053) 561-6951', 500, NULL, NULL, 1, 1, 'Classrooms, water supply, toilets', 'Multi-story building', NULL, '2025-11-03 12:44:32', '2025-11-03 12:44:32'),
(6, 'St. Peter and Paul Cathedral', 'Religious', 'Real Street', 'District 26', 11.00550000, 124.60800000, NULL, NULL, NULL, NULL, 0, 1, NULL, NULL, NULL, '2025-11-03 12:44:32', '2025-11-03 12:44:32'),
(7, 'Lake Danao Natural Park', 'Park', 'Brgy. Lake Danao', 'Lake Danao', 11.05280000, 124.71690000, NULL, NULL, NULL, NULL, 0, 1, 'Parking, picnic areas, lake', 'Limited accessibility', NULL, '2025-11-03 12:44:32', '2025-11-03 12:44:32'),
(8, 'Ormoc City Multi-Purpose Gymnasium', 'Evacuation Center', 'Rizal Avenue', 'District 1', 11.00900000, 124.61000000, NULL, 1500, NULL, NULL, 1, 1, 'Basketball court, bleachers, toilets, storage', 'Ground floor accessible', NULL, '2025-11-03 12:44:32', '2025-11-03 12:44:32');

-- --------------------------------------------------------

--
-- Table structure for table `maps`
--

CREATE TABLE `maps` (
  `map_id` int(11) NOT NULL,
  `map_name` varchar(100) NOT NULL,
  `map_type` varchar(50) NOT NULL COMMENT 'e.g., hazard_map, evacuation_routes, base_map',
  `map_description` text DEFAULT NULL,
  `file_path` varchar(255) NOT NULL COMMENT 'Path to image file or URL',
  `file_size_kb` int(11) DEFAULT NULL,
  `image_width` int(11) DEFAULT NULL,
  `image_height` int(11) DEFAULT NULL,
  `coverage_area` varchar(100) DEFAULT NULL COMMENT 'e.g., Ormoc City, Specific Barangay',
  `map_scale` varchar(50) DEFAULT NULL,
  `creation_date` date DEFAULT NULL,
  `source` varchar(200) DEFAULT NULL COMMENT 'Map source/creator',
  `is_active` tinyint(1) DEFAULT 1,
  `is_default` tinyint(1) DEFAULT 0 COMMENT 'Default map to display',
  `display_order` int(11) DEFAULT 0,
  `created_by` int(11) DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
  `last_updated` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `maps`
--

INSERT INTO `maps` (`map_id`, `map_name`, `map_type`, `map_description`, `file_path`, `file_size_kb`, `image_width`, `image_height`, `coverage_area`, `map_scale`, `creation_date`, `source`, `is_active`, `is_default`, `display_order`, `created_by`, `created_at`, `last_updated`) VALUES
(1, 'Ormoc City Hazard Map', 'hazard_map', 'Official hazard map showing flood zones, landslide areas, and evacuation centers', '/resources/ormoc_map.png', NULL, NULL, NULL, 'Ormoc City', NULL, NULL, 'CDRRMO Ormoc City', 1, 1, 1, NULL, '2025-11-03 12:44:32', '2025-11-03 12:52:47');

-- --------------------------------------------------------

--
-- Table structure for table `safety_guidelines`
--

CREATE TABLE `safety_guidelines` (
  `guideline_id` int(11) NOT NULL,
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
  `last_updated` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `safety_guidelines`
--

INSERT INTO `safety_guidelines` (`guideline_id`, `hazard_type`, `guideline_title`, `guideline_content`, `priority_level`, `category`, `target_audience`, `emergency_contact`, `visual_aid_url`, `language`, `is_active`, `created_by`, `last_updated`) VALUES
(1, 'Flood', 'Flash Flood Warning', 'Move to higher ground immediately. Do not cross flowing water. Stay away from rivers during heavy rain.', 1, 'During', 'General Public', 'CDRRMO: (053) 561-5027', NULL, 'English', 1, NULL, '2025-11-03 12:44:32'),
(2, 'Flood', 'Flood Preparation', 'Know evacuation routes. Prepare emergency kit (water, food, medicines, documents). Store important documents in waterproof containers. Monitor PAGASA alerts.', 2, 'Prevention', 'General Public', NULL, NULL, 'English', 1, NULL, '2025-11-03 12:44:32'),
(3, 'Flood', 'After Flood Safety', 'Avoid floodwater (may be contaminated). Check building safety before entering. Document damage for insurance. Boil water before drinking.', 3, 'After', 'General Public', NULL, NULL, 'English', 1, NULL, '2025-11-03 12:44:32'),
(4, 'Landslide', 'Landslide Warning Signs', 'Watch for: ground cracks, tilting trees/posts, sudden water flow changes, rumbling sounds, doors/windows sticking. Evacuate immediately if observed.', 1, 'Prevention', 'Residents in hillside areas', 'CDRRMO: (053) 561-5027', NULL, 'English', 1, NULL, '2025-11-03 12:44:32'),
(5, 'Landslide', 'During Landslide Emergency', 'Move perpendicular to landslide direction (not up/downslope). Stay away from river valleys. Alert neighbors. Do not return until authorities clear area.', 1, 'During', 'General Public', 'Emergency: 911', NULL, 'English', 1, NULL, '2025-11-03 12:44:32'),
(6, 'Storm Surge', 'Typhoon Storm Surge', 'Evacuate coastal areas when Category 3+ typhoon approaches. Move at least 1km inland or to elevated structures (3+ floors). Bring emergency supplies.', 1, 'During', 'Coastal residents', 'CDRRMO: (053) 561-5027', NULL, 'English', 1, NULL, '2025-11-03 12:44:32'),
(7, 'Storm Surge', 'Storm Surge Preparation', 'Know storm surge risk zones. Identify evacuation routes and shelters. Prepare go-bag with essentials. Secure outdoor items.', 2, 'Prevention', 'Coastal residents', NULL, NULL, 'English', 1, NULL, '2025-11-03 12:44:32'),
(8, 'Fire', 'Fire Prevention', 'Install smoke detectors. Keep fire extinguishers accessible. Do not overload electrical outlets. Store flammable materials safely. Create fire escape plan.', 2, 'Prevention', 'General Public', 'Fire: (053) 561-2222', NULL, 'English', 1, NULL, '2025-11-03 12:44:32'),
(9, 'Fire', 'During Fire', 'Evacuate immediately. Stay low under smoke. Feel doors before opening (heat check). Do not use elevators. Call fire department. Meet at designated meeting point.', 1, 'During', 'General Public', 'Fire: (053) 561-2222', NULL, 'English', 1, NULL, '2025-11-03 12:44:32'),
(10, 'Typhoon', 'Typhoon Preparation', 'Monitor PAGASA updates. Secure loose outdoor items. Stock food, water, medicines for 3 days. Charge devices. Prepare flashlights. Know evacuation center location.', 2, 'Prevention', 'General Public', 'CDRRMO: (053) 561-5027', NULL, 'English', 1, NULL, '2025-11-03 12:44:32'),
(11, 'Typhoon', 'During Typhoon', 'Stay indoors away from windows. Avoid using electrical appliances if flooded. Do not go outside until \"all clear\" announced. Listen to emergency radio.', 1, 'During', 'General Public', 'CDRRMO: (053) 561-5027', NULL, 'English', 1, NULL, '2025-11-03 12:44:32');

-- --------------------------------------------------------

--
-- Table structure for table `users`
--

CREATE TABLE `users` (
  `user_id` int(11) NOT NULL,
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
  `last_login` timestamp NULL DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `users`
--

INSERT INTO `users` (`user_id`, `username`, `password_hash`, `full_name`, `role`, `email`, `phone`, `department`, `is_active`, `last_password_change`, `failed_login_attempts`, `account_locked_until`, `created_at`, `last_login`) VALUES
(1, 'admin', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Ormoc CDRRMO Admin', 'admin', 'admin@ormoc.gov.ph', NULL, 'City Disaster Risk Reduction and Management Office', 1, NULL, 0, NULL, '2025-11-03 12:44:32', NULL),
(2, 'viewer', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Public Viewer', 'viewer', 'public@ormoc.gov.ph', NULL, NULL, 1, NULL, 0, NULL, '2025-11-03 12:44:32', NULL),
(3, 'analyst', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Risk Analyst', 'editor', 'analyst@ormoc.gov.ph', NULL, 'CDRRMO - Analysis Division', 1, NULL, 0, NULL, '2025-11-03 12:44:32', NULL);

-- --------------------------------------------------------

--
-- Table structure for table `user_activity`
--

CREATE TABLE `user_activity` (
  `activity_id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `activity_type` varchar(50) NOT NULL,
  `activity_description` text DEFAULT NULL,
  `ip_address` varchar(45) DEFAULT NULL,
  `user_agent` varchar(255) DEFAULT NULL,
  `timestamp` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Indexes for dumped tables
--

--
-- Indexes for table `audit_log`
--
ALTER TABLE `audit_log`
  ADD PRIMARY KEY (`log_id`),
  ADD KEY `idx_audit_user` (`user_id`),
  ADD KEY `idx_audit_table` (`table_name`),
  ADD KEY `idx_audit_timestamp` (`timestamp`);

--
-- Indexes for table `hazard_zones`
--
ALTER TABLE `hazard_zones`
  ADD PRIMARY KEY (`zone_id`),
  ADD KEY `idx_hazard_barangay` (`barangay`),
  ADD KEY `idx_hazard_created_by` (`created_by`),
  ADD KEY `idx_hazard_type` (`hazard_type`),
  ADD KEY `idx_hazard_severity` (`severity_level`);

--
-- Indexes for table `incidents`
--
ALTER TABLE `incidents`
  ADD PRIMARY KEY (`incident_id`),
  ADD KEY `zone_id` (`zone_id`),
  ADD KEY `idx_incident_date` (`incident_date`),
  ADD KEY `idx_incident_barangay` (`barangay`),
  ADD KEY `idx_incident_created_by` (`created_by`),
  ADD KEY `idx_incident_type` (`incident_type`),
  ADD KEY `idx_incident_severity` (`severity`);

--
-- Indexes for table `landmarks`
--
ALTER TABLE `landmarks`
  ADD PRIMARY KEY (`landmark_id`),
  ADD KEY `idx_landmarks_barangay` (`barangay`),
  ADD KEY `idx_landmarks_created_by` (`created_by`),
  ADD KEY `idx_landmarks_type` (`landmark_type`),
  ADD KEY `idx_landmarks_evacuation` (`is_evacuation_site`);

--
-- Indexes for table `maps`
--
ALTER TABLE `maps`
  ADD PRIMARY KEY (`map_id`),
  ADD KEY `idx_maps_type` (`map_type`),
  ADD KEY `idx_maps_active` (`is_active`),
  ADD KEY `idx_maps_default` (`is_default`),
  ADD KEY `idx_maps_created_by` (`created_by`);

--
-- Indexes for table `safety_guidelines`
--
ALTER TABLE `safety_guidelines`
  ADD PRIMARY KEY (`guideline_id`),
  ADD KEY `idx_guideline_hazard` (`hazard_type`),
  ADD KEY `idx_guideline_created_by` (`created_by`);

--
-- Indexes for table `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`user_id`),
  ADD UNIQUE KEY `username` (`username`),
  ADD UNIQUE KEY `email` (`email`);

--
-- Indexes for table `user_activity`
--
ALTER TABLE `user_activity`
  ADD PRIMARY KEY (`activity_id`),
  ADD KEY `idx_activity_user` (`user_id`),
  ADD KEY `idx_activity_type` (`activity_type`),
  ADD KEY `idx_activity_timestamp` (`timestamp`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `audit_log`
--
ALTER TABLE `audit_log`
  MODIFY `log_id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `hazard_zones`
--
ALTER TABLE `hazard_zones`
  MODIFY `zone_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=8;

--
-- AUTO_INCREMENT for table `incidents`
--
ALTER TABLE `incidents`
  MODIFY `incident_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=10;

--
-- AUTO_INCREMENT for table `landmarks`
--
ALTER TABLE `landmarks`
  MODIFY `landmark_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=9;

--
-- AUTO_INCREMENT for table `maps`
--
ALTER TABLE `maps`
  MODIFY `map_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT for table `safety_guidelines`
--
ALTER TABLE `safety_guidelines`
  MODIFY `guideline_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=12;

--
-- AUTO_INCREMENT for table `users`
--
ALTER TABLE `users`
  MODIFY `user_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT for table `user_activity`
--
ALTER TABLE `user_activity`
  MODIFY `activity_id` int(11) NOT NULL AUTO_INCREMENT;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `audit_log`
--
ALTER TABLE `audit_log`
  ADD CONSTRAINT `audit_log_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE SET NULL;

--
-- Constraints for table `incidents`
--
ALTER TABLE `incidents`
  ADD CONSTRAINT `incidents_ibfk_1` FOREIGN KEY (`zone_id`) REFERENCES `hazard_zones` (`zone_id`) ON DELETE SET NULL;

--
-- Constraints for table `user_activity`
--
ALTER TABLE `user_activity`
  ADD CONSTRAINT `user_activity_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
