rm *.jar
cd src
javac -encoding utf8 *.java
jar -cvfe  ../Server.jar Server *.class
jar -cvfe  ../Client.jar Client *.class
rm *.class
cd ..

java -jar Server.jar
