# jcrunch
Penetration Test Tool. JCrunch is a wordlist generator where you can modify a dictionary and add a number as suffix generating all possible combinations. Useful to generate words like: Jim1943, james007 or ser2018.

### Compile and generate .jar file
* mvn package

### Options
* -normalize or -n: normalize words (lower case and escape characters)
* -cut_words or -c: cut words to a given length
* -add_num_min or -x: concatenate a number as a suffix after words (minimum)
* -add_num_max or -y: concatenate a number as a suffix after words (maximum)
* -wordlist or -w: wordlist file
* -suffix_wordlist or -s: suffix wordlist file (suffixes after number i.e.: hello1234abc)
* -left_pad or -p: complete number with ZEROS
* -encoding or -e: encoding of wordlist files

### Execution Examples
* java -jar JCrunch.jar -normalize -cut_words 4 -wordlist nombres_propios.txt -add_num_min 0 -add_num_max 9999
* java -jar JCrunch.jar -n -c 4 -w nombres_propios.txt -x 0 -y 9999
* java -jar JCrunch.jar -n -c 4 -w nombres_propios.txt -x 0 -y 9999 | aircrack-ng --bssid aa:aa:aa:aa:aa:aa -w- handshakefile.cap
* java -jar jcrunch.jar -w prefixes.txt -x 1000000 -y 5000000 -s suffixes.txt > my_dictionary.txt
