Install instructions

Generate certificate for inbound SSL connection on 8443

info comes from: http://stackoverflow.com/questions/951890/eclipse-wtp-how-do-i-enable-ssl-on-tomcat

[user@fedpark23 keystore]$ keytool -genkey -alias tomcat -keypass mypassword -keystore ./tomcat-keystore.jks -storepass mypassword -keyalg RSA -validity 360 -keysize 2048
What is your first and last name?
  [Unknown]:  Lawrence Turcotte
What is the name of your organizational unit?
  [Unknown]:  OSA-UNIT
What is the name of your organization?
  [Unknown]:  OSA
What is the name of your City or Locality?
  [Charlotte, NC]:  Charlotte
What is the name of your State or Province?
  [NC]:  
What is the two-letter country code for this unit?
  [US]:  
Is CN=Lawrence Turcotte, OU=OSA-UNIT, O=OSA, L=Charlotte, ST=NC, C=US correct?
  [no]:  yes
  
Modify server.xml for Tomcat 8 to add SSL inbound connector with keystore location

    <Connector port="8443" protocol="org.apache.coyote.http11.Http11NioProtocol"
               maxThreads="15" SSLEnabled="true" scheme="https" secure="true"
               clientAuth="false" sslProtocol="TLS"
               keystoreFile="/run/media/user/fedparkdev1/opt/apache-tomcat-8.0.33/keystore/tomcat-keystore.jks"
               keystorePass="mypassword" />

Modify application.properties with facebook parameters


facebook.appKey={{SEE README}}
facebook.appSecret={{SEE README}}
facebook.canvasPage={{SEE README}}

facebook.appKey=1035026473232719
facebook.appSecret=1e722e7305e9677cb31abc26dd2e7915
facebook.canvasPage=https://apps.facebook.com/maplezcanvas


Troubleshooting

org.springframework.social.facebook.web.SignedRequestException: Invalid signature.

Probably indicates that the app secret is incorrect within application.properties

Facebook signed_request occurs here within spring social where "Secure Canvas URL" is https://192.168.1.11:8443/spring-social-canvas-proxy/canvas/
Note: canvas/ suffix this is important since CanvasSignInController waits for call back POST from facebook to obtain token
org.springframework.social.facebook.web.CanvasSignInController.signin(Model, NativeWebRequest)
	@RequestMapping(method={ RequestMethod.POST, RequestMethod.GET }, params={"signed_request", "!error"})
	public View signin(Model model, NativeWebRequest request) throws SignedRequestException


