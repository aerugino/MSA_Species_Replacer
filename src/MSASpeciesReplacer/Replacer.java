/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package MSASpeciesReplacer;

import java.io.*;
import java.util.*;
import java.util.regex.*;


/**
 *
 * @author john
 */
public class Replacer 
{
    Hashtable replaceTable;
    
   public Replacer(String inputFile, String replaceTableFile, String outputFile)
   {
        replaceTable = new Hashtable();
        
        //Try to build the replace-table hashtable
        try
        {
            BufferedReader br = OpenReplaceTable(replaceTableFile); 
            try
            {
            BuildReplacementHashtable(br);
            }
            catch(IOException e)
            {
                System.out.println("Something happened while reading the Replacement table file\n Error!");
                
            }
            
        }
        catch(FileNotFoundException e)
        {
            System.out.println("Error - Replacement Table file not found!");
        }
        
        try
        {
            BufferedReader br = OpenInputFile(inputFile);
            BufferedWriter bw = OpenOutputFile(outputFile);
            MSAReplace(br, bw);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    
        
   }
   
   
   public Replacer(File inputFile, File replacementTableFile, File outputFile)
   {
       replaceTable = new Hashtable();
        
        //Try to build the replace-table hashtable
        try
        {
            BufferedReader br = new BufferedReader(new FileReader(replacementTableFile));
            
            try
            {
            BuildReplacementHashtable(br);
            }
            catch(IOException e)
            {
                System.out.println("Something happened while reading the Replacement table file\n Error!");
                
            }
            
        }
        catch(FileNotFoundException e)
        {
            System.out.println("Error - Replacement Table file not found!");
        }
        
        try
        {
            BufferedReader br = new BufferedReader(new FileReader(inputFile));
            BufferedWriter bw = new BufferedWriter(new FileWriter(outputFile));
            MSAReplace(br, bw);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
   }
   private BufferedReader OpenReplaceTable(String replaceTable) throws FileNotFoundException
   {
     
       return new BufferedReader(new FileReader(replaceTable));
       
   }
   
   private BufferedWriter OpenOutputFile(String outputFile) throws IOException
   {
       return new BufferedWriter(new FileWriter(outputFile));
       
   }
   
   private BufferedReader OpenInputFile(String inputFile) throws FileNotFoundException
   {
       return new BufferedReader(new FileReader(inputFile));
   }
   
   private void MSAReplace(BufferedReader input, BufferedWriter output) throws IOException
   {
       Pattern pattern = Pattern.compile("[0-9][0-9][0-9]+");
       String number = "";
       String line = "";
       String replaced = "";
       Matcher matcher;
       
       try
       {
       line = input.readLine();
       
       while(line != null)
       {
        matcher = pattern.matcher(line);
           
         while(matcher.find())
         {
         
             number = matcher.group();
           //  replaced = replaceTable.get(number).toString();
             System.out.println("Value: " + replaced);
             
             if(replaceTable.containsKey(number))
             {
             line = line.replace(number, replaceTable.get(number).toString());
             }
         }
            output.write(line + "\n");
            System.out.println(line);
           line = input.readLine();
       }
      // output.write("HI!\n");

       input.close();
       output.close();
       }
       catch(Exception e)
       {
           e.printStackTrace();
       }
   }
   
   private void BuildReplacementHashtable(BufferedReader replaceTableStream) throws IOException
   {
        String line = replaceTableStream.readLine();
       
        while(line != null)
        {
             String[] splits = line.split(",", 2);
             
             //If this is a blank line essentially, we want to just skip it.
             if(splits.length > 1)
             {
                
                 //Remove all leading and trailing spaces
                splits[0] = splits[0].trim();
                splits[1] = splits[1].trim();
                
                //Remove all internal spaces - replace with underscores
                splits[1] = splits[1].replaceAll("\\s", "_");
                
                //Figure out where the first underscore is - so that we can format the species name
                //The way it will work is that the species name has a max length of 20 chars.
                //So, it will be the first letter of the genus name, period, underscore, then up to 14 chars of 
                //the species name, and the last four digits of the accession number.
                int underscoreIndex = splits[1].indexOf("_");
                int maxSpeciesName = splits[1].indexOf("_", underscoreIndex+1);
                if(maxSpeciesName == -1)
                {
                    maxSpeciesName = splits[1].length();
                }
                if(maxSpeciesName > underscoreIndex+14)
                {
                    maxSpeciesName = underscoreIndex+14;
                }
               
                  String speciesID = splits[1].substring(0, 1) + "._"+ splits[1].substring(underscoreIndex+1, maxSpeciesName) + splits[0].substring(splits[0].length()-4);
                
                
             //   System.out.println(splits[0]);
               // System.out.println(speciesID);
                replaceTable.put(splits[0], speciesID);
             }
             
             line = replaceTableStream.readLine();           
        }
        
        Iterator<Map.Entry> it;
        Map.Entry entry;
        
        it = replaceTable.entrySet().iterator();
        while(it.hasNext())
        {
            entry = it.next();
            System.out.println("Key: " + entry.getKey().toString() + "  | Value: " + entry.getValue().toString());
            
        }
      replaceTableStream.close();
   }
   
  
   
   
    
}
