#include <ESP8266WiFi.h>
#include <OneWire.h>

#include <SPI.h>
#include <Wire.h>

const char* ssid = "PENTAGRAM";
const char* password = "12345678";


#define ONE_WIRE_BUS 14 // termometr pin
#define oswietlenieAUTO 1
#define swiatloOFF 2
#define swiatloON 3
#define oswietlenie_dziennoc 4

#define czujnik 5  //czujnik pir na D1


int trybOswietlenia = 0;
int swiatlo = 4; // wyjscie na przekaznik D2
int stan;



String readString = String(40);
WiFiServer server(80);

void setup() {
  //------
  IPAddress ip(192, 168, 1, 18); // adres ip 18
  IPAddress subnet(255, 255, 255, 0);
  IPAddress gt(192, 168, 1, 1);
  //------
  Serial.begin(115200);
  delay(1000);
  WiFi.config(ip, gt, subnet);
  WiFi.begin(ssid, password);
  // prepare GPIO
  pinMode(swiatlo, OUTPUT);
  digitalWrite(swiatlo, 0);
  pinMode(czujnik, INPUT);

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

}
//----------------------------------------------------------
void termostat() {
  //int stan;
  int fotorezystor = analogRead(A0); // wyjscie na pin analogowy A0

  if (trybOswietlenia == oswietlenieAUTO && digitalRead(czujnik) ) {
    stan = HIGH;
  }
  else if (trybOswietlenia == swiatloOFF)
    stan = LOW;
  else if (trybOswietlenia == swiatloON)
    stan = HIGH;
  else if (trybOswietlenia == oswietlenie_dziennoc && fotorezystor < 300)
    stan = HIGH;
  else {
    stan = LOW;
  }
  digitalWrite(swiatlo, stan);
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


  if (readString.indexOf("swiatloON") != -1) {
    trybOswietlenia = swiatloON;
  }
  else if (readString.indexOf("swiatloOFF") != -1) {
    trybOswietlenia = swiatloOFF;
  }
  else if (readString.indexOf("auto") != -1) {
    trybOswietlenia = oswietlenieAUTO;
  }
  else if (readString.indexOf("dziennoc") != -1) {
    trybOswietlenia = oswietlenie_dziennoc;
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
  client.println("<font size=\"5\">OŚWIETLENIE</font>");

  client.println("<br>");

  //-----------------------
  client.println("<form action=\"action_page.php\" method=\"get\">");
  client.println("<br>");
  client.println("</form> <br />");
  client.println("<button type=\"button\" onclick=\"location.href='http://192.168.1.18/swiatloON'\">");
  client.println(" Swiatlo: ON ");
  client.println("</button>");
  client.println("<br>");
  client.println("<button type=\"button\" onclick=\"location.href='http://192.168.1.18/swiatloOFF'\">");
  client.println("Swiatlo: OFF");
  client.println("</button>");
  client.println("<br>");
  client.println("<button type=\"button\" onclick=\"location.href='http://192.168.1.18/auto'\">");
  client.println("czujnik ruchu");
  client.println("</button>");
  client.println("<br>");

  client.println("</form>");
  client.println("<button type=\"button\" onclick=\"location.href='http://192.168.1.18/dziennoc'\">");
  client.println("Dzien/Noc");
  client.println("</button>");
  client.println("");
  client.println("<br>");
  client.print("<font size=\"1\">Stan Oswietlenia: </font>");
  client.println(stan);
  client.print("<font size=\"1\">tryb o: </font>");
  client.println(trybOswietlenia);
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