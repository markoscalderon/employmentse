EmploymentSE 
============

Overview
--------

The EmploymentSE program is composed on 4 main components:

1. **TSVParser**

    Package: org.employmentse.parser
    
    Class: TSVParser
    
    Description: This class extends from AbstractParser (tika) and implements the methods of getSupportedTypes and parse. Basically, it takes a TSV file and parse the document to a XHTML structure.

2. **JSONTableContentHandler**

    Package: org.employmentse.content.handler
    
    Class: JSONTableContentHandler
    
    Description: This class extends from SafeContentHandler and implements the methods characters, startElement and endElement. It generates the output of individual json files based on the rows from a table during the XHTML creation with a tika parser. It is possible to enable or disable deduplication tecniques.

3. **Deduplication**

    Package: org.employmentse.deduplication
    
    Classes: Deduplicator and FingerPrint
    
    Description: This implements a deduplication technique for a set of rows/records. The deduplication process creates a FingerPrint for a record on the fly and calculate the similarity across a set of FingerPrints that are already stored. For a matter of performance, the set of FingerPrints are stored on buckets per a certain key. So in that way, it will only perform a similarity comparison (shingle hashes) to a subset of fingerprints (and not all the fingerprints stored). It also uses a catching technique by loading only if a bucket is requested, if all the other buckets are not requested, then those are never loaded. This module uses a Redis Database.

4. **EmploymentSE**

    Package: org.employmentse
    
    Class: EmploymentSE
    
    Description: the main program that read tsv files from a 'assets' directory and put the results in a directory called 'ouput'



Installation
------------

1. Install Redis if Deduplication is enabled. Deduplication is disabled by default. 

2. EmploymentSE is built using gradle. For easiness, this project is shipped with a bash and bat script that it will take care of the gradle installation, dependency, and building. Therefore the only command needed to build the project is the following:

```
On Unix: ./gradlew build
On Mac: gradlew.bat build
```

Running
-------

Finally, please create a folder called "assets" on the root of the project and put your tsv files. Then, sit back, get a coffee, and do:

```
On Unix: ./gradlew run -Dexec.args="-program tsv -deduplication off"
On Mac: gradlew.bat run -Dexec.args="-program tsv -deduplication off"
```

You should see a new folder called "output" with the individual json files


Using ETLlib 
------------

ETLlib is a set of tools that enables data extraction. One of those tool is tsvtojson which extracts the contents of tsv files and creates json files. You can read more about it on: https://github.com/chrismattmann/etllib

Aditionally, to these set of tools, we created an internal tool called JSONSplitter that is provided with this package. This tool enables to split a JSONArray into individual files with the option to use the deduplicator module.

For using the etllib support, first install ETLlib, see:
https://github.com/chrismattmann/etllib

Once etllib is installed, create a folder called "assets" on the root of the project and put your tsv files. Then, do:

```
sh crawler.sh -d assets
```

This will generated a json file containing all the rows per each tsv file. Then, we need to generate the individual json files, do:

```
On Unix: ./gradlew run -Dexec.args="-program json -deduplication off"
On Mac: gradlew.bat run -Dexec.args="-program json -deduplication off"
```

You should see a new folder called "output" with the individual json files


Authors
-------

Johnson Hsieh

Imran Mammadli

Marco Calderon


