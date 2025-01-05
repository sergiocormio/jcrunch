package jcrunch;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class JCrunch {
   
   private List<String> wordlist = new ArrayList<>();
   private List<String> suffixWordlist = new ArrayList<>();
   private List<String> prefixWordlist = new ArrayList<>();
   private boolean addNumberSuffix = false;
   private int minNumberSuffix = 0;
   private int maxNumberSuffix = 9999;
   private boolean applyPadding = false;
   private String leftPadExpression;
   private int firstWordIndex = 0;
   private StringBuilder stringBuilderAux = new StringBuilder();

   
   public JCrunch ( CommandLine commandLine ) throws IOException{
      String filePath = commandLine.getOptionValue( "wordlist" );
      BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(filePath), commandLine.getOptionValue( "encoding", "iso-8859-1" )));
      String word;
      while ((word = br.readLine()) != null) {
         if(commandLine.hasOption( "normalize" )){
            word = normalize(word);
         }
         
         if(commandLine.hasOption( "cut_words")){
            int length =Integer.parseInt( commandLine.getOptionValue( "cut_words", "4" ) );
            if (word.length() > length){
               word = word.substring( 0, length );
            }
         }
         
         // process the line.
         wordlist.add( word );
      }
      br.close();
      
      if(commandLine.hasOption( "first_word")){
    	  String firstWord = commandLine.getOptionValue( "first_word" );
    	  this.firstWordIndex = wordlist.indexOf(firstWord);
    	  if(firstWordIndex < 0 ){
    		  firstWordIndex = 0;
    	  }
      }
      
      if(commandLine.hasOption( "add_num_min" ) || commandLine.hasOption( "add_num_max" )){
    	  addNumberSuffix = true;
    	  minNumberSuffix =Integer.parseInt( commandLine.getOptionValue( "add_num_min", "0" ) );
    	  maxNumberSuffix =Integer.parseInt( commandLine.getOptionValue( "add_num_max", "9999" ) );
      }

      if( commandLine.hasOption( "suffix_wordlist" )){
         String suffixFilePath = commandLine.getOptionValue( "suffix_wordlist" );
         br = new BufferedReader(new InputStreamReader(new FileInputStream(suffixFilePath), commandLine.getOptionValue( "encoding", "iso-8859-1" )));
         while ((word = br.readLine()) != null) {
	    	 if(commandLine.hasOption( "normalize" )){
	             word = normalize(word);
	          }
	    	 suffixWordlist.add( word );
         }
         br.close();
      }else{
         suffixWordlist.add( "" );
      }
      
      if( commandLine.hasOption( "prefix_wordlist" )){
          String prefixFilePath = commandLine.getOptionValue( "prefix_wordlist" );
          br = new BufferedReader(new InputStreamReader(new FileInputStream(prefixFilePath), commandLine.getOptionValue( "encoding", "iso-8859-1" )));
          while ((word = br.readLine()) != null) {
        	  if(commandLine.hasOption( "normalize" )){
                  word = normalize(word);
              }
        	  prefixWordlist.add( word );
          }
          br.close();
       }else{
    	   prefixWordlist.add( "" );
       }
      
      
      if( commandLine.hasOption( "left_pad" )){
         applyPadding = true;
         leftPadExpression =  "%0" + commandLine.getOptionValue( "left_pad" , "8") + "d";
      }
   }

private String normalize(String word) {
	word = word.toLowerCase();
	word = word.replaceAll( "á", "a" );
	word = word.replaceAll( "é", "a" );
	word = word.replaceAll( "í", "a" );
	word = word.replaceAll( "ó", "a" );
	word = word.replaceAll( "ú", "a" );
	word = word.replaceAll( "-", "" );
	word = word.replaceAll( " ", "" );
	return word;
}
   
   public void run(){
      int count = 0;
      String number;
      String word;
      int index = firstWordIndex;
      if( addNumberSuffix ){
         for( count = minNumberSuffix; count <= maxNumberSuffix; count++){
        	for (String prefix: prefixWordlist){
	            while( index < wordlist.size() ){
	            	word = wordlist.get(index);
	            	for(String postSuffix : suffixWordlist){
	            		if(applyPadding){
	            			number = String.format( leftPadExpression, count);
	            		}else{
	            			number = String.valueOf( count );
	            		}
	            		print(prefix+word, postSuffix , number);
	            	}
	               index++;
	            }
	            index = 0;
        	}
         }
      }else{
    	  for (String prefix: prefixWordlist){
    		  while( index < wordlist.size() ){
    			  word = wordlist.get(index);
    			  for(String suffix : suffixWordlist){
    				  print(prefix, word, suffix);
    			  }
    			  index++;
	           }
	          index = 0;
    	  }
      }
   }
   
   private void print(String prefix, String middle, String suffix){
      stringBuilderAux.setLength(0); //clear
      stringBuilderAux.append( prefix );
      stringBuilderAux.append( middle );
      stringBuilderAux.append( suffix );
      System.out.println( stringBuilderAux );
   }
   
   public static void main( String[] args ) throws IOException {
      Options options = new Options();
      Option wordlistFile = Option.builder("w")
                              .argName( "FILE" )
                              .longOpt( "wordlist" )
                              .hasArg()
                              .required()
                              .desc( "wordlist file" )
                              .build();
      options.addOption( wordlistFile );
      
      Option prefixesWordlistFile = Option.builder("p")
              .argName( "FILE" )
              .longOpt( "prefix_wordlist" )
              .hasArg()
              .desc( "prefixes wordlist file" )
              .build();
      options.addOption( prefixesWordlistFile );
      
      Option suffixesWordlistFile = Option.builder("s")
               .argName( "FILE" )
               .longOpt( "suffix_wordlist" )
               .hasArg()
               .desc( "suffixes wordlist file (suffixes after number i.e.: hello1234abc)" )
               .build();
      options.addOption( suffixesWordlistFile );
      
      Option normalize = Option.builder("n")
               .longOpt( "normalize" )
               .desc( "normalize words: lower case and escape characters" )
               .build();
      options.addOption( normalize );
      
      Option cutWords = Option.builder("c")
               .argName( "NUMBER_OF_CHARACTERS" )
               .longOpt( "cut_words" )
               .hasArg()
               .desc( "cut words to a given length" )
               .build();
      options.addOption( cutWords );
      
      Option addNumMin = Option.builder("x")
               .argName( "MIN_NUMBER" )
               .longOpt( "add_num_min" )
               .hasArg()
               .desc( "concatenate a number as a suffix after words (minimum)" )
               .build();
      options.addOption( addNumMin );
      
      Option addNumMax = Option.builder("y")
               .argName( "MAX_NUMBER" )
               .longOpt( "add_num_max" )
               .hasArg()
               .desc( "concatenate a number as a suffix after words (maximum)" )
               .build();
      options.addOption( addNumMax );
      
      Option encoding = Option.builder("e")
               .argName( "ENCODING" )
               .longOpt( "encoding" )
               .hasArg()
               .desc( "encoding of wordlist files" )
               .build();
      options.addOption( encoding );
      
      Option leftPad = Option.builder("l")
               .argName( "DIGITS" )
               .longOpt( "left_pad" )
               .hasArg()
               .desc( "complete number with ZEROS" )
               .build();
      options.addOption( leftPad );
      
      Option firstWord = Option.builder("f")
              .argName( "WORD" )
              .longOpt( "first_word" )
              .hasArg()
              .desc( "begins with this WORD from the wordlist" )
              .build();
      options.addOption( firstWord );
      
      // create the parser
      CommandLineParser parser = new DefaultParser();
      try {
          // parse the command line arguments
          CommandLine commandLine = parser.parse( options, args );
          new JCrunch( commandLine ).run();
      }
      catch( ParseException exp ) {
          // oops, something went wrong
          System.err.println( "Parsing failed.  Reason: " + exp.getMessage() );
          
          String header = "Generate a list of possible password based on a wordlist and numbers\n\n";
          String footer = "\nPlease report issues at https://github.com/sergiocormio/jcrunch";
          
          HelpFormatter formatter = new HelpFormatter();
          formatter.printHelp("jcrunch", header, options, footer, true);
      }
   }

}
