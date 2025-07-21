-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Jul 20, 2025 at 06:55 PM
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
-- Database: `airline_db`
--

-- --------------------------------------------------------

--
-- Table structure for table `admins`
--

CREATE TABLE `admins` (
  `admin_id` int(11) NOT NULL,
  `username` varchar(20) NOT NULL,
  `password` varchar(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `admins`
--

INSERT INTO `admins` (`admin_id`, `username`, `password`) VALUES
(1, 'DOMESTIC', 'DMST@01'),
(2, 'INTERNATIONAL', 'INRNL@02');

-- --------------------------------------------------------

--
-- Table structure for table `flights`
--

CREATE TABLE `flights` (
  `flight_id` int(11) NOT NULL,
  `flight_number` varchar(10) NOT NULL,
  `flight_type` varchar(10) NOT NULL,
  `departure` varchar(50) NOT NULL,
  `destination` varchar(50) NOT NULL,
  `departure_time` datetime NOT NULL,
  `arrival_time` datetime NOT NULL,
  `total_seats` int(11) NOT NULL,
  `available_seats` int(11) NOT NULL,
  `price` decimal(10,2) NOT NULL,
  `admin_id` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `flights`
--

INSERT INTO `flights` (`flight_id`, `flight_number`, `flight_type`, `departure`, `destination`, `departure_time`, `arrival_time`, `total_seats`, `available_seats`, `price`, `admin_id`) VALUES
(1, 'SG160', 'DMST', 'AHMEDABAD', 'DELHI', '2025-07-21 02:00:00', '2025-07-21 03:45:00', 80, 80, 4400.00, 1),
(2, 'SG9232', 'DMST', 'AHMEDABAD', 'DELHI', '2025-07-22 06:10:00', '2025-07-22 07:45:00', 75, 75, 3500.00, 1),
(3, 'SG163', 'DMST', 'DELHI', 'AHMEDABAD', '2025-07-21 20:50:00', '2025-07-21 22:40:00', 70, 70, 4205.00, 1),
(4, 'SG9213', 'DMST', 'DELHI', 'AHMEDABAD', '2025-07-22 12:30:00', '2025-07-22 22:40:00', 75, 75, 3400.00, 1),
(5, 'SG1081', 'DMST', 'AHMEDABAD', 'MUMBAI', '2025-07-21 09:30:00', '2025-07-21 10:50:00', 70, 70, 3100.00, 1),
(6, 'SG1082', 'DMST', 'MUMBAI', 'AHMEDABAD', '2025-07-22 15:25:00', '2025-07-22 16:55:00', 80, 80, 3500.00, 1),
(7, 'SG15', 'INRNL', 'AHMEDABAD', 'DUBAI', '2025-07-22 16:35:00', '2025-07-22 18:25:00', 160, 160, 10450.00, 2),
(8, 'SG16', 'INRNL', 'DUBAI', 'AHMEDABAD', '2025-07-22 19:25:00', '2025-07-22 23:45:00', 165, 165, 11045.00, 2),
(9, 'AI810', 'INRNL', 'AHMEDABAD', 'TORONTO', '2025-07-22 20:20:00', '2025-07-23 10:50:00', 307, 307, 103541.00, 2),
(10, 'AI188', 'INRNL', 'TORONTO', 'AHMEDABAD', '2025-07-23 13:10:00', '2025-07-24 19:40:00', 310, 310, 118128.00, 2),
(11, 'AI2494', 'INRNL', 'AHMEDABAD', 'NEW YORK', '2025-07-23 20:30:00', '2025-07-24 07:55:00', 310, 310, 134520.00, 2),
(12, 'AI144', 'INRNL', 'NEW YORK', 'AHMEDABAD', '2025-07-23 11:30:00', '2025-07-24 16:25:00', 305, 305, 174909.00, 2);

-- --------------------------------------------------------

--
-- Table structure for table `passengers`
--

CREATE TABLE `passengers` (
  `passenger_id` int(11) NOT NULL,
  `name` varchar(100) NOT NULL,
  `email` varchar(100) NOT NULL,
  `phone` varchar(15) NOT NULL,
  `password` varchar(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `passengers`
--

INSERT INTO `passengers` (`passenger_id`, `name`, `email`, `phone`, `password`) VALUES
(1, 'Rajesh Sharma', 'rajesh.sharma@example.com', '9876543210', 'rajesh@1'),
(2, 'Priya Patel', 'priya.patel@example.com', '8765432109', 'priya#12'),
(3, 'Amit Kumar', 'amit.k@example.com', '7654321098', 'amit987'),
(4, 'Ananya Singh', 'ananya.s@example.com', '8989765432', 'ananya#'),
(5, 'Vikram Reddy', 'vikram.reddy@example.com', '7890123456', 'vikram#'),
(6, 'Deepika Iyer', 'deepika.iyer@example.com', '9012345678', 'deepi123'),
(7, 'Arjun Mehta', 'arjun.mehta@example.com', '8123456789', 'arjun@m'),
(8, 'Sneha Gupta', 'sneha.g@example.com', '9988776655', 'sneha09'),
(9, 'Rahul Desai', 'rahul.desai@example.com', '7778889990', 'desai@r'),
(10, 'Neha Choudhury', 'neha.c@example.com', '9876501234', 'nehac@1'),
(11, 'Sanjay Verma', 'sanjay.verma@example.com', '8899776600', 'sverma#88'),
(23, 'Ansh Gajera', 'ansh.gajera@gmail.com', '9875181452', 'ansh@4227'),
(24, 'Keyan Patel', 'keyan.j@outlook.com', '6427555193', 'keyzorr_18');

-- --------------------------------------------------------

--
-- Table structure for table `reports`
--

CREATE TABLE `reports` (
  `report_id` int(11) NOT NULL,
  `flight_id` int(11) NOT NULL,
  `report_date` date NOT NULL,
  `revenue` decimal(10,2) NOT NULL,
  `occupancy_rate` decimal(5,2) NOT NULL,
  `passenger_count` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `reservations`
--

CREATE TABLE `reservations` (
  `reservation_id` int(11) NOT NULL,
  `flight_id` int(11) NOT NULL,
  `passenger_id` int(11) NOT NULL,
  `seat_number` varchar(5) NOT NULL,
  `reservation_date` datetime DEFAULT current_timestamp(),
  `status` enum('CONFIRMED','CANCELLED') DEFAULT 'CONFIRMED'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Indexes for dumped tables
--

--
-- Indexes for table `admins`
--
ALTER TABLE `admins`
  ADD PRIMARY KEY (`admin_id`),
  ADD UNIQUE KEY `username` (`username`);

--
-- Indexes for table `flights`
--
ALTER TABLE `flights`
  ADD PRIMARY KEY (`flight_id`),
  ADD KEY `admin_id` (`admin_id`);

--
-- Indexes for table `passengers`
--
ALTER TABLE `passengers`
  ADD PRIMARY KEY (`passenger_id`),
  ADD UNIQUE KEY `email` (`email`);

--
-- Indexes for table `reports`
--
ALTER TABLE `reports`
  ADD PRIMARY KEY (`report_id`),
  ADD KEY `flight_id` (`flight_id`);

--
-- Indexes for table `reservations`
--
ALTER TABLE `reservations`
  ADD PRIMARY KEY (`reservation_id`),
  ADD UNIQUE KEY `flight_id` (`flight_id`,`seat_number`),
  ADD KEY `passenger_id` (`passenger_id`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `admins`
--
ALTER TABLE `admins`
  MODIFY `admin_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT for table `flights`
--
ALTER TABLE `flights`
  MODIFY `flight_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=13;

--
-- AUTO_INCREMENT for table `passengers`
--
ALTER TABLE `passengers`
  MODIFY `passenger_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=25;

--
-- AUTO_INCREMENT for table `reports`
--
ALTER TABLE `reports`
  MODIFY `report_id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `reservations`
--
ALTER TABLE `reservations`
  MODIFY `reservation_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=21;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `flights`
--
ALTER TABLE `flights`
  ADD CONSTRAINT `flights_ibfk_1` FOREIGN KEY (`admin_id`) REFERENCES `admins` (`admin_id`);

--
-- Constraints for table `reports`
--
ALTER TABLE `reports`
  ADD CONSTRAINT `reports_ibfk_1` FOREIGN KEY (`flight_id`) REFERENCES `flights` (`flight_id`);

--
-- Constraints for table `reservations`
--
ALTER TABLE `reservations`
  ADD CONSTRAINT `reservations_ibfk_1` FOREIGN KEY (`flight_id`) REFERENCES `flights` (`flight_id`),
  ADD CONSTRAINT `reservations_ibfk_2` FOREIGN KEY (`passenger_id`) REFERENCES `passengers` (`passenger_id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
