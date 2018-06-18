# jcrunch
Penetration Test Tool. JCrunch is a wordlist generator where you can modify a dcitionary and add a number as suffix generating all possible combinations.

### Options
* -normalize or -n: normalize words (lower case and escape characters)
* -cut_words or -c: cut words to a given length
* -add_num_min or -x: concatenate a number as a suffix after words (minimum)
* -add_num_max or -y: concatenate a number as a suffix after words (maximum)
* -wordlist or -w: wordlist file

### Exceution Examples
* java -jar JCrunch.jar -normalize -cut_words 4 -wordlist nombres_propios.txt -add_num_min 0 -add_num_max 9999
* java -jar JCrunch.jar -n -c 4 -w nombres_propios.txt -x 0 -y 9999
* java -jar JCrunch.jar -n -c 4 -w nombres_propios.txt -x 0 -y 9999 | aircrack-ng --bssid aa:aa:aa:aa:aa:aa -w- handshakefile.cap
