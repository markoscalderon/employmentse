#!/bin/bash
usage()
{
    echo "Usage: sh crawler.sh [-d dataset]"
    echo "-d dataset"
    echo "    Directory name containing the *.tsv files"
}

if [ "$#" -ne 2 ]
then
    echo "Error: Incorrect number of parameters"
    usage
    exit
fi

if [ "$1" != "-d" ]
then
    usage
    exit
fi

if [ -d "etllib-json-files" ]
then
    if [ "$(ls -A etllib-json-files/)" ]
    then
    rm -r etllib-json-files/*
    fi
else
    mkdir etllib-json-files
fi

count=0
printf "Processing, please wait...\n\n"

for filename in $2/*.tsv
do
    count=$((count+1))
    outfilename=$(basename "$filename")
    jsonfile="${outfilename%.*}"
    /Users/liferayhr/Documents/repositories/etllib/bin/tsvtojson -t $filename -j etllib-json-files/$jsonfile.json -c colheaders.txt -o employment -e encoding.txt
    echo "$count : $jsonfile created."
done

printf "Total $count JSON files produced.\n\n"
echo "JSON files stored in etllib-json-files"
