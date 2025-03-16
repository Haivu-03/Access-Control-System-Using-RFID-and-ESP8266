# README - RFID System with ESP8266 and Android App

## Introduction
This project includes:
1. **ESP8266** reads RFID MFRC522 cards, controls a servo, and sends data to the server.
2. **Android App** allows users to view entry-exit history, record payments, and process transactions.

## Hardware
- ESP8266 (NodeMCU)
- RFID MFRC522 module
- Servo motor

## Libraries
- **ESP8266**: `ESP8266WiFi.h`, `SPI.h`, `MFRC522.h`, `Servo.h`
- **Android**: `Retrofit`, `Room Database`, `Firebase` (optional)

## Hardware Connections
| Device   | ESP8266 |
|----------|--------|
| RFID SS  | D8     |
| RFID RST | D2     |
| Servo PWM | D1    |

## Code Structure
- **ESP8266**: `setup()`, `loop()`
- **Android**: `MainActivity`, `PaymentActivity`, `ApiService`

## Server Configuration (XAMPP)
1. Install XAMPP: [https://www.apachefriends.org](https://www.apachefriends.org)
2. Start Apache & MySQL.
3. Place PHP files in `htdocs` (e.g., `C:\xampp\htdocs\IoT1\receive_data.php`).
4. Access `http://localhost/IoT1/receive_data.php` to check the connection.

## Usage
- **ESP8266**: Upload code, scan RFID card to unlock.
- **Android**: Log in, view history, process payments.

## Notes
- Ensure XAMPP is running Apache & MySQL.
- Check WiFi connection & app network permissions.

