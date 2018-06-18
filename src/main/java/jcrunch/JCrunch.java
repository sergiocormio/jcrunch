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
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class JCrunch {
   
   private List<String> wordlist = new ArrayList<>();
   private boolean addNumberSuffix = false;
   private int minNumberSuffix = 0;
   private int maxNumberSuffix = 9999;
   
   public JCrunch ( BufferedReader br, CommandLine commandLine) throws IOException{
      String word;
      while ((word = br.readLine()) != null) {
         if(commandLine.hasOption( "normalize" )){
            word = word.toLowerCase();
            word = word.replaceAll( "á", "a" );
            word = word.replaceAll( "é", "a" );
            word = word.replaceAll( "í", "a" );
            word = word.replaceAll( "ó", "a" );
            word = word.replaceAll( "ú", "a" );
            word = word.replaceAll( "-", "" );
            word = word.replaceAll( " ", "" );
         }
         
         if(commandLine.hasOption( "cut_words")){
            int length =Integer.parseInt( commandLine.getOptionValue( "cut_words", "4" ) );
            if (word.length() > length){
               word = word.substring( 0, length );
            }
         }
         
         if(commandLine.hasOption( "add_num_min" ) || commandLine.hasOption( "add_num_max" )){
            addNumberSuffix = true;
            minNumberSuffix =Integer.parseInt( commandLine.getOptionValue( "add_num_min", "0" ) );
            maxNumberSuffix =Integer.parseInt( commandLine.getOptionValue( "add_num_max", "9999" ) );
         }
         // process the line.
         wordlist.add( word );
      }
      br.close();
   }
   
   public void run(){
      int count = 0;
      if( addNumberSuffix ){
         for( count = minNumberSuffix; count <= maxNumberSuffix; count++){
            for( String word : wordlist ){
               System.out.println( word + count );
               if(count < 1000){
                  System.out.println( word + "0" + count );
                  System.out.println( word + "00" + count );
                  System.out.println( word + "000" + count );
               }
            }
         }
      }else{
         for( String word : wordlist ){
            System.out.println( word );
         }
      }
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
      
      // create the parser
      CommandLineParser parser = new DefaultParser();
      try {
          // parse the command line arguments
          CommandLine commandLine = parser.parse( options, args );
          String filePath = commandLine.getOptionValue( "wordlist" );
          BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(filePath), "iso-8859-1"));
          new JCrunch( br, commandLine ).run();
      }
      catch( ParseException exp ) {
          // oops, something went wrong
          System.err.println( "Parsing failed.  Reason: " + exp.getMessage() );
      }
   }

}
