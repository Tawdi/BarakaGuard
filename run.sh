
SRC="src"
OUT="out"
LIB="lib/*"

javac -d $OUT -cp "$LIB" $(find $SRC -name "*.java")

java -cp "$OUT:$LIB" main.java.com.barakaguard.app.Main