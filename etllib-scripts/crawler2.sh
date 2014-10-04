#!/bin/bash
usage()
{
    echo "Usage: sh test.sh [-d dataset] [-o output]"
    echo "-d dataset"
    echo "    Directory name containing the *.tsv files"
    echo "-o output"
    echo "    Directory name where the json files will be created"
}

if [ "$#" -ne 4 ]
then
    echo "Error: Incorrect number of parameters"
    usage
    exit
fi

if [ "$1" != "-d" ] || [ "$3" != "-o" ]
then
    usage
    exit
fi

if [ -d "$4" ]
then
    if [ "$(ls -A $4/)" ]
    then
    rm -r $4/*
    fi
fi

count=0
printf "Processing, please wait...\n\n"

for filename in $2/*.tsv
do
    count=$((count+1))
    jsonfile=${filename:6:24}
    tsvtojson -t $filename -j $4/$jsonfile.json -c colheaders.txt -o employment -e encoding.txt
    echo "$count : $jsonfile created."
done

printf "Total $count JSON files produced.\n\n"
