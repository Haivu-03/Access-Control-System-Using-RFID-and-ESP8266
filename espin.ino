#include <ESP8266WiFi.h>
#include <SPI.h>
#include <MFRC522.h>
#include <Servo.h>

#define SS_PIN  D8      // Chân D8 cho RFID
#define RST_PIN D2      // Chân D2 cho RFID
#define SERVO_PIN D1    // Chân D1 cho Servo motor

MFRC522 rfid(SS_PIN, RST_PIN);  
Servo servo;

// Danh sách UID của các thẻ được cấp phép
byte T1[4] = {0xB7, 0xBE, 0x38, 0x63}; // Thẻ 1
byte T2[4] = {0x4C, 0x53, 0xAB, 0x33}; // Thẻ 2
byte T3[4] = {0x49, 0x12, 0xE7, 0xB3}; // Thẻ 3

int angle = 0;           // Góc hiện tại của servo
unsigned long openTime = 0; // Thời gian mở servo
bool servoOpen = false;  // Trạng thái của servo

// Thông tin WiFi
const char* ssid = "VNH"; // Thay bằng SSID WiFi của bạn
const char* password = "!!!!!!!!"; // Thay bằng mật khẩu WiFi

// Thông tin server
const char* server = "http://172.20.10.9/IoT1/receive_data.php"; // Địa chỉ IP server

void setup() {
  Serial.begin(9600);
  SPI.begin();                 // Khởi tạo SPI bus
  rfid.PCD_Init();              // Khởi tạo RFID
  servo.attach(SERVO_PIN);      // Khởi tạo servo
  servo.write(angle);           // Đặt servo về góc 0°

  // Kết nối WiFi
  WiFi.begin(ssid, password);
  while (WiFi.status() != WL_CONNECTED) {
    delay(1000);
    Serial.println("Connecting to WiFi...");
  }
  Serial.println("Connected to WiFi");
}

void loop() {
  // Kiểm tra nếu có thẻ RFID mới
  if (rfid.PICC_IsNewCardPresent() && rfid.PICC_ReadCardSerial()) {
    const char* cardName = getCardName();  // Kiểm tra tên thẻ

    if (cardName != nullptr) {
      Serial.print("Authorized Tag: ");
      Serial.println(cardName);
      openServo();  // Mở servo
    } else {
      Serial.print("Unauthorized Tag with UID:");
      for (int i = 0; i < rfid.uid.size; i++) {
        Serial.print(rfid.uid.uidByte[i] < 0x10 ? " 0" : " ");
        Serial.print(rfid.uid.uidByte[i], HEX);
      }
      Serial.println();
    }

    rfid.PICC_HaltA();        // Dừng thẻ
    rfid.PCD_StopCrypto1();   // Dừng mã hóa
  }

  // Kiểm tra nếu servo đã mở quá 5 giây
  if (servoOpen && millis() - openTime >= 5000) {
    closeServo();  // Đóng servo
  }
}

const char* getCardName() {
  // So sánh UID của thẻ với danh sách thẻ được cấp phép
  if (memcmp(rfid.uid.uidByte, T1, 4) == 0) return "T1";
  if (memcmp(rfid.uid.uidByte, T2, 4) == 0) return "T2";
  if (memcmp(rfid.uid.uidByte, T3, 4) == 0) return "T3";
  return nullptr;  // Thẻ không hợp lệ
}

void openServo() {
  Serial.println("Attempting to open servo...");
  servo.write(90);  // Mở servo tới 90°
  delay(200);  // Giảm thiểu nhiễu khi điều khiển servo
  Serial.println("Servo opened to 90°");

  servoOpen = true;
  openTime = millis();  // Lưu thời điểm mở servo

  // Gửi dữ liệu lên server
  String tag = getCardName();
  double thanhtien = 3000;
  String url = String(server) + "?tag=" + tag + "_IN" + "&thanhtien=" + thanhtien;
  sendData(url);
}


void sendData(String url) {
  WiFiClient client;  // Tạo đối tượng client
  if (client.connect("172.20.10.9", 80)) {  // Kết nối tới server
    client.print(String("GET ") + url + " HTTP/1.1\r\n" +
                 "Host: " + "172.20.10.9" + "\r\n" +
                 "Connection: close\r\n\r\n");
    delay(500);  // Chờ phản hồi
    while (client.available()) {
      String line = client.readStringUntil('\n');
      Serial.println(line);  // In phản hồi từ server
    }
  } else {
    Serial.println("Connection failed");
  }
}

void closeServo() {
  servo.write(0);  // Đóng servo về 0°
  Serial.println("Servo closed to 0°");
  servoOpen = false;
}
