#!/bin/bash
# make sure Apache Solr and OODT are both installed and running on your machine
# set up environment variable $WORKFLOW_HOME=/path/to/your/oodt/workflow

usage()
{
    echo "Usage: sh batch_caspge.sh [-i] [-k] [-v]"
    echo "-i input"
    echo "    Input directory"
    echo "-k key"
    echo "    Metadata key"
    echo "-v value"
    echo "    Metadata value"
}

if [ "$#" -ne 6 ]
then
    printf "ERROR. Incorrect number of parameters\n"
    usage
    exit
fi

if [ "$1" != "-i" ] || [ "$3" != "-k" ] || [ "$5" != "-v" ]
then
    printf "ERROR. The following parameter(s) are missing: "
    if [ "$1" != "-i" ]; then printf "'input' "; fi
    if [ "$3" != "-k" ]; then printf "'key' "; fi
    if [ "$5" != "-v" ]; then printf "'value' "; fi
    printf "%s\n"
    usage
    exit
fi

if [ "$2" == "" ]
    then
    printf "ERROR. The input folder not identified\n"
    usage
    exit
fi

count=1
printf "Counting files. Please wait..."
TotalFiles=$(find $2 -type f -name "*.json" | wc -l | tr -d ' ')
sleep 1
printf "\r$TotalFiles matching files found. Start indexing\n\n"

cd $WORKFLOW_HOME/bin
declare -a files=($2/*.json)
for (( i = 0; i < ${#files[*]}; ++ i ))
do
    CurrFile=$(basename "${files[$i]}")
    NextFile=$(basename "${files[$i+1]}")

    ./wmgr-client --url http://localhost:9001 --operation --sendEvent --eventName fileconcatenator-pge --metaData --key $4 $6

    if [ "$NextFile" != "" ]
    then
        count=$((count+1))
        printf "Processing file:"
        printf "%s\r" " $NextFile ($count of $TotalFiles)                                              "
        sleep 2.
    fi
done
printf "\nTotal number of files produced: $count\n"