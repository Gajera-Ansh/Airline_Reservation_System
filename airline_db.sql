-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Aug 23, 2025 at 04:39 PM
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
CREATE DATABASE IF NOT EXISTS `airline_db` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE `airline_db`;

DELIMITER $$
--
-- Procedures
--
CREATE DEFINER=`root`@`localhost` PROCEDURE `checkFlight` (IN `user_departure_city` VARCHAR(50), IN `user_destination_city` VARCHAR(50), IN `user_departure_time` DATETIME, IN `user_arrival_time` DATETIME, OUT `FlightNum` VARCHAR(10))   BEGIN
    -- Initialize output
    SET FlightNum = NULL;

    -- Check for conflicts at departure city
    SELECT flight_number
    INTO FlightNum
    FROM flights
    WHERE 
        (departure = user_departure_city OR destination = user_departure_city)
        AND (
            (
                DATE(user_departure_time) = DATE(departure_time)
                AND departure_time BETWEEN DATE_SUB(user_departure_time, INTERVAL 10 MINUTE)
                                      AND DATE_ADD(user_departure_time, INTERVAL 10 MINUTE)
            )
            OR
            (
                DATE(user_departure_time) = DATE(arrival_time)
                AND arrival_time BETWEEN DATE_SUB(user_departure_time, INTERVAL 10 MINUTE)
                                    AND DATE_ADD(user_departure_time, INTERVAL 10 MINUTE)
            )
        )
    LIMIT 1;

    -- If not found in departure check, try destination city
    IF FlightNum IS NULL THEN
        SELECT flight_number
        INTO FlightNum
        FROM flights
        WHERE 
            (departure = user_destination_city OR destination = user_destination_city)
            AND (
                (
                    DATE(user_arrival_time) = DATE(departure_time)
                    AND departure_time BETWEEN DATE_SUB(user_arrival_time, INTERVAL 10 MINUTE)
                                          AND DATE_ADD(user_arrival_time, INTERVAL 10 MINUTE)
                )
                OR
                (
                    DATE(user_arrival_time) = DATE(arrival_time)
                    AND arrival_time BETWEEN DATE_SUB(user_arrival_time, INTERVAL 10 MINUTE)
                                        AND DATE_ADD(user_arrival_time, INTERVAL 10 MINUTE)
                )
            )
        LIMIT 1;
    END IF;

END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `getFlight` (IN `flightId` INT(11), OUT `flightNum` VARCHAR(10), OUT `flightDep` VARCHAR(50), OUT `flightDes` VARCHAR(50), OUT `flightDepTime` DATETIME, OUT `flightDesTime` DATETIME)   BEGIN
	SELECT flight_number, departure, destination, departure_time, arrival_time INTO 
    flightNum, flightDep, flightDes, flightDepTime, flightDesTime FROM flights WHERE flight_id = flightId;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `getPassenger` (IN `passId` INT(11), OUT `passName` VARCHAR(50), OUT `passEmail` VARCHAR(100), OUT `passPhone` VARCHAR(10))   BEGIN
	SELECT name, email, phone INTO passName, passEmail, passPhone 
    FROM passengers INNER JOIN reservations ON reservations.passenger_id = passengers.passenger_id 
    WHERE passengers.passenger_id = passId AND reservations.status = 'CONFIRMED' LIMIT 1;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `getPayment` (IN `flightId` INT(11), IN `passId` INT(11), OUT `bill` DECIMAL(10,2))   BEGIN
	SELECT SUM(amount) INTO bill FROM payments
    WHERE passenger_id = passId AND flight_id = flightId AND payments.status = 'CONFIRMED';
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `updateFlight` (IN `id` INT(10), IN `newflightNum` VARCHAR(10), IN `newdep_time` DATETIME, IN `newarr_time` DATETIME, IN `newtotal_seat` INT(10), IN `newava_seat` INT(10), IN `newPrice` DOUBLE)   BEGIN
	UPDATE flights SET flight_number = newflightNum, departure_time = newdep_time, arrival_time = newarr_time,
    total_seats = newtotal_seat, available_seats = newava_seat, price = newPrice WHERE flight_id = id;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `updateForRemoveFlight` (IN `flightId` INT(11))   BEGIN
	-- 1. Drop foreign keys 
ALTER TABLE payments DROP FOREIGN KEY payments_ibfk_2;
ALTER TABLE reservations DROP FOREIGN KEY reservations_ibfk_1;
ALTER TABLE reports DROP FOREIGN KEY reports_ibfk_1;

-- 2. Delete related records
DELETE FROM payments WHERE flight_id = flightId;
DELETE FROM reservations WHERE flight_id = flightId;
DELETE FROM reports WHERE flight_id = flightId;

-- 3. Delete the flight
DELETE FROM flights WHERE flight_id = flightId;

-- 4. Re-add foreign keys with ON DELETE CASCADE (optional)
ALTER TABLE payments
  ADD CONSTRAINT payments_ibfk_2
  FOREIGN KEY (flight_id) REFERENCES flights(flight_id) ON DELETE CASCADE ;

ALTER TABLE reservations
  ADD CONSTRAINT reservations_ibfk_1
  FOREIGN KEY (flight_id) REFERENCES flights(flight_id) ON DELETE CASCADE ;

ALTER TABLE reports
  ADD CONSTRAINT reports_ibfk_1
  FOREIGN KEY (flight_id) REFERENCES flights(flight_id) ON DELETE CASCADE ;
END$$

DELIMITER ;

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
(1, 'SG160', 'DMST', 'AHMEDABAD', 'DELHI', '2025-08-24 01:59:00', '2025-08-24 03:43:00', 85, 80, 4000.00, 1),
(2, 'SG9232', 'DMST', 'AHMEDABAD', 'DELHI', '2025-08-25 06:10:00', '2025-08-25 07:45:00', 75, 71, 3500.00, 1),
(3, 'SG163', 'DMST', 'DELHI', 'AHMEDABAD', '2025-08-24 20:50:00', '2025-08-24 22:40:00', 70, 70, 4205.00, 1),
(4, 'SG9213', 'DMST', 'DELHI', 'AHMEDABAD', '2025-08-25 12:30:00', '2025-08-25 22:40:00', 75, 75, 3400.00, 1),
(5, 'SG1081', 'DMST', 'AHMEDABAD', 'MUMBAI', '2025-08-24 09:30:00', '2025-08-24 10:50:00', 70, 70, 3100.00, 1),
(6, 'SG1082', 'DMST', 'MUMBAI', 'AHMEDABAD', '2025-08-25 15:25:00', '2025-08-25 16:55:00', 80, 77, 3500.00, 1),
(7, 'SG15', 'INRNL', 'AHMEDABAD', 'DUBAI', '2025-08-25 16:35:00', '2025-08-25 20:25:00', 160, 160, 10450.00, 2),
(8, 'SG16', 'INRNL', 'DUBAI', 'AHMEDABAD', '2025-08-25 19:25:00', '2025-08-25 23:45:00', 165, 165, 11045.00, 2),
(9, 'AI810', 'INRNL', 'AHMEDABAD', 'TORONTO', '2025-08-25 20:20:00', '2025-08-26 10:50:00', 307, 307, 103541.00, 2),
(10, 'AI188', 'INRNL', 'TORONTO', 'AHMEDABAD', '2025-08-26 13:10:00', '2025-08-27 19:40:00', 310, 310, 118128.00, 2),
(11, 'AI2494', 'INRNL', 'AHMEDABAD', 'NEW YORK', '2025-08-26 20:30:00', '2025-08-27 07:55:00', 310, 310, 134520.00, 2),
(12, 'AI144', 'INRNL', 'NEW YORK', 'AHMEDABAD', '2025-08-26 11:30:00', '2025-08-27 16:25:00', 305, 305, 174909.00, 2),
(13, 'SG1083', 'DMST', 'AHMEDABAD', 'MUMBAI', '2025-08-26 19:20:00', '2025-08-26 20:40:00', 60, 60, 3000.00, 1),
(14, 'AI119', 'INRNL', 'MUMBAI', 'AUSTIN', '2025-08-26 01:15:00', '2025-08-27 17:44:00', 300, 300, 120000.00, 2),
(15, 'SG151', 'DMST', 'DELHI', 'BENGALURU', '2025-08-26 19:20:00', '2025-08-26 22:00:00', 55, 55, 6959.00, 1),
(16, 'SG541', 'DMST', 'BENGALURU', 'MUMBAI', '2025-08-27 04:10:00', '2025-08-27 06:00:00', 65, 65, 4033.00, 1),
(17, 'AI174', 'INRNL', 'AUSTIN', 'MUMBAI', '2025-08-26 05:25:00', '2025-08-27 20:15:00', 295, 295, 115000.00, 2);

-- --------------------------------------------------------

--
-- Table structure for table `passengers`
--

CREATE TABLE `passengers` (
  `passenger_id` int(11) NOT NULL,
  `name` varchar(50) NOT NULL,
  `email` varchar(100) NOT NULL,
  `phone` varchar(10) NOT NULL,
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
(23, 'Ansh Gajera', 'ansh.gajera@gmail.com', '9873583460', 'ansh@4227'),
(24, 'Keyan Patel', 'keyan.j@outlook.com', '6437655192', 'keyzorr_18'),
(25, 'Rudra Gondaliya', 'rudragndly123@gmail.com', '8437300509', 'rudra@123'),
(28, 'nitya', 'nitya2007@gmail.com', '9999999999', '336jn@');

-- --------------------------------------------------------

--
-- Table structure for table `payments`
--

CREATE TABLE `payments` (
  `payment_id` int(11) NOT NULL,
  `passenger_id` int(11) NOT NULL,
  `flight_id` int(11) NOT NULL,
  `amount` decimal(10,2) NOT NULL,
  `payment_time` datetime DEFAULT current_timestamp(),
  `status` varchar(20) NOT NULL DEFAULT 'CONFIRMED'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `reports`
--

CREATE TABLE `reports` (
  `report_id` int(11) NOT NULL,
  `flight_id` int(11) DEFAULT NULL,
  `seats_booked` int(11) DEFAULT NULL,
  `revenue` decimal(10,2) DEFAULT NULL,
  `report_date` datetime DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `reports`
--

INSERT INTO `reports` (`report_id`, `flight_id`, `seats_booked`, `revenue`, `report_date`) VALUES
(1, 1, 0, 0.00, '2025-08-23 15:56:10'),
(2, 2, 0, 0.00, '2025-08-23 09:08:39'),
(3, 3, 0, 0.00, '2025-07-29 03:23:07'),
(4, 4, 0, 0.00, '2025-07-29 03:23:34'),
(5, 5, 0, 0.00, '2025-08-18 11:27:09'),
(6, 6, 0, 0.00, '2025-08-23 15:57:34'),
(7, 7, 0, 0.00, '2025-08-18 11:11:37'),
(8, 8, 0, 0.00, '2025-07-22 22:29:57'),
(9, 9, 0, 0.00, '2025-07-28 22:38:29'),
(10, 10, 0, 0.00, '2025-07-22 22:30:48'),
(11, 11, 0, 0.00, '2025-07-22 22:31:44'),
(12, 12, 0, 0.00, '2025-07-22 22:31:44'),
(13, 13, 0, 0.00, '2025-07-28 23:03:10'),
(14, 14, 0, 0.00, '2025-07-28 23:01:26'),
(15, 15, 0, 0.00, '2025-07-28 18:49:58'),
(16, 16, 0, 0.00, '2025-07-28 18:56:37'),
(17, 17, 0, 0.00, '2025-07-28 19:04:23');

-- --------------------------------------------------------

--
-- Table structure for table `reservations`
--

CREATE TABLE `reservations` (
  `reservation_id` int(11) NOT NULL,
  `flight_id` int(11) NOT NULL,
  `passenger_id` int(11) NOT NULL,
  `passengerName` varchar(50) DEFAULT NULL,
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
-- Indexes for table `payments`
--
ALTER TABLE `payments`
  ADD PRIMARY KEY (`payment_id`),
  ADD KEY `passenger_id` (`passenger_id`),
  ADD KEY `flight_id` (`flight_id`);

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
  MODIFY `flight_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=18;

--
-- AUTO_INCREMENT for table `passengers`
--
ALTER TABLE `passengers`
  MODIFY `passenger_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=30;

--
-- AUTO_INCREMENT for table `payments`
--
ALTER TABLE `payments`
  MODIFY `payment_id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `reports`
--
ALTER TABLE `reports`
  MODIFY `report_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=18;

--
-- AUTO_INCREMENT for table `reservations`
--
ALTER TABLE `reservations`
  MODIFY `reservation_id` int(11) NOT NULL AUTO_INCREMENT;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `flights`
--
ALTER TABLE `flights`
  ADD CONSTRAINT `flights_ibfk_1` FOREIGN KEY (`admin_id`) REFERENCES `admins` (`admin_id`);

--
-- Constraints for table `payments`
--
ALTER TABLE `payments`
  ADD CONSTRAINT `payments_ibfk_1` FOREIGN KEY (`passenger_id`) REFERENCES `passengers` (`passenger_id`),
  ADD CONSTRAINT `payments_ibfk_2` FOREIGN KEY (`flight_id`) REFERENCES `flights` (`flight_id`) ON DELETE CASCADE;

--
-- Constraints for table `reports`
--
ALTER TABLE `reports`
  ADD CONSTRAINT `reports_ibfk_1` FOREIGN KEY (`flight_id`) REFERENCES `flights` (`flight_id`) ON DELETE CASCADE;

--
-- Constraints for table `reservations`
--
ALTER TABLE `reservations`
  ADD CONSTRAINT `reservations_ibfk_1` FOREIGN KEY (`flight_id`) REFERENCES `flights` (`flight_id`) ON DELETE CASCADE,
  ADD CONSTRAINT `reservations_ibfk_2` FOREIGN KEY (`passenger_id`) REFERENCES `passengers` (`passenger_id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
