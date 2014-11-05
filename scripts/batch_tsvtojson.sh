#!/bin/bash
# put this script in the etllib/bin folder
# make sure the folder contains the following files: "tsvtojson", "colheaders.txt", "encoding.txt"
# use the following command: time sh batch_tsvtojson.sh -d [path/to/input/folder] -o [path/to/output/folder]

usage()
{
    echo "Usage: sh batch_tsvtojson.sh [-d] [-o]"
    echo "-d dataset"
    echo "    Directory containing tsv files"
    echo "-o output"
    echo "    Directory to deploy json files"
}

if [ "$#" -ne 4 ]
then
    echo "ERROR. Incorrect number of parameters"
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
    rm -r $4/* 2> /dev/null
    fi
fi

count=0
printf "Processing, please wait...\n\n"
for input_file in $2/*.tsv
do
    count=$((count+1))
    output_file=$(basename "$input_file")
    output_file="${output_file%.*}.json"
    #extension="${output_file##*.}"

    tsvtojson -t $input_file -j $4/$output_file -c colheaders.txt -o employment -e encoding.txt
    echo "$count : $output_file created."
done
printf "Total $count JSON files produced.\n\n"