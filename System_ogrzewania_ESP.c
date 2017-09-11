#include <ESP8266WiFi.h>
#include <OneWire.h>
#include <DallasTemperature.h>
#include <SPI.h>
#include <Wire.h>
#include <Adafruit_GFX.h>
#include <Adafruit_SSD1306.h>
const char* ssid = "PENTAGRAM";
const char* password = "12345678";

#define OLED_RESET LED_BUILTIN  //4 pin do oleda
Adafruit_SSD1306 display(OLED_RESET);


#if (SSD1306_LCDHEIGHT != 32)
#error("Height incorrect, please fix Adafruit_SSD1306.h!");
#endif


#define ONE_WIRE_BUS 14 // termometr pin
#define piecAUTO 1
#define piecOFF 2 
#define piecON 3

#define TempMin 0
#define TempMax 50
float temp_grzania; // temp do podania pin
double temperatura = 20;
int grzalka = 0;
int piec = 12; // wyjscie na przekaznik
int stan;
String readString = String(40);
// create an instance of the server
// specify the port to listen on as an argument
WiFiServer server(80);

void setup() {
  //------
  IPAddress ip(192, 168, 1, 17);
  IPAddress subnet(255, 255, 255, 0);
  IPAddress gt(192, 168, 1, 1);
  //------
  Serial.begin(115200);
  delay(1000);
  WiFi.config(ip, gt, subnet);
  WiFi.begin(ssid, password);
  // prepare GPIO2
  pinMode(piec, OUTPUT);
  digitalWrite(piec, 0);

  // connect to WiFi network
  Serial.println();
  Serial.println();
  Serial.print("laczenie z : ");
  Serial.println(ssid);
  while (WiFi.status() != WL_CONNECTED) {
    delay(500);
    Serial.print(".");

  }
  Serial.println("");
  Serial.println("polaczono");
  // Start the server
  server.begin();
  // Print the IP address
  Serial.println(WiFi.localIP());



  display.begin(SSD1306_SWITCHCAPVCC, 0x3C);

}
//----------------------------------------------------------
void termostat() {
  //int stan;
  OneWire oneWire(ONE_WIRE_BUS);
  DallasTemperature sensors(&oneWire);
  sensors.requestTemperatures();
  temperatura = (sensors.getTempCByIndex(0));

  if (grzalka == piecAUTO && temperatura < temp_grzania) {
    stan = HIGH;
  }
  else if (grzalka == piecOFF)
    stan = LOW;
  else if (grzalka == piecON)
    stan = HIGH;
  else {
    stan = LOW;
  }
  digitalWrite(piec, stan);

  //Serial.println("TERMOSTAT temp: ");
  // Serial.println(temperatura);
  //Serial.println("TERMOSTAT stan: ");
  //Serial.println(temp_grzania);
  //Serial.println("TERMO T/F: ");
  //Serial.println(grzalka);

  display.clearDisplay();
  display.display();

  display.setTextSize(0);

  display.setCursor(0, 0);
  display.setTextColor(WHITE);
  display.print("Temperatura: ");
  display.print(temperatura);
  //display.println(" 'C");
  display.println("");
  display.print("ogrzewanie: ");
  display.display();
  if (stan) {
    display.print("ON");
    display.display();
  }
  else {
    display.print("OFF");
    display.display();
  }




}
//----------------------------------------------------------
void wifi() {
  //-----------------

  // check if a client has connected
  WiFiClient client = server.available();
  delay(1);
  if (!client) {
    return;
  }
  //Serial.println("nev client:");
  while (!client.available()) {
    delay(1);
  }
  String readString = client.readStringUntil('\n');
  //Serial.println(readString);
  //int abc = client.parseInt();
  //Serial.println(temp_grzania);


  if (readString.indexOf("piecON") != -1) {
    grzalka = piecON;
    //Serial.println("piecON");
  }
  else if (readString.indexOf("piecOFF") != -1) {
    grzalka = piecOFF;
    //Serial.println("piecOFF");
  }
  else if (readString.indexOf("auto") != -1) {
    float GetTemp = readString.toFloat();
    // Serial.print("get temp: ");
    // Serial.println(GetTemp);
    String asd = readString.substring(18, 24);
    //Serial.print("substring: ");
    // Serial.println(asd);

    if ((GetTemp >= TempMin) && (GetTemp <= TempMax)) {
      grzalka = piecAUTO;
      //temp_grzania = GetTemp;
      temp_grzania = asd.toFloat();
      //Serial.print("piec auto: ");
      // Serial.println(temp_grzania);
    }
    //else {
    // Serial.print("bledna temperatura");
    //}
  }
  //-------------------------------------------
  client.println("HTTP/1.1 200 OK");
  // readString = "";
  client.println("content-Type: text/html");
  client.println();
  client.println("<!doctype html>");
  client.println("<html>");
  client.println("<head>");
  client.println("<title>ESP</title>");
  client.println("<meta name=\"viewport\" content=\"width=320\">");
  client.println("<meta name=\"viewport\" content=\"width=device-width\">");
  client.println("<meta charset=\"utf-8\">");
  client.println("<meta name=\"viewport\" content=\"initial-scale=1.0, user-scalable=no\">");
  client.println("</head>");
  client.println("<body>");
  client.println("<center>");
  client.println("<font size=\"5\">Temperatura: </font>");
  client.println(temperatura);
  client.println("<br>");
  client.println("<font size=\"3\">Temperatura ogrzewania: </font>");
  client.print(temp_grzania);
  //-----------------------
  client.println("<form action=\"action_page.php\" method=\"get\">");
  client.println("<br>");
  client.println("</form> <br />");
  client.println("<button type=\"button\" onclick=\"location.href='http://192.168.1.17/piecON'\">");
  client.println(" PIEC: ON ");
  client.println("</button>");
  client.println("<br>");
  client.println("<button type=\"button\" onclick=\"location.href='http://192.168.1.17/piecOFF'\">");
  client.println("PIEC: OFF");
  client.println("</button>");
  client.println("<br>");
  client.println("<br>");
  client.println("<form action=\"auto\">");
  client.println("<br>");
  client.println("Termostat:<br>");
  client.println("<input type=\"text\" name=\"wartosc\" value="">");
  client.println("<br>");
  client.println("<input type=\"submit\" value=\"zatiwerdz\">");
  client.println("</form>");

  client.print("<font size=\"5\">Stan Pieca: </font>");
  client.println(grzalka);

  client.print("<font size=\"5\">Piec ON/OFF: </font>");
  client.println(stan);
  client.println("</center>");
  client.println("</body>");
  client.println("</html>");
  readString = "";
  client.stop();


}
void loop() {
  wifi();
  termostat();
  delay(500);
}